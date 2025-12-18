package com.example.cosmicweather.data.local.entity

import com.example.cosmicweather.domain.model.Weather
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for entity-to-domain and domain-to-entity mapping functions.
 * Tests our custom mapper logic without requiring Android framework.
 */
class WeatherConditionEntityMapperTest {

    @Test
    fun toWeather_mapsAllFieldsCorrectly() {
        // Given
        val entity = WeatherConditionEntity(
            id = 1,
            condition = "Sunny",
            temperature = 25,
            emoji = "☀️",
            mood = "energetic"
        )

        // When
        val weather = entity.toWeather()

        // Then
        assertEquals(1, weather.id)
        assertEquals("Sunny", weather.condition)
        assertEquals(25, weather.temperature)
        assertEquals("☀️", weather.emoji)
        assertEquals("energetic", weather.mood)
    }

    @Test
    fun toEntity_mapsAllFieldsCorrectly() {
        // Given
        val weather = Weather(
            id = 2,
            condition = "Rainy",
            temperature = 15,
            emoji = "🌧️",
            mood = "cozy"
        )

        // When
        val entity = weather.toEntity()

        // Then
        assertEquals(2, entity.id)
        assertEquals("Rainy", entity.condition)
        assertEquals(15, entity.temperature)
        assertEquals("🌧️", entity.emoji)
        assertEquals("cozy", entity.mood)
    }

    @Test
    fun toWeather_toEntity_roundTripPreservesData() {
        // Given
        val originalWeather = Weather(
            id = 5,
            condition = "Foggy",
            temperature = 12,
            emoji = "🌫️",
            mood = "mysterious"
        )

        // When - convert to entity and back
        val entity = originalWeather.toEntity()
        val resultWeather = entity.toWeather()

        // Then - should be identical
        assertEquals(originalWeather, resultWeather)
    }

    @Test
    fun toEntity_toWeather_roundTripPreservesData() {
        // Given
        val originalEntity = WeatherConditionEntity(
            id = 7,
            condition = "Clear Night",
            temperature = 10,
            emoji = "🌙",
            mood = "romantic"
        )

        // When - convert to weather and back
        val weather = originalEntity.toWeather()
        val resultEntity = weather.toEntity()

        // Then - should be identical
        assertEquals(originalEntity, resultEntity)
    }

    @Test
    fun toWeather_preservesEmojis() {
        // Given - test various emojis
        val emojis = listOf("☀️", "🌧️", "☁️", "❄️", "🌫️", "⛈️", "🌙", "💨")

        emojis.forEachIndexed { index, emoji ->
            val entity = WeatherConditionEntity(
                id = index,
                condition = "Test",
                temperature = 20,
                emoji = emoji,
                mood = "test"
            )

            // When
            val weather = entity.toWeather()

            // Then
            assertEquals(emoji, weather.emoji)
        }
    }
}
