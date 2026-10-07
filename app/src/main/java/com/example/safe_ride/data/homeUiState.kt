package com.example.safe_ride.data

data class HomeUiState(
    val currentLocation: String = "Current Location (GPS)",
    val destination: String = "",
    val journeyStarted: Boolean = false
)