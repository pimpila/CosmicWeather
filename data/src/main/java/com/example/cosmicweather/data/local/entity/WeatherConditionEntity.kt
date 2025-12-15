package com.example.cosmicweather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cosmicweather.domain.model.Weather

/**
 * Room entity representing weather conditions in the database.
 */
@Entity(tableName = "weather_conditions")
data class WeatherConditionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "condition")
    val condition: String,

    @ColumnInfo(name = "temperature")
    val temperature: Int,

    @ColumnInfo(name = "emoji")
    val emoji: String,

    @ColumnInfo(name = "mood")
    val mood: String
)

/**
 * Convert entity to domain model.
 */
fun WeatherConditionEntity.toWeather(): Weather {
    return Weather(
        id = id,
        condition = condition,
        temperature = temperature,
        emoji = emoji,
        mood = mood
    )
}

/**
 * Convert domain model to entity.
 */
fun Weather.toEntity(): WeatherConditionEntity {
    return WeatherConditionEntity(
        id = id,
        condition = condition,
        temperature = temperature,
        emoji = emoji,
        mood = mood
    )
}
