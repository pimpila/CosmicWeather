package com.example.cosmicweather.data.repository

import com.example.cosmicweather.data.local.dao.WeatherDao
import com.example.cosmicweather.data.local.database.WeatherDatabase
import com.example.cosmicweather.data.local.entity.toWeather
import com.example.cosmicweather.domain.model.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for weather data.
 * Abstracts data source (Room database) from the rest of the app.
 */
class WeatherRepository(
    private val weatherDao: WeatherDao,
    private val database: WeatherDatabase
) {
    private var isInitialized = false

    /**
     * Ensure database is populated before first use.
     */
    private suspend fun ensureInitialized() {
        if (!isInitialized) {
            WeatherDatabase.ensureDatabasePopulated(database)
            isInitialized = true
        }
    }

    /**
     * Get a random weather condition.
     * This is used to generate new horoscope readings.
     *
     * @return Random weather or null if database is empty
     */
    suspend fun getRandomWeather(): Weather? {
        ensureInitialized()
        return weatherDao.getRandomWeather()?.toWeather()
    }

    /**
     * Get all weather scenarios as a Flow (for future features).
     */
    fun getAllWeatherScenarios(): Flow<List<Weather>> {
        return weatherDao.getAllWeather()
            .map { entities -> entities.map { it.toWeather() } }
    }

    /**
     * Get weather by ID.
     */
    suspend fun getWeatherById(id: Int): Weather? {
        return weatherDao.getWeatherById(id)?.toWeather()
    }
}
