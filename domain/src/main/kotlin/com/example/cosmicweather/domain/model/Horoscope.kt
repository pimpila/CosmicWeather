package com.example.cosmicweather.domain.model

/**
 * Domain model representing a generated horoscope.
 * Combines zodiac compatibility with weather conditions.
 */
data class Horoscope(
    val sign1: ZodiacSign,
    val sign2: ZodiacSign,
    val weather: Weather,
    val relationshipClimate: String,
    val communication: String,
    val suggestedActivity: String
)
