package com.example.safe_ride.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.safe_ride.data.EmergencyContact
import com.example.safe_ride.data.HomeUiState
import com.example.safe_ride.data.SafeRideDatabase
import com.example.safe_ride.repository.EmergencyContactRepository
import com.example.safe_ride.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EmergencyContactRepository
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val dao = SafeRideDatabase.getDatabase(application).emergencyContactDao()
        repository = EmergencyContactRepository(dao)

        viewModelScope.launch {
            repository.allContacts.collect { contactsList ->
                _uiState.update { it.copy(contacts = contactsList) }
            }
        }
    }

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

    fun getCurrentLocation(context: Context) {
        val locationRepository = LocationRepository(context)
        locationRepository.getCurrentLocation { address ->
            updateCurrentLocation(address)
        }
    }

    fun addContact(name: String, phoneNumber: String) {
        if (name.isNotBlank() && phoneNumber.isNotBlank()) {
            viewModelScope.launch {
                repository.insert(EmergencyContact(name = name.trim(), phoneNumber = phoneNumber.trim()))
            }
        }
    }

    fun deleteContact(contact: EmergencyContact) {
        viewModelScope.launch {
            repository.delete(contact)
        }
    }
}
