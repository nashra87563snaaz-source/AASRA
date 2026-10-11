package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IndianLanguage
import com.example.data.model.SafetyBand
import com.example.data.model.UserRole
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.StatusSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AasraTopBar(
    title: String = "AASRA",
    subtitle: String? = "Your Safety, Our Priority",
    currentRole: UserRole = UserRole.CITIZEN,
    safetyBand: SafetyBand? = null,
    selectedLanguage: IndianLanguage? = null,
    onLanguageClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    onBandClick: (() -> Unit)? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("topbar_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmergencyRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "AASRA Shield",
                            tint = EmergencyRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Role Chip
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when (currentRole) {
                                UserRole.CITIZEN -> MaterialTheme.colorScheme.secondaryContainer
                                UserRole.VOLUNTEER -> StatusSuccess.copy(alpha = 0.2f)
                                UserRole.ADMIN -> EmergencyRed.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = currentRole.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (currentRole) {
                                    UserRole.CITIZEN -> MaterialTheme.colorScheme.onSecondaryContainer
                                    UserRole.VOLUNTEER -> StatusSuccess
                                    UserRole.ADMIN -> EmergencyRed
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Language Selector Chip
                if (selectedLanguage != null) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onLanguageClick?.invoke() }
                            .testTag("topbar_language_indicator")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🇮🇳",
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = selectedLanguage.nativeName.take(6),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // Safety Band Quick Pill
                if (safetyBand != null) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (safetyBand.isConnected) StatusSuccess.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onBandClick?.invoke() }
                            .testTag("topbar_band_indicator")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (safetyBand.isConnected) Icons.Default.Bluetooth else Icons.Default.BluetoothDisabled,
                                contentDescription = "Band Connection",
                                tint = if (safetyBand.isConnected) StatusSuccess else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (safetyBand.isConnected) "${safetyBand.batteryPercent}%" else "Off",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (safetyBand.isConnected) StatusSuccess else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
