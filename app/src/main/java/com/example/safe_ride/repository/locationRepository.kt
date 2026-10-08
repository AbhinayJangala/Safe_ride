package com.example.safe_ride.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale

class LocationRepository(private val context: Context) {

    private val locationClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        onLocationReceived: (String) -> Unit
    ) {
        locationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    resolveAddress(location, onLocationReceived)
                } else {
                    try {
                        val cancellationTokenSource = CancellationTokenSource()
                        locationClient.getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            cancellationTokenSource.token
                        ).addOnSuccessListener { freshLocation ->
                            if (freshLocation != null) {
                                resolveAddress(freshLocation, onLocationReceived)
                            } else {
                                onLocationReceived("Unable to retrieve location (Check GPS)")
                            }
                        }.addOnFailureListener {
                            onLocationReceived("Unable to retrieve location")
                        }
                    } catch (e: Exception) {
                        onLocationReceived("Unable to retrieve location")
                    }
                }
            }
            .addOnFailureListener {
                onLocationReceived("Unable to retrieve location")
            }
    }

    private fun resolveAddress(location: Location, onAddressResolved: (String) -> Unit) {
        val latitude = location.latitude
        val longitude = location.longitude
        val fallback = "$latitude, $longitude"

        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    val addressText = if (!addresses.isNullOrEmpty()) {
                        addresses[0].getAddressLine(0) ?: "${addresses[0].locality ?: ""}, ${addresses[0].adminArea ?: ""}".trim(',', ' ')
                    } else {
                        fallback
                    }
                    onAddressResolved(addressText.ifBlank { fallback })
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                val addressText = if (!addresses.isNullOrEmpty()) {
                    addresses[0].getAddressLine(0) ?: "${addresses[0].locality ?: ""}, ${addresses[0].adminArea ?: ""}".trim(',', ' ')
                } else {
                    fallback
                }
                onAddressResolved(addressText.ifBlank { fallback })
            }
        } catch (e: Exception) {
            onAddressResolved(fallback)
        }
    }
}
