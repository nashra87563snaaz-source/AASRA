package com.example

import android.app.Application
import com.example.data.local.AasraDatabase
import com.example.data.repository.AasraRepository

class AasraApplication : Application() {

    lateinit var database: AasraDatabase
        private set

    lateinit var repository: AasraRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AasraDatabase.getDatabase(this)
        repository = AasraRepository(
            userDao = database.userDao(),
            contactDao = database.emergencyContactDao(),
            eventDao = database.emergencyEventDao(),
            bandDao = database.safetyBandDao(),
            volunteerDao = database.volunteerDao(),
            audioDao = database.audioRecordDao(),
            auditDao = database.auditLogDao()
        )
    }

    companion object {
        lateinit var instance: AasraApplication
            private set
    }
}
