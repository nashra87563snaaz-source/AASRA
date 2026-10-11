package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CallState
import com.example.data.model.ContactDeliveryReport
import com.example.data.model.DeliveryState
import com.example.data.model.EmergencyEvent
import com.example.data.model.EmergencyStatus
import com.example.data.model.MissionState
import com.example.data.model.UploadStatus
import com.example.data.model.UserRole
import com.example.data.model.VolunteerStatus

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val phone: String,
    val email: String,
    val role: String,
    val isPhoneVerified: Boolean,
    val bloodGroup: String,
    val allergies: String,
    val medications: String,
    val chronicConditions: String,
    val emergencyNotes: String,
    val emergencyPin: String,
    val countdownSeconds: Int,
    val autoAudioRecord: Boolean,
    val createdAt: Long
)

@Entity(tableName = "emergency_contacts")
data class EmergencyContactEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phoneNumber: String,
    val relationship: String,
    val isSmsAlertEnabled: Boolean,
    val isCallAlertEnabled: Boolean,
    val priorityOrder: Int,
    val isVerified: Boolean,
    val lastTestTimestamp: Long?
)

@Entity(tableName = "emergency_events")
data class EmergencyEventEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float,
    val mapsLocationUrl: String,
    val source: String,
    val status: String,
    val contactReports: List<ContactDeliveryReport>,
    val assignedVolunteerId: String?,
    val assignedVolunteerName: String?,
    val volunteerDistanceKm: Double?,
    val audioRecordingId: String?,
    val audioUploaded: Boolean,
    val cancellationReason: String?,
    val resolvedTimestamp: Long?,
    val lastLocationUpdate: Long
)

@Entity(tableName = "safety_bands")
data class SafetyBandEntity(
    @PrimaryKey val deviceId: String,
    val deviceName: String,
    val macAddress: String,
    val isConnected: Boolean,
    val connectionType: String,
    val rssi: Int,
    val batteryPercent: Int,
    val isCharging: Boolean,
    val gsmSignalBars: Int,
    val gsmNetworkName: String,
    val isGsmRegistered: Boolean,
    val gpsSatellites: Int,
    val hasGpsFix: Boolean,
    val lastBandLat: Double,
    val lastBandLng: Double,
    val buzzerActive: Boolean,
    val ledActive: Boolean,
    val firmwareVersion: String,
    val lastHeartbeatTimestamp: Long
)

@Entity(tableName = "volunteers")
data class VolunteerEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val phone: String,
    val email: String,
    val verificationStatus: String,
    val backgroundCheckPassed: Boolean,
    val verificationDocumentRef: String,
    val certifications: List<String>,
    val isAvailable: Boolean,
    val currentLat: Double,
    val currentLng: Double,
    val activeEmergencyId: String?,
    val missionState: String,
    val totalMissionsCompleted: Int,
    val rating: Float
)

@Entity(tableName = "audio_records")
data class AudioRecordEntity(
    @PrimaryKey val id: String,
    val emergencyId: String,
    val filePath: String,
    val timestamp: Long,
    val durationSeconds: Int,
    val uploadStatus: String,
    val fileSizeBytes: Long
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val actor: String,
    val action: String,
    val details: String,
    val severity: String
)
