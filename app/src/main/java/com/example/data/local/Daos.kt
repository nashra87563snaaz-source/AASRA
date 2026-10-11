package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun clearUser()
}

@Dao
interface EmergencyContactDao {
    @Query("SELECT * FROM emergency_contacts ORDER BY priorityOrder ASC")
    fun getAllContactsFlow(): Flow<List<EmergencyContactEntity>>

    @Query("SELECT * FROM emergency_contacts ORDER BY priorityOrder ASC")
    suspend fun getAllContacts(): List<EmergencyContactEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContactEntity)

    @Update
    suspend fun updateContact(contact: EmergencyContactEntity)

    @Delete
    suspend fun deleteContact(contact: EmergencyContactEntity)

    @Query("DELETE FROM emergency_contacts WHERE id = :id")
    suspend fun deleteContactById(id: String)
}

@Dao
interface EmergencyEventDao {
    @Query("SELECT * FROM emergency_events ORDER BY timestamp DESC")
    fun getAllEventsFlow(): Flow<List<EmergencyEventEntity>>

    @Query("SELECT * FROM emergency_events WHERE status IN ('PENDING', 'DISPATCHING', 'DELIVERED', 'VOLUNTEER_ASSIGNED') ORDER BY timestamp DESC LIMIT 1")
    fun getActiveEmergencyFlow(): Flow<EmergencyEventEntity?>

    @Query("SELECT * FROM emergency_events WHERE status IN ('PENDING', 'DISPATCHING', 'DELIVERED', 'VOLUNTEER_ASSIGNED') ORDER BY timestamp DESC LIMIT 1")
    suspend fun getActiveEmergency(): EmergencyEventEntity?

    @Query("SELECT * FROM emergency_events WHERE id = :id")
    suspend fun getEventById(id: String): EmergencyEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EmergencyEventEntity)

    @Update
    suspend fun updateEvent(event: EmergencyEventEntity)
}

@Dao
interface SafetyBandDao {
    @Query("SELECT * FROM safety_bands LIMIT 1")
    fun getBandFlow(): Flow<SafetyBandEntity?>

    @Query("SELECT * FROM safety_bands LIMIT 1")
    suspend fun getBand(): SafetyBandEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(band: SafetyBandEntity)
}

@Dao
interface VolunteerDao {
    @Query("SELECT * FROM volunteers")
    fun getAllVolunteersFlow(): Flow<List<VolunteerEntity>>

    @Query("SELECT * FROM volunteers WHERE id = :id")
    suspend fun getVolunteerById(id: String): VolunteerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolunteer(volunteer: VolunteerEntity)

    @Update
    suspend fun updateVolunteer(volunteer: VolunteerEntity)

    @Query("UPDATE volunteers SET verificationStatus = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)
}

@Dao
interface AudioRecordDao {
    @Query("SELECT * FROM audio_records ORDER BY timestamp DESC")
    fun getAllAudioRecordsFlow(): Flow<List<AudioRecordEntity>>

    @Query("SELECT * FROM audio_records WHERE emergencyId = :emergencyId LIMIT 1")
    suspend fun getAudioByEmergencyId(emergencyId: String): AudioRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioRecord(record: AudioRecordEntity)

    @Update
    suspend fun updateAudioRecord(record: AudioRecordEntity)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAuditLogsFlow(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)
}
