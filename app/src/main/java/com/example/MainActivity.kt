package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AasraViewModel
import com.example.ui.viewmodel.AppScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    val isWideScreen = maxWidth > 600.dp
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight()
                            .then(
                                if (isWideScreen) {
                                    Modifier
                                        .widthIn(max = 540.dp)
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp)
                                } else {
                                    Modifier.fillMaxWidth()
                                }
                            ),
                        shape = if (isWideScreen) RoundedCornerShape(24.dp) else RoundedCornerShape(0.dp),
                        shadowElevation = if (isWideScreen) 16.dp else 0.dp,
                        border = if (isWideScreen) BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)) else null,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        AasraApp()
                    }
                }
            }
        }
    }
}

@Composable
fun AasraApp(
    viewModel: AasraViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Request vital emergency permissions on startup
    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Permissions handled transparently
    }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        permissionsLauncher.launch(permissions.toTypedArray())
    }

    // Feedback Toast emitter
    LaunchedEffect(uiState.userFeedbackMessage) {
        uiState.userFeedbackMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearFeedback()
        }
    }

    // BackHandler for sub-screens
    if (uiState.currentScreen !in listOf(AppScreen.Dashboard, AppScreen.Splash, AppScreen.ActiveSos, AppScreen.LanguageSelect)) {
        BackHandler {
            viewModel.navigateTo(AppScreen.Dashboard)
        }
    }

    when (val screen = uiState.currentScreen) {
        is AppScreen.Splash -> {
            SplashScreen(onTimeout = { viewModel.navigateTo(AppScreen.LanguageSelect) })
        }
        is AppScreen.LanguageSelect -> {
            LanguageSelectScreen(
                viewModel = viewModel,
                selectedLanguage = uiState.selectedLanguage,
                onLanguageConfirmed = { viewModel.navigateTo(AppScreen.Dashboard) }
            )
        }
        is AppScreen.Onboarding -> {
            OnboardingScreen(onContinue = { viewModel.navigateTo(AppScreen.Dashboard) })
        }
        is AppScreen.Auth -> {
            AuthScreen(viewModel = viewModel, onAuthSuccess = { viewModel.navigateTo(AppScreen.Dashboard) })
        }
        is AppScreen.Dashboard -> {
            DashboardScreen(viewModel = viewModel, uiState = uiState)
        }
        is AppScreen.ActiveSos -> {
            ActiveSosScreen(viewModel = viewModel, uiState = uiState)
        }
        is AppScreen.Contacts -> {
            ContactsScreen(viewModel = viewModel, uiState = uiState)
        }
        is AppScreen.HardwareBand -> {
            HardwareBandScreen(viewModel = viewModel, uiState = uiState)
        }
        is AppScreen.VolunteerPortal -> {
            VolunteerPortalScreen(viewModel = viewModel, uiState = uiState)
        }
        is AppScreen.AudioEvidence -> {
            AudioEvidenceScreen(viewModel = viewModel, uiState = uiState)
        }
        is AppScreen.AdminConsole -> {
            AdminConsoleScreen(viewModel = viewModel, uiState = uiState)
        }
        is AppScreen.SettingsProfile -> {
            SettingsAndProfileScreen(viewModel = viewModel, uiState = uiState)
        }
    }
}
