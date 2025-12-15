package com.example.cosmicweather.domain.generator

import com.example.cosmicweather.domain.template.HoroscopeTemplates
import com.example.cosmicweather.domain.model.Horoscope
import com.example.cosmicweather.domain.model.Weather
import com.example.cosmicweather.domain.model.ZodiacSign

/**
 * Generates horoscopes by combining zodiac compatibility with weather conditions.
 */
class HoroscopeGenerator {

    /**
     * Generate a horoscope for a zodiac pair and weather condition.
     *
     * @param sign1 First zodiac sign
     * @param sign2 Second zodiac sign (partner)
     * @param weather Current weather conditions
     * @return Generated horoscope
     */
    fun generate(sign1: ZodiacSign, sign2: ZodiacSign, weather: Weather): Horoscope {
        // Get zodiac compatibility (or fallback if not defined)
        val compatibility = HoroscopeTemplates.getCompatibility(sign1, sign2)
            ?: HoroscopeTemplates.getFallbackCompatibility(sign1, sign2)

        // Get weather influence
        val weatherInfluence = HoroscopeTemplates.getWeatherInfluence(weather.mood)

        // Generate relationship climate by combining base compatibility with weather
        val relationshipClimate = if (weatherInfluence != null) {
            "${compatibility.baseClimate} with ${weatherInfluence.energyDescription} energy"
        } else {
            compatibility.baseClimate
        }

        // Generate communication advice based on compatibility and weather
        val communication = generateCommunicationAdvice(compatibility.baseCompatibility, weatherInfluence?.energyDescription)

        // Generate activity suggestion
        val suggestedActivity = generateActivitySuggestion(
            sign1,
            sign2,
            weatherInfluence?.activityType ?: "quality time together",
            weather.condition
        )

        return Horoscope(
            sign1 = sign1,
            sign2 = sign2,
            weather = weather,
            relationshipClimate = relationshipClimate,
            communication = communication,
            suggestedActivity = suggestedActivity
        )
    }

    /**
     * Generate communication advice.
     */
    private fun generateCommunicationAdvice(baseCompatibility: String, energyDescription: String?): String {
        return if (energyDescription != null) {
            when {
                "introspective" in energyDescription -> "Thoughtful, possibly delayed - take time to reflect before speaking"
                "outgoing" in energyDescription -> "Open and expressive - perfect time for big conversations"
                "emotionally charged" in energyDescription -> "Intense and passionate - be mindful of strong reactions"
                "peaceful" in energyDescription -> "Calm and clear - easy flow of understanding"
                "dynamic" in energyDescription -> "Quick and adaptable - go with the flow"
                "low-energy" in energyDescription -> "Keep it simple and patient - save deep talks for later"
                "tender" in energyDescription -> "Gentle and affectionate - vulnerability is welcomed"
                else -> "Authentic and present - let the moment guide you"
            }
        } else {
            "Communicate openly and with care"
        }
    }

    /**
     * Generate activity suggestion based on signs, weather, and activity type.
     */
    private fun generateActivitySuggestion(
        sign1: ZodiacSign,
        sign2: ZodiacSign,
        activityType: String,
        weatherCondition: String
    ): String {
        // Create contextual suggestions based on activity type and weather
        return when {
            "outdoor" in activityType -> when (weatherCondition.lowercase()) {
                "sunny", "hot" -> "Go for a hike, have a picnic, or explore a new neighborhood"
                "partly cloudy" -> "Take a scenic walk or bike ride together"
                else -> "Bundle up for a cozy outdoor stroll"
            }

            "indoor" in activityType -> when (weatherCondition.lowercase()) {
                "rainy", "stormy" -> "Cook something together and keep conversation low-pressure"
                "snowy", "cold" -> "Build a blanket fort and watch movies"
                else -> "Try a new recipe or creative project together"
            }

            "conversation" in activityType -> "Find a quiet spot for meaningful dialogue"

            "romantic" in activityType -> "Set the mood with candles, music, and undivided attention"

            "passionate" in activityType -> "Express yourselves through art, dance, or physical activity"

            "spontaneous" in activityType -> "Say yes to whatever ideas pop up - no plans needed"

            "relaxed" in activityType || "lounging" in activityType -> "Slow down together - nap, read, or just be"

            "fresh starts" in activityType -> "Declutter a space together or start something new"

            else -> "Spend quality time doing what feels right for both of you"
        }
    }
}
