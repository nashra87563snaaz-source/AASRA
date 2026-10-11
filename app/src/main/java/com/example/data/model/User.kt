package com.example.data.model

enum class UserRole {
    CITIZEN,
    VOLUNTEER,
    ADMIN
}

data class MedicalInfo(
    val bloodGroup: String = "",
    val allergies: String = "",
    val medications: String = "",
    val chronicConditions: String = "",
    val emergencyNotes: String = ""
)

data class User(
    val id: String,
    val fullName: String,
    val phone: String,
    val email: String,
    val role: UserRole = UserRole.CITIZEN,
    val isPhoneVerified: Boolean = false,
    val medicalInfo: MedicalInfo = MedicalInfo(),
    val emergencyPin: String = "1234",
    val countdownSeconds: Int = 3,
    val autoAudioRecord: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
