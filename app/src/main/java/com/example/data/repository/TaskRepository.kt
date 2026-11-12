package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.InstagramTask
import com.example.data.model.ProofSubmission
import com.example.data.model.ServerConfig
import com.example.data.model.TaskStatus
import com.example.data.model.UploadStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class TaskRepository(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("pr_social_prefs", Context.MODE_PRIVATE)

    private val defaultTasks = listOf(
        InstagramTask(
            id = "task_101",
            targetUsername = "tech_innovate_bd",
            title = "Follow Tech Innovate BD",
            rewardPoints = 60,
            status = TaskStatus.PENDING,
            followersCount = "45.2K",
            instructions = "প্রোফাইল ওপেন করে Follow বাটনে চাপ দিন। অটোমেটিক স্ক্রিনশট হয়ে সার্ভারে আপলোড হবে।"
        ),
        InstagramTask(
            id = "task_102",
            targetUsername = "social_growth_hub",
            title = "Follow Social Growth Hub",
            rewardPoints = 50,
            status = TaskStatus.PENDING,
            followersCount = "128K",
            instructions = "আইডি ফলো করে ইনস্ট্যান্ট স্ক্রিনশট ও সাবমিশন নিন।"
        ),
        InstagramTask(
            id = "task_103",
            targetUsername = "creative_vibes_zone",
            title = "Follow Creative Vibes Zone",
            rewardPoints = 75,
            status = TaskStatus.PENDING,
            followersCount = "89.5K",
            instructions = "অটো মেশিনের ফলো ডিটেকশন স্বয়ংক্রিয়ভাবে স্ক্রিনশট জমা দিবে।"
        ),
        InstagramTask(
            id = "task_104",
            targetUsername = "lifestyle_bangla_mag",
            title = "Follow Lifestyle Bangla Mag",
            rewardPoints = 40,
            status = TaskStatus.PENDING,
            followersCount = "33.1K",
            instructions = "ফলো সম্পন্ন হলে সাথে সাথে প্রুফ সার্ভারে পোস্ট হবে।"
        )
    )

    private val _tasks = MutableStateFlow<List<InstagramTask>>(defaultTasks)
    val tasks: StateFlow<List<InstagramTask>> = _tasks.asStateFlow()

    private val _submissions = MutableStateFlow<List<ProofSubmission>>(emptyList())
    val submissions: StateFlow<List<ProofSubmission>> = _submissions.asStateFlow()

    private val _serverConfig = MutableStateFlow(loadServerConfig())
    val serverConfig: StateFlow<ServerConfig> = _serverConfig.asStateFlow()

    private fun loadServerConfig(): ServerConfig {
        return ServerConfig(
            endpointUrl = prefs.getString("endpoint_url", "https://api.prsocial.com/api/v1/tasks/upload-proof") ?: "https://api.prsocial.com/api/v1/tasks/upload-proof",
            authToken = prefs.getString("auth_token", "pr_worker_token_default_992") ?: "pr_worker_token_default_992",
            autoSubmitEnabled = prefs.getBoolean("auto_submit", true),
            autoScreenshotEnabled = prefs.getBoolean("auto_screenshot", true),
            screenshotDelayMs = prefs.getLong("delay_ms", 1000L),
            autoNextTask = prefs.getBoolean("auto_next", true)
        )
    }

    fun updateServerConfig(config: ServerConfig) {
        prefs.edit().apply {
            putString("endpoint_url", config.endpointUrl)
            putString("auth_token", config.authToken)
            putBoolean("auto_submit", config.autoSubmitEnabled)
            putBoolean("auto_screenshot", config.autoScreenshotEnabled)
            putLong("delay_ms", config.screenshotDelayMs)
            putBoolean("auto_next", config.autoNextTask)
            apply()
        }
        _serverConfig.value = config
    }

    fun addTask(username: String, points: Int = 50) {
        val cleanUsername = username.trim().removePrefix("@")
        val newTask = InstagramTask(
            id = "task_" + UUID.randomUUID().toString().take(8),
            targetUsername = cleanUsername,
            title = "Follow @$cleanUsername",
            rewardPoints = points,
            status = TaskStatus.PENDING,
            followersCount = "New Campaign"
        )
        _tasks.value = listOf(newTask) + _tasks.value
    }

    fun updateTaskStatus(taskId: String, status: TaskStatus) {
        _tasks.value = _tasks.value.map {
            if (it.id == taskId) it.copy(status = status) else it
        }
    }

    fun addOrUpdateSubmission(submission: ProofSubmission) {
        val existingIndex = _submissions.value.indexOfFirst { it.id == submission.id }
        if (existingIndex >= 0) {
            val updated = _submissions.value.toMutableList()
            updated[existingIndex] = submission
            _submissions.value = updated
        } else {
            _submissions.value = listOf(submission) + _submissions.value
        }

        if (submission.status == UploadStatus.SUBMITTED) {
            updateTaskStatus(submission.taskId, TaskStatus.SUBMITTED)
        }
    }

    fun getTaskById(taskId: String): InstagramTask? {
        return _tasks.value.find { it.id == taskId }
    }
}
