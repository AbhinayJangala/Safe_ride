package com.example.safe_ride.repository

import com.example.safe_ride.data.EmergencyContact
import com.example.safe_ride.data.EmergencyContactDao
import kotlinx.coroutines.flow.Flow

class EmergencyContactRepository(private val emergencyContactDao: EmergencyContactDao) {

    val allContacts: Flow<List<EmergencyContact>> = emergencyContactDao.getAllContacts()

    suspend fun insert(contact: EmergencyContact) {
        emergencyContactDao.insertContact(contact)
    }

    suspend fun delete(contact: EmergencyContact) {
        emergencyContactDao.deleteContact(contact)
    }
}
