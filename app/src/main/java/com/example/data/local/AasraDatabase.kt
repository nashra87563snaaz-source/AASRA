package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        EmergencyContactEntity::class,
        EmergencyEventEntity::class,
        SafetyBandEntity::class,
        VolunteerEntity::class,
        AudioRecordEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AasraDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun emergencyEventDao(): EmergencyEventDao
    abstract fun safetyBandDao(): SafetyBandDao
    abstract fun volunteerDao(): VolunteerDao
    abstract fun audioRecordDao(): AudioRecordDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: AasraDatabase? = null

        fun getDatabase(context: Context): AasraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AasraDatabase::class.java,
                    "aasra_safety_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Seed default emergency contacts, safety band, and initial user on first run
            CoroutineScope(Dispatchers.IO).launch {
                val database = getDatabase(context)

                // Seed Default User
                database.userDao().insertOrUpdate(
                    UserEntity(
                        id = "usr_aasra_prime",
                        fullName = "Nashra Naaz",
                        phone = "+91 98765 43210",
                        email = "nashra.naaz@aasrasafety.org",
                        role = "CITIZEN",
                        isPhoneVerified = true,
                        bloodGroup = "O+",
                        allergies = "Penicillin, Sulfa drugs",
                        medications = "Inhaler as needed",
                        chronicConditions = "Asthma (Mild)",
                        emergencyNotes = "Always carry inhaler in handbag. Emergency contact: Mother & Police.",
                        emergencyPin = "2580",
                        countdownSeconds = 3,
                        autoAudioRecord = true,
                        createdAt = System.currentTimeMillis()
                    )
                )

                // Seed Emergency Contacts
                database.emergencyContactDao().insertContact(
                    EmergencyContactEntity(
                        id = "ctc_01",
                        name = "Mother (Fatima Naaz)",
                        phoneNumber = "+91 98765 11111",
                        relationship = "Mother",
                        isSmsAlertEnabled = true,
                        isCallAlertEnabled = true,
                        priorityOrder = 1,
                        isVerified = true,
                        lastTestTimestamp = System.currentTimeMillis() - 86400000L
                    )
                )
                database.emergencyContactDao().insertContact(
                    EmergencyContactEntity(
                        id = "ctc_02",
                        name = "Sister (Ayesha Naaz)",
                        phoneNumber = "+91 98765 22222",
                        relationship = "Sister",
                        isSmsAlertEnabled = true,
                        isCallAlertEnabled = true,
                        priorityOrder = 2,
                        isVerified = true,
                        lastTestTimestamp = System.currentTimeMillis() - 172800000L
                    )
                )
                database.emergencyContactDao().insertContact(
                    EmergencyContactEntity(
                        id = "ctc_03",
                        name = "Local Police Helpline 112",
                        phoneNumber = "112",
                        relationship = "Emergency Services",
                        isSmsAlertEnabled = true,
                        isCallAlertEnabled = false,
                        priorityOrder = 3,
                        isVerified = true,
                        lastTestTimestamp = null
                    )
                )

                // Seed Initial Connected Safety Band
                database.safetyBandDao().insertOrUpdate(
                    SafetyBandEntity(
                        deviceId = "AASRA-BAND-ESP32-01",
                        deviceName = "AASRA Smart Safety Band",
                        macAddress = "24:6F:28:B4:9C:12",
                        isConnected = true,
                        connectionType = "BLE 5.0 (ESP32)",
                        rssi = -58,
                        batteryPercent = 88,
                        isCharging = false,
                        gsmSignalBars = 4,
                        gsmNetworkName = "Airtel 2G GSM (SIM800L)",
                        isGsmRegistered = true,
                        gpsSatellites = 8,
                        hasGpsFix = true,
                        lastBandLat = 28.6139,
                        lastBandLng = 77.2090,
                        buzzerActive = false,
                        ledActive = true,
                        firmwareVersion = "v2.4.1-AASRA-ESP32",
                        lastHeartbeatTimestamp = System.currentTimeMillis()
                    )
                )

                // Seed Volunteer Community
                database.volunteerDao().insertVolunteer(
                    VolunteerEntity(
                        id = "vol_01",
                        userId = "usr_vol_rahul",
                        name = "Rahul Sharma",
                        phone = "+91 99887 76655",
                        email = "rahul.safety@gmail.com",
                        verificationStatus = "VERIFIED",
                        backgroundCheckPassed = true,
                        verificationDocumentRef = "GOVT_ID_POLICE_CLEARANCE_VERIFIED.pdf",
                        certifications = listOf("Red Cross First Responder", "CPR Certified"),
                        isAvailable = true,
                        currentLat = 28.6185,
                        currentLng = 77.2140,
                        activeEmergencyId = null,
                        missionState = "IDLE",
                        totalMissionsCompleted = 14,
                        rating = 4.95f
                    )
                )
                database.volunteerDao().insertVolunteer(
                    VolunteerEntity(
                        id = "vol_02",
                        userId = "usr_vol_priya",
                        name = "Dr. Priya Sen",
                        phone = "+91 98112 34455",
                        email = "dr.priya.emergency@delhihealth.in",
                        verificationStatus = "VERIFIED",
                        backgroundCheckPassed = true,
                        verificationDocumentRef = "MEDICAL_COUNCIL_REGISTRATION.pdf",
                        certifications = listOf("Emergency Medical Officer", "Trauma Care Specialist"),
                        isAvailable = true,
                        currentLat = 28.6250,
                        currentLng = 77.2010,
                        activeEmergencyId = null,
                        missionState = "IDLE",
                        totalMissionsCompleted = 29,
                        rating = 5.0f
                    )
                )
                database.volunteerDao().insertVolunteer(
                    VolunteerEntity(
                        id = "vol_03",
                        userId = "usr_vol_vikram",
                        name = "Vikram Malik",
                        phone = "+91 97665 44332",
                        email = "vikram.m@ngo.org",
                        verificationStatus = "PENDING",
                        backgroundCheckPassed = false,
                        verificationDocumentRef = "COMMUNITY_VOLUNTEER_APPLICATION.pdf",
                        certifications = listOf("Community Watch Leader"),
                        isAvailable = false,
                        currentLat = 28.6300,
                        currentLng = 77.2200,
                        activeEmergencyId = null,
                        missionState = "IDLE",
                        totalMissionsCompleted = 0,
                        rating = 0.0f
                    )
                )

                // Seed Initial Audit Log
                database.auditLogDao().insertLog(
                    AuditLogEntity(
                        id = "audit_init",
                        timestamp = System.currentTimeMillis(),
                        actor = "SYSTEM",
                        action = "SYSTEM_INITIALIZED",
                        details = "AASRA Safety Framework v2.4 initialized with secure local sandbox, encrypted Room DB, and physical band BLE interface.",
                        severity = "INFO"
                    )
                )
            }
        }
    }
}
