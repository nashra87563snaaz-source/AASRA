package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.hardware.SafetyBandManager
import com.example.ui.components.AasraTopBar
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.NavyAccent
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.AasraUiState
import com.example.ui.viewmodel.AasraViewModel
import com.example.ui.viewmodel.AppScreen

@Composable
fun HardwareBandScreen(
    viewModel: AasraViewModel,
    uiState: AasraUiState
) {
    val context = LocalContext.current
    val band = uiState.band
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Controls & Telemetry, 1: Hardware Specs & Schematics, 2: ESP32 Firmware C++

    Scaffold(
        topBar = {
            AasraTopBar(
                title = "AASRA Safety Band Hub",
                subtitle = "ESP32 • SIM800L 2G • NEO-6M GPS",
                onBackClick = { viewModel.navigateTo(AppScreen.Dashboard) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Controls & Telemetry", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Circuit & Wiring", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("ESP32 Firmware", fontWeight = FontWeight.Bold) }
                )
            }

            when (selectedTab) {
                0 -> BandControlsTab(viewModel, uiState)
                1 -> BandHardwareSpecsTab()
                2 -> BandFirmwareTab(context)
            }
        }
    }
}

@Composable
private fun BandControlsTab(
    viewModel: AasraViewModel,
    uiState: AasraUiState
) {
    val band = uiState.band

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Device Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(band.deviceName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("MAC: ${band.macAddress} • ${band.firmwareVersion}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = { viewModel.toggleBandConnection() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (band.isConnected) StatusSuccess else MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (band.isConnected) "CONNECTED" else "CONNECT", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4 Telemetry Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TelemetryTile("Battery", "${band.batteryPercent}%", Icons.Default.BatteryChargingFull, StatusSuccess)
                        TelemetryTile("BLE RSSI", "${band.rssi} dBm", Icons.Default.Bluetooth, NavyAccent)
                        TelemetryTile("2G GSM", "${band.gsmSignalBars}/5 Bars", Icons.Default.SignalCellularAlt, StatusWarning)
                        TelemetryTile("NEO-6M GPS", "${band.gpsSatellites} Sats", Icons.Default.GpsFixed, StatusSuccess)
                    }
                }
            }
        }

        // Live Diagnostic Test Buttons
        item {
            Text("Hardware Actuator Testing", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.toggleBandBuzzer() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (band.buzzerActive) EmergencyRed else MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (band.buzzerActive) "Mute Siren" else "Test Siren (85dB)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { viewModel.toggleBandLed() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (band.ledActive) StatusSuccess else MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (band.ledActive) "LED Strobe ON" else "LED OFF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Button(
                onClick = { viewModel.triggerBandPhysicalSos() },
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("simulate_hardware_button_sos")
            ) {
                Icon(Icons.Default.TouchApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SIMULATE PHYSICAL BAND BUTTON SOS", fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
        }

        // Live BLE & GSM Log Feed
        item {
            Text("Live BLE GATT Telemetry Log", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    uiState.bandLogs.takeLast(6).forEach { logLine ->
                        Text(
                            text = logLine,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryTile(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun BandHardwareSpecsTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Physical Prototype Bill of Materials (BOM)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• ESP32 DevKit V1 (Main microcontroller with BLE 5.0 & dual-core 240MHz)\n• SIM800L GSM/GPRS Module (Dedicated 2G SMS dispatch & emergency phone calling)\n• NEO-6M GPS Module with ceramic patch antenna (NMEA GPS fix)\n• 3.7V 1200mAh LiPo Rechargeable Battery\n• TP4056 USB-C Li-ion Charging Module with battery protection IC\n• MT3608 DC-DC Step-Up / Buck Converter (Calibrated strictly to 4.0V for SIM800L)\n• Tactile Momentary SOS Push Button (connected to GPIO4 with internal pull-up)\n• 85dB Active Piezo Buzzer (GPIO18) & Ultra-Bright Amber Emergency Strobe LED (GPIO19)",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmergencyRed.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = EmergencyRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Critical Hardware Power Safety Notice", fontWeight = FontWeight.Bold, color = EmergencyRed)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1. SIM800L requires 3.7V to 4.2V and draws up to 2.0A peak current during RF transmission bursts. NEVER connect SIM800L VCC to the ESP32's 3.3V or 5V rail—doing so will cause brownout resets.\n2. Power the SIM800L directly from the 3.7V LiPo battery via the TP4056 output with a 1000µF low-ESR decoupling capacitor.\n3. GSM Network Availability: Confirm 2G GSM support with your local cellular carrier before standalone operation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Standalone Disconnected Fallback Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "When the user is away from their smartphone or the phone's battery is depleted, the ESP32 firmware detects BLE disconnection and switches to Standalone 2G Mode. Upon SOS button press, it directly reads NEO-6M NMEA GPS sentences and commands the SIM800L module using AT commands (AT+CMGS) to send SMS alerts directly to EEPROM-stored emergency numbers.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun BandFirmwareTab(context: Context) {
    val firmware = SafetyBandManager.ARDUINO_FIRMWARE_CODE
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ESP32 Arduino C++ Firmware", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("AASRA ESP32 Firmware", firmware))
                        Toast.makeText(context, "Firmware code copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Code", fontSize = 12.sp)
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = firmware,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier
                        .padding(12.dp)
                        .horizontalScroll(rememberScrollState())
                )
            }
        }
    }
}
