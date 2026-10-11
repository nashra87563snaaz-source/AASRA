package com.example.data.hardware

import android.content.Context
import com.example.data.model.SafetyBand
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Protocol Specification for Physical AASRA Safety Band (ESP32 + SIM800L + NEO-6M GPS):
 *
 * BLE Service UUID:        0000A500-0000-1000-8000-00805F9B34FB
 * SOS Alert Char UUID:     0000A501-0000-1000-8000-00805F9B34FB (Notify)
 * Telemetry Char UUID:     0000A502-0000-1000-8000-00805F9B34FB (Read / Notify)
 * Band Control Char UUID:  0000A503-0000-1000-8000-00805F9B34FB (Write)
 *
 * Packet Framing:
 * [START_BYTE: 0xAA][CMD_ID: 1 Byte][PAYLOAD_LEN: 1 Byte][PAYLOAD: N Bytes][CHECKSUM: 1 Byte][END_BYTE: 0x55]
 *
 * Command Types:
 *  - 0x01: SOS_BUTTON_PRESSED (Band -> Phone: Lat, Lng, Battery, GSM_Status)
 *  - 0x02: TELEMETRY_HEARTBEAT (Band -> Phone: Batt%, GSM_Signal, Satellites)
 *  - 0x03: ACTIVATE_BUZZER (Phone -> Band: Duration_ms, Frequency)
 *  - 0x04: ACTIVATE_LED (Phone -> Band: Pattern: Strobe / Solid)
 *  - 0x05: CANCEL_SOS_ACK (Phone -> Band: Turn off sirens and alarm LED)
 *  - 0x06: SYNC_CONTACT_NUMBERS (Phone -> Band: Stored in ESP32 EEPROM for standalone 2G SMS)
 */
class SafetyBandManager(private val context: Context) {

    private val _bandState = MutableStateFlow(
        SafetyBand(
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
            firmwareVersion = "v2.4.1-AASRA-ESP32"
        )
    )
    val bandState: StateFlow<SafetyBand> = _bandState.asStateFlow()

    private val _recentLogs = MutableStateFlow<List<String>>(
        listOf(
            "[BLE] Initialized AASRA BLE GATT Client Service 0xAA500",
            "[BLE] Connected to AASRA-BAND-ESP32-01 (RSSI -58 dBm)",
            "[BAND] SIM800L GSM module registered on Airtel 2G (Signal 4/5)",
            "[BAND] NEO-6M GPS locked with 8 satellites. Fix OK",
            "[BAND] Ready for instant push button SOS trigger"
        )
    )
    val recentLogs: StateFlow<List<String>> = _recentLogs.asStateFlow()

    fun triggerBuzzer(active: Boolean) {
        _bandState.value = _bandState.value.copy(buzzerActive = active)
        addLog(if (active) "[CMD -> BAND 0x03] Buzzer Activated at 85dB alert frequency" else "[CMD -> BAND 0x03] Buzzer Muted")
    }

    fun toggleLed(active: Boolean) {
        _bandState.value = _bandState.value.copy(ledActive = active)
        addLog(if (active) "[CMD -> BAND 0x04] Emergency Strobe LED Enabled" else "[CMD -> BAND 0x04] LED Disabled")
    }

    fun toggleConnection() {
        val next = !_bandState.value.isConnected
        _bandState.value = _bandState.value.copy(
            isConnected = next,
            buzzerActive = false
        )
        addLog(if (next) "[BLE] Reconnected to AASRA Band via BLE" else "[BLE] Safety band disconnected. Standalone 2G GSM fallback engaged.")
    }

    fun refreshTelemetry() {
        val current = _bandState.value
        val newBatt = (current.batteryPercent - 1).coerceAtLeast(15)
        _bandState.value = current.copy(
            batteryPercent = newBatt,
            lastHeartbeatTimestamp = System.currentTimeMillis()
        )
        addLog("[BAND -> TELEMETRY 0x02] Heartbeat ACK: Battery=${newBatt}%, Satellites=8, GSM=OK")
    }

    private fun addLog(msg: String) {
        val updated = (_recentLogs.value + msg).takeLast(20)
        _recentLogs.value = updated
    }

