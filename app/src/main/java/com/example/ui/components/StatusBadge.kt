package com.example.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeliveryState
import com.example.data.model.EmergencyStatus
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

@Composable
fun DeliveryBadge(state: DeliveryState, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (state) {
        DeliveryState.DELIVERED -> Triple(StatusSuccess.copy(alpha = 0.2f), StatusSuccess, "DELIVERED")
        DeliveryState.PENDING -> Triple(StatusWarning.copy(alpha = 0.2f), StatusWarning, "PENDING")
        DeliveryState.FAILED -> Triple(StatusError.copy(alpha = 0.2f), StatusError, "FAILED")
        DeliveryState.RETRYING -> Triple(StatusWarning.copy(alpha = 0.2f), StatusWarning, "RETRYING")
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun EmergencyStatusBadge(status: EmergencyStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (status) {
        EmergencyStatus.PENDING -> Triple(StatusWarning.copy(alpha = 0.2f), StatusWarning, "STARTING")
        EmergencyStatus.DISPATCHING -> Triple(StatusWarning.copy(alpha = 0.2f), StatusWarning, "DISPATCHING")
        EmergencyStatus.DELIVERED -> Triple(EmergencyRed.copy(alpha = 0.2f), EmergencyRed, "ALERTS SENT")
        EmergencyStatus.VOLUNTEER_ASSIGNED -> Triple(StatusSuccess.copy(alpha = 0.2f), StatusSuccess, "VOLUNTEER ASSIGNED")
        EmergencyStatus.RESOLVED -> Triple(StatusSuccess.copy(alpha = 0.2f), StatusSuccess, "RESOLVED - SAFE")
        EmergencyStatus.CANCELLED -> Triple(Color.Gray.copy(alpha = 0.2f), Color.LightGray, "CANCELLED")
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
