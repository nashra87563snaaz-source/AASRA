package com.example.data.model

enum class EmergencyStatus {
    PENDING,
    DISPATCHING,
    DELIVERED,
    VOLUNTEER_ASSIGNED,
    RESOLVED,
    CANCELLED
}

enum class DeliveryState {
    PENDING,
    DELIVERED,
    FAILED,
    RETRYING
}

enum class CallState {
    IDLE,
    ATTEMPTED,
    CONNECTED,
    FAILED
}

data class ContactDeliveryReport(
    val contactId: String,
    val contactName: String,
    val phoneNumber: String,
    val smsState: DeliveryState = DeliveryState.PENDING,
    val callState: CallState = CallState.IDLE,
    val errorMessage: String? = null
)

data class EmergencyEvent(
    val id: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float = 5.0f,
    val mapsLocationUrl: String,
    val source: String = "MOBILE_APP", // "MOBILE_APP" or "SAFETY_BAND_ESP32"
    val status: EmergencyStatus = EmergencyStatus.PENDING,
    val contactReports: List<ContactDeliveryReport> = emptyList(),
    val assignedVolunteerId: String? = null,
    val assignedVolunteerName: String? = null,
    val volunteerDistanceKm: Double? = null,
    val audioRecordingId: String? = null,
    val audioUploaded: Boolean = false,
    val cancellationReason: String? = null,
    val resolvedTimestamp: Long? = null,
    val lastLocationUpdate: Long = System.currentTimeMillis()
)
