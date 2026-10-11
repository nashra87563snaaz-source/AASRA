package com.example.data.model

data class SafetyBand(
    val deviceId: String = "AASRA-BAND-ESP32-01",
    val deviceName: String = "AASRA Smart Safety Band",
    val macAddress: String = "24:6F:28:B4:9C:12",
    val isConnected: Boolean = true,
    val connectionType: String = "BLE 5.0 (ESP32)",
    val rssi: Int = -58, // dBm
    val batteryPercent: Int = 86,
    val isCharging: Boolean = false,
    val gsmSignalBars: Int = 4, // 0-5
    val gsmNetworkName: String = "Airtel / Jio 2G GSM (SIM800L)",
    val isGsmRegistered: Boolean = true,
    val gpsSatellites: Int = 7,
    val hasGpsFix: Boolean = true,
    val lastBandLat: Double = 28.6139,
    val lastBandLng: Double = 77.2090,
    val buzzerActive: Boolean = false,
    val ledActive: Boolean = true,
    val firmwareVersion: String = "v2.4.1-AASRA-ESP32",
    val lastHeartbeatTimestamp: Long = System.currentTimeMillis()
)
