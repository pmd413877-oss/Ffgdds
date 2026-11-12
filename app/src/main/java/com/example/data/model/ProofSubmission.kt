package com.example.data.model

data class ProofSubmission(
    val id: String,
    val taskId: String,
    val targetUsername: String,
    val screenshotPath: String,
    val timestamp: Long = System.currentTimeMillis(),
    val serverUrl: String,
    val status: UploadStatus = UploadStatus.QUEUED,
    val responseCode: Int = 0,
    val responseBody: String = "",
    val errorMessage: String? = null
)

enum class UploadStatus {
    QUEUED,        // সারিবদ্ধ
    UPLOADING,     // আপলোড হচ্ছে
    SUBMITTED,     // সাবমিট সফল (Success)
    FAILED         // ব্যর্থ (Failed)
}

data class ServerConfig(
    val endpointUrl: String = "https://api.prsocial.com/api/v1/tasks/upload-proof",
    val authToken: String = "pr_worker_token_default_992",
    val autoSubmitEnabled: Boolean = true,
    val autoScreenshotEnabled: Boolean = true,
    val screenshotDelayMs: Long = 1000L,
    val autoNextTask: Boolean = true,
    val customServerHeader: String = "X-PR-Worker-Auth"
)
