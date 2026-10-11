package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AasraRepository(
    private val userDao: UserDao,
    private val contactDao: EmergencyContactDao,
    private val eventDao: EmergencyEventDao,
    private val bandDao: SafetyBandDao,
    private val volunteerDao: VolunteerDao,
    private val audioDao: AudioRecordDao,
    private val auditDao: AuditLogDao
) {

    // User Operations
    fun getCurrentUserFlow(): Flow<User?> = userDao.getCurrentUserFlow().map { it?.toDomain() }

    suspend fun getCurrentUser(): User? = userDao.getCurrentUser()?.toDomain()

    suspend fun saveUser(user: User) {
        userDao.insertOrUpdate(user.toEntity())
    }

    suspend fun updateMedicalInfo(medicalInfo: MedicalInfo) {
        val current = userDao.getCurrentUser()
        if (current != null) {
            val updated = current.copy(
                bloodGroup = medicalInfo.bloodGroup,
                allergies = medicalInfo.allergies,
                medications = medicalInfo.medications,
                chronicConditions = medicalInfo.chronicConditions,
                emergencyNotes = medicalInfo.emergencyNotes
            )
            userDao.insertOrUpdate(updated)
        }
    }

    suspend fun updateRole(newRole: UserRole) {
        val current = userDao.getCurrentUser()
        if (current != null) {
            userDao.insertOrUpdate(current.copy(role = newRole.name))
        }
    }

    // Emergency Contacts Operations
    fun getAllContactsFlow(): Flow<List<EmergencyContact>> =
        contactDao.getAllContactsFlow().map { list -> list.map { it.toDomain() } }

    suspend fun getAllContacts(): List<EmergencyContact> =
        contactDao.getAllContacts().map { it.toDomain() }

    suspend fun saveContact(contact: EmergencyContact) {
        contactDao.insertContact(contact.toEntity())
    }

    suspend fun deleteContact(contactId: String) {
        contactDao.deleteContactById(contactId)
    }

    // Emergency Events Operations
    fun getAllEventsFlow(): Flow<List<EmergencyEvent>> =
        eventDao.getAllEventsFlow().map { list -> list.map { it.toDomain() } }

    fun getActiveEmergencyFlow(): Flow<EmergencyEvent?> =
        eventDao.getActiveEmergencyFlow().map { it?.toDomain() }

    suspend fun getActiveEmergency(): EmergencyEvent? =
        eventDao.getActiveEmergency()?.toDomain()

    suspend fun saveEmergencyEvent(event: EmergencyEvent) {
        eventDao.insertEvent(event.toEntity())
    }

    suspend fun updateEmergencyEvent(event: EmergencyEvent) {
        eventDao.updateEvent(event.toEntity())
    }

    // Safety Band Operations
    fun getSafetyBandFlow(): Flow<SafetyBand?> =
        bandDao.getBandFlow().map { it?.toDomain() }

    suspend fun saveSafetyBand(band: SafetyBand) {
        bandDao.insertOrUpdate(band.toEntity())
    }

    // Volunteer Operations
    fun getAllVolunteersFlow(): Flow<List<Volunteer>> =
        volunteerDao.getAllVolunteersFlow().map { list -> list.map { it.toDomain() } }

    suspend fun saveVolunteer(volunteer: Volunteer) {
        volunteerDao.insertVolunteer(volunteer.toEntity())
    }

    suspend fun updateVolunteerVerification(volunteerId: String, status: VolunteerStatus) {
        volunteerDao.updateStatus(volunteerId, status.name)
    }

    // Audio Records Operations
    fun getAllAudioRecordsFlow(): Flow<List<AudioRecord>> =
        audioDao.getAllAudioRecordsFlow().map { list -> list.map { it.toDomain() } }

    suspend fun saveAudioRecord(record: AudioRecord) {
        audioDao.insertAudioRecord(record.toEntity())
    }

    // Audit Logs Operations
    fun getAuditLogsFlow(): Flow<List<AuditLog>> =
        auditDao.getAuditLogsFlow().map { list -> list.map { it.toDomain() } }

    suspend fun logAction(actor: String, action: String, details: String, severity: AuditSeverity = AuditSeverity.INFO) {
        auditDao.insertLog(
            AuditLogEntity(
                id = "audit_${System.currentTimeMillis()}",
                timestamp = System.currentTimeMillis(),
                actor = actor,
                action = action,
                details = details,
                severity = severity.name
            )
        )
    }

    // Entity to Domain mappers
    private fun UserEntity.toDomain() = User(
        id = id,
        fullName = fullName,
        phone = phone,
        email = email,
        role = try { UserRole.valueOf(role) } catch (e: Exception) { UserRole.CITIZEN },
        isPhoneVerified = isPhoneVerified,
        medicalInfo = MedicalInfo(
            bloodGroup = bloodGroup,
            allergies = allergies,
            medications = medications,
            chronicConditions = chronicConditions,
            emergencyNotes = emergencyNotes
        ),
        emergencyPin = emergencyPin,
        countdownSeconds = countdownSeconds,
        autoAudioRecord = autoAudioRecord,
        createdAt = createdAt
    )

    private fun User.toEntity() = UserEntity(
        id = id,
        fullName = fullName,
        phone = phone,
        email = email,
        role = role.name,
        isPhoneVerified = isPhoneVerified,
        bloodGroup = medicalInfo.bloodGroup,
        allergies = medicalInfo.allergies,
        medications = medicalInfo.medications,
        chronicConditions = medicalInfo.chronicConditions,
        emergencyNotes = medicalInfo.emergencyNotes,
        emergencyPin = emergencyPin,
        countdownSeconds = countdownSeconds,
        autoAudioRecord = autoAudioRecord,
        createdAt = createdAt
    )

    private fun EmergencyContactEntity.toDomain() = EmergencyContact(
        id = id,
        name = name,
        phoneNumber = phoneNumber,
        relationship = relationship,
        isSmsAlertEnabled = isSmsAlertEnabled,
        isCallAlertEnabled = isCallAlertEnabled,
        priorityOrder = priorityOrder,
        isVerified = isVerified,
        lastTestTimestamp = lastTestTimestamp
    )

    private fun EmergencyContact.toEntity() = EmergencyContactEntity(
        id = id,
        name = name,
        phoneNumber = phoneNumber,
        relationship = relationship,
        isSmsAlertEnabled = isSmsAlertEnabled,
        isCallAlertEnabled = isCallAlertEnabled,
        priorityOrder = priorityOrder,
        isVerified = isVerified,
        lastTestTimestamp = lastTestTimestamp
    )

    private fun EmergencyEventEntity.toDomain() = EmergencyEvent(
        id = id,
        userId = userId,
        userName = userName,
        userPhone = userPhone,
        timestamp = timestamp,
        latitude = latitude,
        longitude = longitude,
        accuracyMeters = accuracyMeters,
        mapsLocationUrl = mapsLocationUrl,
        source = source,
        status = try { EmergencyStatus.valueOf(status) } catch (e: Exception) { EmergencyStatus.PENDING },
        contactReports = contactReports,
        assignedVolunteerId = assignedVolunteerId,
        assignedVolunteerName = assignedVolunteerName,
        volunteerDistanceKm = volunteerDistanceKm,
        audioRecordingId = audioRecordingId,
        audioUploaded = audioUploaded,
        cancellationReason = cancellationReason,
        resolvedTimestamp = resolvedTimestamp,
        lastLocationUpdate = lastLocationUpdate
    )

    private fun EmergencyEvent.toEntity() = EmergencyEventEntity(
        id = id,
        userId = userId,
        userName = userName,
        userPhone = userPhone,
        timestamp = timestamp,
        latitude = latitude,
        longitude = longitude,
        accuracyMeters = accuracyMeters,
        mapsLocationUrl = mapsLocationUrl,
        source = source,
        status = status.name,
        contactReports = contactReports,
        assignedVolunteerId = assignedVolunteerId,
        assignedVolunteerName = assignedVolunteerName,
        volunteerDistanceKm = volunteerDistanceKm,
        audioRecordingId = audioRecordingId,
        audioUploaded = audioUploaded,
        cancellationReason = cancellationReason,
        resolvedTimestamp = resolvedTimestamp,
        lastLocationUpdate = lastLocationUpdate
    )

    private fun SafetyBandEntity.toDomain() = SafetyBand(
        deviceId = deviceId,
        deviceName = deviceName,
        macAddress = macAddress,
        isConnected = isConnected,
        connectionType = connectionType,
        rssi = rssi,
        batteryPercent = batteryPercent,
        isCharging = isCharging,
        gsmSignalBars = gsmSignalBars,
        gsmNetworkName = gsmNetworkName,
        isGsmRegistered = isGsmRegistered,
        gpsSatellites = gpsSatellites,
        hasGpsFix = hasGpsFix,
        lastBandLat = lastBandLat,
        lastBandLng = lastBandLng,
        buzzerActive = buzzerActive,
        ledActive = ledActive,
        firmwareVersion = firmwareVersion,
        lastHeartbeatTimestamp = lastHeartbeatTimestamp
    )

    private fun SafetyBand.toEntity() = SafetyBandEntity(
        deviceId = deviceId,
        deviceName = deviceName,
        macAddress = macAddress,
        isConnected = isConnected,
        connectionType = connectionType,
        rssi = rssi,
        batteryPercent = batteryPercent,
        isCharging = isCharging,
        gsmSignalBars = gsmSignalBars,
        gsmNetworkName = gsmNetworkName,
        isGsmRegistered = isGsmRegistered,
        gpsSatellites = gpsSatellites,
        hasGpsFix = hasGpsFix,
        lastBandLat = lastBandLat,
        lastBandLng = lastBandLng,
        buzzerActive = buzzerActive,
        ledActive = ledActive,
        firmwareVersion = firmwareVersion,
        lastHeartbeatTimestamp = lastHeartbeatTimestamp
    )

    private fun VolunteerEntity.toDomain() = Volunteer(
        id = id,
        userId = userId,
        name = name,
        phone = phone,
        email = email,
        verificationStatus = try { VolunteerStatus.valueOf(verificationStatus) } catch (e: Exception) { VolunteerStatus.PENDING },
        backgroundCheckPassed = backgroundCheckPassed,
        verificationDocumentRef = verificationDocumentRef,
        certifications = certifications,
        isAvailable = isAvailable,
        currentLat = currentLat,
        currentLng = currentLng,
        activeEmergencyId = activeEmergencyId,
        missionState = try { MissionState.valueOf(missionState) } catch (e: Exception) { MissionState.IDLE },
        totalMissionsCompleted = totalMissionsCompleted,
        rating = rating
    )

    private fun Volunteer.toEntity() = VolunteerEntity(
        id = id,
        userId = userId,
        name = name,
        phone = phone,
        email = email,
        verificationStatus = verificationStatus.name,
        backgroundCheckPassed = backgroundCheckPassed,
        verificationDocumentRef = verificationDocumentRef,
        certifications = certifications,
        isAvailable = isAvailable,
        currentLat = currentLat,
        currentLng = currentLng,
        activeEmergencyId = activeEmergencyId,
        missionState = missionState.name,
        totalMissionsCompleted = totalMissionsCompleted,
        rating = rating
    )

    private fun AudioRecordEntity.toDomain() = AudioRecord(
        id = id,
        emergencyId = emergencyId,
        filePath = filePath,
        timestamp = timestamp,
        durationSeconds = durationSeconds,
        uploadStatus = try { UploadStatus.valueOf(uploadStatus) } catch (e: Exception) { UploadStatus.LOCAL_RECORDED },
        fileSizeBytes = fileSizeBytes
    )

    private fun AudioRecord.toEntity() = AudioRecordEntity(
        id = id,
        emergencyId = emergencyId,
        filePath = filePath,
        timestamp = timestamp,
        durationSeconds = durationSeconds,
        uploadStatus = uploadStatus.name,
        fileSizeBytes = fileSizeBytes
    )

    private fun AuditLogEntity.toDomain() = AuditLog(
        id = id,
        timestamp = timestamp,
        actor = actor,
        action = action,
        details = details,
        severity = try { AuditSeverity.valueOf(severity) } catch (e: Exception) { AuditSeverity.INFO }
    )
}
