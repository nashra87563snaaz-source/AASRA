package com.example.data.model

enum class UploadStatus {
    LOCAL_RECORDED,
    UPLOADING,
    CLOUD_SYNCED,
    UPLOAD_FAILED
}

data class AudioRecord(
    val id: String,
    val emergencyId: String,
    val filePath: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 30,
    val uploadStatus: UploadStatus = UploadStatus.LOCAL_RECORDED,
    val fileSizeBytes: Long = 245760L
)

enum class AuditSeverity {
    INFO,
    WARNING,
    ALERT,
    CRITICAL
}

data class AuditLog(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actor: String,
    val action: String,
    val details: String,
    val severity: AuditSeverity = AuditSeverity.INFO
)
