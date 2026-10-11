package com.example.safe_ride.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EmergencyContact::class], version = 1, exportSchema = false)
abstract class SafeRideDatabase : RoomDatabase() {

    abstract fun emergencyContactDao(): EmergencyContactDao

    companion object {
        @Volatile
        private var INSTANCE: SafeRideDatabase? = null

        fun getDatabase(context: Context): SafeRideDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SafeRideDatabase::class.java,
                    "safe_ride_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
