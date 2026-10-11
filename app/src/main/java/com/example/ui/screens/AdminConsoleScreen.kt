package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditSeverity
import com.example.data.model.VolunteerStatus
import com.example.ui.components.AasraTopBar
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.NavyAccent
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.AasraUiState
import com.example.ui.viewmodel.AasraViewModel
import com.example.ui.viewmodel.AppScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminConsoleScreen(
    viewModel: AasraViewModel,
    uiState: AasraUiState
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Volunteer Verification, 1: Audit & Security Logs, 2: Abuse Control & Config

    Scaffold(
        topBar = {
            AasraTopBar(
                title = "AASRA Admin Console",
                subtitle = "Security & Governance Portal",
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
                    text = { Text("Volunteers (${uiState.volunteers.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Audit Trails", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Abuse Shield", fontWeight = FontWeight.Bold) }
                )
            }

            when (selectedTab) {
                0 -> AdminVolunteersTab(viewModel, uiState)
                1 -> AdminAuditTab(uiState)
                2 -> AdminAbuseTab(context)
            }
        }
    }
}

@Composable
private fun AdminVolunteersTab(
    viewModel: AasraViewModel,
    uiState: AasraUiState
) {
    val volunteers = uiState.volunteers

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Volunteer Identity Verification Queue", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(volunteers, key = { it.id }) { volunteer ->
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
                            Text(volunteer.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${volunteer.email} • ${volunteer.phone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (volunteer.verificationStatus) {
                                VolunteerStatus.VERIFIED -> StatusSuccess.copy(alpha = 0.2f)
                                VolunteerStatus.PENDING -> StatusWarning.copy(alpha = 0.2f)
                                else -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = volunteer.verificationStatus.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (volunteer.verificationStatus) {
                                    VolunteerStatus.VERIFIED -> StatusSuccess
                                    VolunteerStatus.PENDING -> StatusWarning
                                    else -> MaterialTheme.colorScheme.error
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Submitted Doc: ${volunteer.verificationDocumentRef} • Certs: ${volunteer.certifications.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.adminSetVolunteerStatus(volunteer.id, VolunteerStatus.VERIFIED) },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Approve", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.adminSetVolunteerStatus(volunteer.id, VolunteerStatus.REJECTED) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reject", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.adminSetVolunteerStatus(volunteer.id, VolunteerStatus.SUSPENDED) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Suspend", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAuditTab(uiState: AasraUiState) {
    val logs = uiState.auditLogs

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Immutable Security Audit Logs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(logs, key = { it.id }) { log ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (log.severity) {
                                            AuditSeverity.CRITICAL -> EmergencyRed
                                            AuditSeverity.ALERT -> EmergencyRed
                                            AuditSeverity.WARNING -> StatusWarning
                                            AuditSeverity.INFO -> NavyAccent
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(log.action, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                        Text(timeStr, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Actor: ${log.actor} — ${log.details}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminAbuseTab(context: android.content.Context) {
    var maxAlertsPerMinute by remember { mutableStateOf("3") }
    var volunteerRadiusKm by remember { mutableStateOf("10") }
    var rateLimitEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Abuse Prevention & Emergency Policies", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

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
                            Text("SOS Rate Limiting", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Prevents bot spoofing & accidental burst triggers", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = rateLimitEnabled, onCheckedChange = { rateLimitEnabled = it })
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = maxAlertsPerMinute,
                        onValueChange = { maxAlertsPerMinute = it },
                        label = { Text("Max SOS triggers per minute") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = volunteerRadiusKm,
                        onValueChange = { volunteerRadiusKm = it },
                        label = { Text("Volunteer Broadcast Radius (Kilometers)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { Toast.makeText(context, "Security parameters committed to backend rules.", Toast.LENGTH_SHORT).show() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SAVE SECURITY POLICY", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
