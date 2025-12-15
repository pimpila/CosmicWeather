package com.example.cosmicweather.domain.template

import com.example.cosmicweather.domain.model.ZodiacSign

/**
 * Template for zodiac pair compatibility.
 * Defines base relationship traits independent of weather.
 */
data class ZodiacCompatibility(
    val sign1: ZodiacSign,
    val sign2: ZodiacSign,
    val baseClimate: String,
    val baseCompatibility: String
)

/**
 * Template for how weather affects relationship dynamics.
 */
data class WeatherInfluence(
    val mood: String,              // matches Weather.mood
    val energyDescription: String,  // how this weather affects energy
    val activityType: String        // suggested activity category
)
