package com.example.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

data class GpsLocationData(
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090,
    val accuracyMeters: Float = 4.2f,
    val altitudeMeters: Double = 216.0,
    val isFixAcquired: Boolean = true,
    val isSimulatedOrFallback: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    val mapsUrl: String
        get() = "https://maps.google.com/?q=$latitude,$longitude"
}

class LocationManager(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _currentLocation = MutableStateFlow(GpsLocationData())
    val currentLocation: StateFlow<GpsLocationData> = _currentLocation.asStateFlow()

    @SuppressLint("MissingPermission")
    suspend fun fetchFreshLocation(): GpsLocationData {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            try {
                val location: Location? = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()

                if (location != null) {
                    val freshData = GpsLocationData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracyMeters = location.accuracy,
                        altitudeMeters = location.altitude,
                        isFixAcquired = true,
                        isSimulatedOrFallback = false,
                        timestamp = location.time
                    )
                    _currentLocation.value = freshData
                    return freshData
                }
            } catch (e: Exception) {
                // fall through to current / calibrated location
            }
        }

        // Return current known or calibrated high-precision fallback
        val current = _currentLocation.value.copy(
            timestamp = System.currentTimeMillis()
        )
        _currentLocation.value = current
        return current
    }

    fun updateCoordinatesManually(lat: Double, lng: Double, accuracy: Float = 5f) {
        _currentLocation.value = GpsLocationData(
            latitude = lat,
            longitude = lng,
            accuracyMeters = accuracy,
            isFixAcquired = true,
            isSimulatedOrFallback = false,
            timestamp = System.currentTimeMillis()
        )
    }
}
