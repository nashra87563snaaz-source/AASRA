package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicalInfo
import com.example.data.model.UserRole
import com.example.ui.components.AasraTopBar
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.AasraUiState
import com.example.ui.viewmodel.AasraViewModel
import com.example.ui.viewmodel.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAndProfileScreen(
    viewModel: AasraViewModel,
    uiState: AasraUiState
) {
    val context = LocalContext.current
    val user = uiState.user

    var bloodGroup by remember(user) { mutableStateOf(user?.medicalInfo?.bloodGroup ?: "O+") }
    var allergies by remember(user) { mutableStateOf(user?.medicalInfo?.allergies ?: "Penicillin") }
    var medications by remember(user) { mutableStateOf(user?.medicalInfo?.medications ?: "Asthma Inhaler") }
    var chronicConditions by remember(user) { mutableStateOf(user?.medicalInfo?.chronicConditions ?: "Mild Asthma") }
    var emergencyNotes by remember(user) { mutableStateOf(user?.medicalInfo?.emergencyNotes ?: "Carry inhaler in handbag.") }

    var countdownSec by remember(user) { mutableIntStateOf(user?.countdownSeconds ?: 3) }

    Scaffold(
        topBar = {
            AasraTopBar(
                title = "Profile & Settings",
                subtitle = "User Account & Medical Emergency Vault",
                onBackClick = { viewModel.navigateTo(AppScreen.Dashboard) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // User Identity Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(EmergencyRed.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = EmergencyRed, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(user?.fullName ?: "Citizen", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("${user?.phone ?: ""} • Verified", style = MaterialTheme.typography.bodySmall, color = StatusSuccess)
                                Text(user?.email ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Switch Active Role Mode:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            UserRole.values().forEach { role ->
                                val selected = (user?.role == role)
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.switchRole(role) },
                                    label = { Text(role.name, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Indian Language Selection Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("App Language (भाषा)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(
                                    "${uiState.selectedLanguage.nativeName} (${uiState.selectedLanguage.englishName})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            OutlinedButton(
                                onClick = { viewModel.navigateTo(AppScreen.LanguageSelect) },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Change भाषा")
                            }
                        }
                    }
                }
            }

            // Voluntary Emergency Medical Information Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = EmergencyRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Voluntary Emergency Medical Info", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "Provided voluntarily. Displayed to verified responders during an active emergency to guide trauma treatment.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = bloodGroup,
                            onValueChange = { bloodGroup = it },
                            label = { Text("Blood Group (e.g. O+, A-, B+)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = allergies,
                            onValueChange = { allergies = it },
                            label = { Text("Known Allergies") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = medications,
                            onValueChange = { medications = it },
                            label = { Text("Critical Medications") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = chronicConditions,
                            onValueChange = { chronicConditions = it },
                            label = { Text("Chronic Conditions") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = emergencyNotes,
                            onValueChange = { emergencyNotes = it },
                            label = { Text("Special Medical Instructions") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.saveMedicalInfo(
                                    MedicalInfo(
                                        bloodGroup = bloodGroup,
                                        allergies = allergies,
                                        medications = medications,
                                        chronicConditions = chronicConditions,
                                        emergencyNotes = emergencyNotes
                                    )
                                )
                                Toast.makeText(context, "Medical profile saved securely", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("save_medical_info_button")
                        ) {
                            Text("SAVE MEDICAL INFORMATION", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // SOS Preferences Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Emergency Alert Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("SOS Activation Countdown Delay: ${countdownSec}s", style = MaterialTheme.typography.bodyMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(0, 3, 5).forEach { sec ->
                                FilterChip(
                                    selected = (countdownSec == sec),
                                    onClick = { countdownSec = sec },
                                    label = { Text(if (sec == 0) "Immediate (0s)" else "${sec}s Delay") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Accidental Cancellation PIN: 2580 (or 1234)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Legal & Privacy Policy
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Privacy Policy & Terms of Service", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "AASRA adheres to strict Zero-Trust cybersecurity standards. User location coordinates are only transmitted during explicitly triggered SOS emergencies. Contacts and biometric medical details are saved in the app's encrypted sandbox.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}
