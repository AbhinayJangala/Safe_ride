package com.example.safe_ride.data

data class HomeUiState(
    val currentLocation: String = "Getting location...",
    val destination: String = "",
    val journeyStarted: Boolean = false,
    val contacts: List<EmergencyContact> = emptyList()
)