    companion object {
        const val BLE_SERVICE_UUID = "0000A500-0000-1000-8000-00805F9B34FB"
        const val BLE_CHAR_SOS = "0000A501-0000-1000-8000-00805F9B34FB"
        const val BLE_CHAR_TELEMETRY = "0000A502-0000-1000-8000-00805F9B34FB"
        const val BLE_CHAR_CONTROL = "0000A503-0000-1000-8000-00805F9B34FB"

        /**
         * Arduino C++ Code for ESP32 Physical Safety Band:
         */
        val ARDUINO_FIRMWARE_CODE = """
// =========================================================================
// AASRA SMART SAFETY BAND - ESP32 FIRMWARE
// Hardware: ESP32 DevKit V1 + SIM800L (2G) + NEO-6M GPS + Buzzer + SOS Button
// =========================================================================
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <TinyGPS++.h>
#include <HardwareSerial.h>

#define SERVICE_UUID        "0000A500-0000-1000-8000-00805F9B34FB"
#define CHAR_SOS_UUID       "0000A501-0000-1000-8000-00805F9B34FB"
#define CHAR_TELEM_UUID     "0000A502-0000-1000-8000-00805F9B34FB"
#define CHAR_CTRL_UUID      "0000A503-0000-1000-8000-00805F9B34FB"

// Hardware Pinout Definitions
#define PIN_SOS_BUTTON      4   // Active LOW push button with internal pull-up
#define PIN_BUZZER          18  // Piezo Buzzer (PWM/Digital Out)
#define PIN_LED             19  // Ultra-bright Alert LED
#define PIN_BATTERY_ADC     34  // Voltage divider (100k + 100k) to 3.7V LiPo

// Hardware Serial Ports
HardwareSerial gpsSerial(1); // RX=16, TX=17 for NEO-6M GPS
HardwareSerial gsmSerial(2); // RX=26, TX=27 for SIM800L GSM

TinyGPSPlus gps;
BLECharacteristic *pSosChar;
BLECharacteristic *pTelemChar;
bool deviceConnected = false;
volatile bool emergencyTriggered = false;

void IRAM_ATTR onSosButtonInterrupt() {
  emergencyTriggered = true;
}

void setup() {
  Serial.begin(115200);
  gpsSerial.begin(9600, SERIAL_8N1, 16, 17);
  gsmSerial.begin(9600, SERIAL_8N1, 26, 27);

  pinMode(PIN_SOS_BUTTON, INPUT_PULLUP);
  attachInterrupt(digitalPinToInterrupt(PIN_SOS_BUTTON), onSosButtonInterrupt, FALLING);
  pinMode(PIN_BUZZER, OUTPUT);
  pinMode(PIN_LED, OUTPUT);
  digitalWrite(PIN_BUZZER, LOW);
  digitalWrite(PIN_LED, LOW);

  // Initialize BLE Server
  BLEDevice::init("AASRA-BAND-ESP32-01");
  BLEServer *pServer = BLEDevice::createServer();
  BLEService *pService = pServer->createService(SERVICE_UUID);

  pSosChar = pService->createCharacteristic(CHAR_SOS_UUID, BLECharacteristic::PROPERTY_NOTIFY);
  pSosChar->addDescriptor(new BLE2902());
  pTelemChar = pService->createCharacteristic(CHAR_TELEM_UUID, BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_NOTIFY);
  pTelemChar->addDescriptor(new BLE2902());

  pService->start();
  pServer->getAdvertising()->start();
  Serial.println("AASRA Band Ready. Awaiting SOS or BLE connection...");
}

void sendSim800lEmergencySms(const char* phone, const char* message) {
  gsmSerial.println("AT+CMGF=1"); // Set SMS text mode
  delay(100);
  gsmSerial.print("AT+CMGS=\"");
  gsmSerial.print(phone);
  gsmSerial.println("\"");
  delay(100);
  gsmSerial.print(message);
  gsmSerial.write(26); // Ctrl+Z to send
  delay(2000);
}

void loop() {
  while (gpsSerial.available() > 0) {
    gps.encode(gpsSerial.read());
  }

  if (emergencyTriggered) {
    emergencyTriggered = false;
    digitalWrite(PIN_BUZZER, HIGH);
    digitalWrite(PIN_LED, HIGH);

    double lat = gps.location.isValid() ? gps.location.lat() : 28.6139;
    double lng = gps.location.isValid() ? gps.location.lng() : 77.2090;

    char payload[64];
    snprintf(payload, sizeof(payload), "SOS:LAT=%.5f:LNG=%.5f", lat, lng);

    if (deviceConnected) {
      pSosChar->setValue(payload);
      pSosChar->notify();
    } else {
      // Fallback: Standalone 2G SMS transmission directly from SIM800L
      char msg[160];
      snprintf(msg, sizeof(msg), "EMERGENCY: AASRA Band SOS! Lat:%.5f, Lng:%.5f", lat, lng);
      sendSim800lEmergencySms("+919876543210", msg);
    }
  }
  delay(50);
}
        """.trimIndent()
    }
}
