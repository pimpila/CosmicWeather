package com.example.cosmicweather.domain.model

/**
 * Domain model representing weather conditions.
 * This is used by the UI and horoscope generator.
 */
data class Weather(
    val id: Int,
    val condition: String,        // e.g., "Sunny", "Rainy", "Snowy"
    val temperature: Int,          // In Celsius
    val emoji: String,            // Weather emoji like "☀️", "🌧️", "❄️"
    val mood: String              // Weather mood: "energetic", "cozy", "calm", etc.
)
