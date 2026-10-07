package com.example.safe_ride.viewmodel

import androidx.lifecycle.ViewModel
import com.example.safe_ride.data.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun updateCurrentLocation(location: String) {
        _uiState.update { it.copy(currentLocation = location) }
    }

    fun updateDestination(destination: String) {
        _uiState.update { it.copy(destination = destination) }
    }

    fun startJourney() {
        if (_uiState.value.destination.isNotBlank()) {
            _uiState.update { it.copy(journeyStarted = true) }
        }
    }

    fun resetJourney() {
        _uiState.update { it.copy(journeyStarted = false, destination = "") }
    }
}
