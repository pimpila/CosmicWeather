package com.example.cosmicweather.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cosmicweather.data.local.dao.WeatherDao
import com.example.cosmicweather.data.local.entity.WeatherConditionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room database for weather conditions.
 * Pre-populated with diverse weather scenarios on first creation.
 */
@Database(
    entities = [WeatherConditionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        private const val DATABASE_NAME = "cosmic_weather_database"

        /**
         * Get database instance (singleton pattern).
         */
        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Callback to prepopulate database with weather scenarios.
         */
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.weatherDao())
                    }
                }
            }
        }

        /**
         * Prepopulate database with diverse weather scenarios.
         */
        private suspend fun populateDatabase(weatherDao: WeatherDao) {
            val weatherScenarios = listOf(
                WeatherConditionEntity(
                    id = 1,
                    condition = "Sunny",
                    temperature = 25,
                    emoji = "☀️",
                    mood = "energetic"
                ),
                WeatherConditionEntity(
                    id = 2,
                    condition = "Rainy",
                    temperature = 15,
                    emoji = "🌧️",
                    mood = "cozy"
                ),
                WeatherConditionEntity(
                    id = 3,
                    condition = "Cloudy",
                    temperature = 18,
                    emoji = "☁️",
                    mood = "contemplative"
                ),
                WeatherConditionEntity(
                    id = 4,
                    condition = "Snowy",
                    temperature = -2,
                    emoji = "❄️",
                    mood = "serene"
                ),
                WeatherConditionEntity(
                    id = 5,
                    condition = "Foggy",
                    temperature = 12,
                    emoji = "🌫️",
                    mood = "mysterious"
                ),
                WeatherConditionEntity(
                    id = 6,
                    condition = "Partly Cloudy",
                    temperature = 22,
                    emoji = "⛅",
                    mood = "balanced"
                ),
                WeatherConditionEntity(
                    id = 7,
                    condition = "Stormy",
                    temperature = 16,
                    emoji = "⛈️",
                    mood = "intense"
                ),
                WeatherConditionEntity(
                    id = 8,
                    condition = "Clear Night",
                    temperature = 10,
                    emoji = "🌙",
                    mood = "romantic"
                ),
                WeatherConditionEntity(
                    id = 9,
                    condition = "Windy",
                    temperature = 14,
                    emoji = "💨",
                    mood = "restless"
                ),
                WeatherConditionEntity(
                    id = 10,
                    condition = "Hot",
                    temperature = 35,
                    emoji = "🔥",
                    mood = "passionate"
                ),
                WeatherConditionEntity(
                    id = 11,
                    condition = "Crisp",
                    temperature = 5,
                    emoji = "🍂",
                    mood = "refreshing"
                ),
                WeatherConditionEntity(
                    id = 12,
                    condition = "Humid",
                    temperature = 28,
                    emoji = "💧",
                    mood = "sluggish"
                )
            )

            weatherDao.insertAll(*weatherScenarios.toTypedArray())
        }
    }
}
