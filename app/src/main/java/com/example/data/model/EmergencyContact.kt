package com.example.data.model

data class EmergencyContact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val relationship: String,
    val isSmsAlertEnabled: Boolean = true,
    val isCallAlertEnabled: Boolean = true,
    val priorityOrder: Int = 1,
    val isVerified: Boolean = true,
    val lastTestTimestamp: Long? = null
)
