package com.example.data.model

enum class VolunteerStatus {
    PENDING,
    VERIFIED,
    REJECTED,
    SUSPENDED
}

enum class MissionState {
    IDLE,
    DISPATCHED,
    EN_ROUTE,
    ON_SCENE,
    COMPLETED
}

data class Volunteer(
    val id: String,
    val userId: String,
    val name: String,
    val phone: String,
    val email: String,
    val verificationStatus: VolunteerStatus = VolunteerStatus.PENDING,
    val backgroundCheckPassed: Boolean = false,
    val verificationDocumentRef: String = "ID_DOCUMENT_UPLOADED.pdf",
    val certifications: List<String> = listOf("First Aid Certified", "CPR Trained"),
    val isAvailable: Boolean = true,
    val currentLat: Double = 28.6210,
    val currentLng: Double = 77.2150,
    val activeEmergencyId: String? = null,
    val missionState: MissionState = MissionState.IDLE,
    val totalMissionsCompleted: Int = 12,
    val rating: Float = 4.9f
)
