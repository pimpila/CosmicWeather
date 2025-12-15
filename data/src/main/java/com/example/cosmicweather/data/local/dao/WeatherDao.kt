package com.example.cosmicweather.data.local.dao

import androidx.room.*
import com.example.cosmicweather.data.local.entity.WeatherConditionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for weather conditions.
 */
@Dao
interface WeatherDao {

    /**
     * Get a random weather condition.
     * This is used to generate new horoscope readings.
     */
    @Query("SELECT * FROM weather_conditions ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWeather(): WeatherConditionEntity?

    /**
     * Get all weather conditions.
     */
    @Query("SELECT * FROM weather_conditions ORDER BY id ASC")
    fun getAllWeather(): Flow<List<WeatherConditionEntity>>

    /**
     * Get weather by ID.
     */
    @Query("SELECT * FROM weather_conditions WHERE id = :id LIMIT 1")
    suspend fun getWeatherById(id: Int): WeatherConditionEntity?

    /**
     * Insert weather conditions (for database seeding).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg weather: WeatherConditionEntity)

    /**
     * Delete all weather conditions.
     */
    @Query("DELETE FROM weather_conditions")
    suspend fun deleteAll()
}
