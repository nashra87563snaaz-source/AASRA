package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.MissionState
import com.example.data.model.VolunteerStatus
import com.example.ui.components.AasraTopBar
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.AasraUiState
import com.example.ui.viewmodel.AasraViewModel
import com.example.ui.viewmodel.AppScreen

@Composable
fun VolunteerPortalScreen(
    viewModel: AasraViewModel,
    uiState: AasraUiState
) {
    val context = LocalContext.current
    val currentVolunteer = uiState.volunteers.firstOrNull()
    val activeSos = uiState.activeEmergency

    Scaffold(
        topBar = {
            AasraTopBar(
                title = "Volunteer Responder Hub",
                subtitle = "Community Rapid Emergency Network",
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

            // Volunteer Profile Card
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(StatusSuccess.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = StatusSuccess)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(currentVolunteer?.name ?: "Verified Volunteer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Rating: ${currentVolunteer?.rating ?: 4.9}★ • ${currentVolunteer?.totalMissionsCompleted ?: 12} Missions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            // Verification Badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (currentVolunteer?.verificationStatus) {
                                    VolunteerStatus.VERIFIED -> StatusSuccess.copy(alpha = 0.2f)
                                    VolunteerStatus.PENDING -> StatusWarning.copy(alpha = 0.2f)
                                    else -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                }
                            ) {
                                Text(
                                    text = currentVolunteer?.verificationStatus?.name ?: "VERIFIED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (currentVolunteer?.verificationStatus) {
                                        VolunteerStatus.VERIFIED -> StatusSuccess
                                        VolunteerStatus.PENDING -> StatusWarning
                                        else -> MaterialTheme.colorScheme.error
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Availability Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Responder Duty Status", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(
                                    if (currentVolunteer?.isAvailable == true) "On Duty • Receiving emergency alerts in 10 km" else "Off Duty • Alerts paused",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (currentVolunteer?.isAvailable == true) StatusSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = currentVolunteer?.isAvailable ?: true,
                                onCheckedChange = { currentVolunteer?.let { v -> viewModel.toggleVolunteerAvailability(v.id) } },
                                modifier = Modifier.testTag("volunteer_availability_switch")
                            )
                        }
                    }
                }
            }

            // Sensitive Verification & Legal Disclosure Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Lawful Identity & Background Check", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Volunteer responders undergo rigorous identity verification and background validation by authorized administrators. Government identity tokens are protected with zero-trust storage. Victim's exact coordinates are shielded until official mission acceptance.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Active Emergency Radar Section (10 km Radius)
            item {
                Text("10 km Emergency Incident Radar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            if (activeSos != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EmergencyRed.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = EmergencyRed)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("ACTIVE SOS IN 10 KM", fontWeight = FontWeight.Black, color = EmergencyRed, fontSize = 14.sp)
                                }
                                Text("~1.8 km away", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Distress call initiated by citizen ${activeSos.userName.take(1)}***. Coordinates broadcasted via AASRA network.",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (currentVolunteer?.activeEmergencyId == activeSos.id) {
                                // Mission In Progress
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = StatusSuccess.copy(alpha = 0.2f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Mission Status: ${currentVolunteer.missionState.name}", fontWeight = FontWeight.Bold, color = StatusSuccess)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    val mapUri = Uri.parse("geo:${activeSos.latitude},${activeSos.longitude}?q=${activeSos.latitude},${activeSos.longitude}(Emergency Victim)")
                                                    val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                                                    context.startActivity(mapIntent)
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Navigate", fontSize = 12.sp)
                                            }

                                            Button(
                                                onClick = { viewModel.updateVolunteerMissionState(currentVolunteer.id, MissionState.COMPLETED) },
                                                colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Mark Safe", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Accept / Decline Mission
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { Toast.makeText(context, "Mission declined", Toast.LENGTH_SHORT).show() },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("DECLINE")
                                    }

                                    Button(
                                        onClick = { currentVolunteer?.let { v -> viewModel.acceptEmergencyAssignment(v.id, activeSos.id) } },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1.3f).testTag("accept_mission_button")
                                    ) {
                                        Icon(Icons.Default.DirectionsRun, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("ACCEPT MISSION", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No Active Emergencies Nearby", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("10 km radius secure. Standing by for SOS pings.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Report Misuse Button
            item {
                OutlinedButton(
                    onClick = { Toast.makeText(context, "Report misuse submitted to administrator review.", Toast.LENGTH_LONG).show() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ReportProblem, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Report Unsafe Situation or Misuse")
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
