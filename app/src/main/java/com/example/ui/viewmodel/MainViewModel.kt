package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.InstagramTask
import com.example.data.model.ProofSubmission
import com.example.data.model.ServerConfig
import com.example.data.model.TaskStatus
import com.example.data.model.UploadStatus
import com.example.data.network.ServerUploadManager
import com.example.data.repository.TaskRepository
import com.example.util.ScreenshotHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

enum class AppScreen {
    PR_DASHBOARD,
    AUTO_MACHINE,
    TASK_LIST,
    HISTORY,
    SETTINGS
}

data class AutomationUiState(
    val currentScreen: AppScreen = AppScreen.PR_DASHBOARD,
    val activeTask: InstagramTask? = null,
    val isAutoMachineActive: Boolean = true,
    val followDetected: Boolean = false,
    val isCapturing: Boolean = false,
    val capturedScreenshotFile: File? = null,
    val capturedBitmap: Bitmap? = null,
    val isUploading: Boolean = false,
    val lastSubmission: ProofSubmission? = null,
    val statusBannerMessage: String? = null,
    val statusBannerIsSuccess: Boolean = true,
    val serverPingLatency: Long? = null,
    val serverPingStatus: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TaskRepository(application)
    private val uploadManager = ServerUploadManager()

    val tasks: StateFlow<List<InstagramTask>> = repository.tasks
    val submissions: StateFlow<List<ProofSubmission>> = repository.submissions
    val serverConfig: StateFlow<ServerConfig> = repository.serverConfig

    private val _uiState = MutableStateFlow(AutomationUiState())
    val uiState: StateFlow<AutomationUiState> = _uiState.asStateFlow()

    init {
        // Set first pending task as active
        viewModelScope.launch {
            tasks.collect { taskList ->
                if (_uiState.value.activeTask == null) {
                    val firstPending = taskList.firstOrNull { it.status == TaskStatus.PENDING } ?: taskList.firstOrNull()
                    _uiState.value = _uiState.value.copy(activeTask = firstPending)
                }
            }
        }
    }

    fun setScreen(screen: AppScreen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun selectTask(task: InstagramTask) {
        _uiState.value = _uiState.value.copy(
            activeTask = task,
            currentScreen = AppScreen.AUTO_MACHINE,
            followDetected = false,
            capturedScreenshotFile = null,
            capturedBitmap = null,
            statusBannerMessage = null
        )
        repository.updateTaskStatus(task.id, TaskStatus.IN_PROGRESS)
    }

    fun onFollowActionDetected(username: String, bitmapProvider: () -> Bitmap?) {
        val currentTask = _uiState.value.activeTask ?: return
        if (_uiState.value.isCapturing || _uiState.value.isUploading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                followDetected = true,
                isCapturing = true,
                statusBannerMessage = "ফলো সনাক্ত হয়েছে! স্ক্রিনশট নেওয়া হচ্ছে...",
                statusBannerIsSuccess = true
            )

            // Wait configured delay for Instagram UI to render the "Following" state
            delay(serverConfig.value.screenshotDelayMs)

            val bitmap = bitmapProvider() ?: ScreenshotHelper.generateSampleProofBitmap(username = currentTask.targetUsername)
            val file = ScreenshotHelper.saveBitmapToFile(getApplication(), bitmap, currentTask.id)

            _uiState.value = _uiState.value.copy(
                isCapturing = false,
                capturedBitmap = bitmap,
                capturedScreenshotFile = file,
                statusBannerMessage = "স্ক্রিনশট সফলভাবে ক্যাপচার হয়েছে!"
            )

            if (serverConfig.value.autoSubmitEnabled) {
                // Auto-upload and submit to server!
                uploadAndSubmitProof(currentTask, file)
            } else {
                _uiState.value = _uiState.value.copy(
                    statusBannerMessage = "স্ক্রিনশট তৈরি! সার্ভারে সাবমিট করতে নিচের বাটনে চাপ দিন।"
                )
            }
        }
    }

    fun triggerManualCapture(bitmapProvider: () -> Bitmap?) {
        val currentTask = _uiState.value.activeTask ?: return
        onFollowActionDetected(currentTask.targetUsername, bitmapProvider)
    }

    fun submitCurrentProof() {
        val currentTask = _uiState.value.activeTask ?: return
        val file = _uiState.value.capturedScreenshotFile ?: return
        uploadAndSubmitProof(currentTask, file)
    }

    private fun uploadAndSubmitProof(task: InstagramTask, file: File) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUploading = true,
                statusBannerMessage = "নির্দিষ্ট সার্ভারে স্ক্রিনশট অটোমেটিক আপলোড হচ্ছে...",
                statusBannerIsSuccess = true
            )

            val submission = ProofSubmission(
                id = "sub_" + UUID.randomUUID().toString().take(8),
                taskId = task.id,
                targetUsername = task.targetUsername,
                screenshotPath = file.absolutePath,
                serverUrl = serverConfig.value.endpointUrl,
                status = UploadStatus.UPLOADING
            )
            repository.addOrUpdateSubmission(submission)

            // Execute upload to user's configured server endpoint
            val result = uploadManager.uploadScreenshotProof(submission, serverConfig.value)
            repository.addOrUpdateSubmission(result)

            if (result.status == UploadStatus.SUBMITTED) {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    lastSubmission = result,
                    statusBannerMessage = "✓ স্ক্রিনশট আপলোড ও সার্ভারে সাবমিট সম্পন্ন! (+${task.rewardPoints} Points)",
                    statusBannerIsSuccess = true
                )

                // If Auto Next Task is enabled, automatically load the next pending task
                if (serverConfig.value.autoNextTask) {
                    delay(2500)
                    advanceToNextTask()
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    lastSubmission = result,
                    statusBannerMessage = result.errorMessage ?: "সার্ভার আপলোড ব্যর্থ হয়েছে। পুনরায় চেষ্টা করুন।",
                    statusBannerIsSuccess = false
                )
            }
        }
    }

    fun handleGalleryScreenshotPicked(uri: Uri) {
        val currentTask = _uiState.value.activeTask ?: return
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                val file = ScreenshotHelper.saveBitmapToFile(context, bitmap, currentTask.id)

                _uiState.value = _uiState.value.copy(
                    capturedBitmap = bitmap,
                    capturedScreenshotFile = file,
                    statusBannerMessage = "গ্যালারি থেকে স্ক্রিনশট যুক্ত হয়েছে। সার্ভারে সাবমিট করতে পারেন।"
                )

                if (serverConfig.value.autoSubmitEnabled) {
                    uploadAndSubmitProof(currentTask, file)
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to load image from URI", e)
                _uiState.value = _uiState.value.copy(
                    statusBannerMessage = "ছবি লোড করতে সমস্যা হয়েছে: ${e.localizedMessage}",
                    statusBannerIsSuccess = false
                )
            }
        }
    }

    fun advanceToNextTask() {
        val taskList = tasks.value
        val next = taskList.firstOrNull { it.status == TaskStatus.PENDING && it.id != _uiState.value.activeTask?.id }
        if (next != null) {
            selectTask(next)
        } else {
            _uiState.value = _uiState.value.copy(
                statusBannerMessage = "সব টাস্ক সম্পন্ন হয়েছে! নতুন টাস্ক যোগ করুন বা রিফ্রেশ করুন।"
            )
        }
    }

    fun addNewTask(username: String, points: Int) {
        repository.addTask(username, points)
    }

    fun updateServerConfig(config: ServerConfig) {
        repository.updateServerConfig(config)
    }

    fun testServerConnection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(serverPingStatus = "কানেকশন টেস্ট করা হচ্ছে...")
            val result = uploadManager.testServerConnection(
                serverConfig.value.endpointUrl,
                serverConfig.value.authToken
            )
            result.onSuccess { (code, latency) ->
                _uiState.value = _uiState.value.copy(
                    serverPingLatency = latency,
                    serverPingStatus = "সফল! রেসপন্স কোড: $code (${latency}ms)"
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    serverPingLatency = null,
                    serverPingStatus = "ব্যর্থ: ${err.localizedMessage ?: "কানেক্ট করা যায়নি"}"
                )
            }
        }
    }

    fun toggleAutoMachine(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isAutoMachineActive = enabled)
    }

    fun clearBanner() {
        _uiState.value = _uiState.value.copy(statusBannerMessage = null)
    }
}
