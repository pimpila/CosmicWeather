package com.example.cosmicweather.domain.template

import com.example.cosmicweather.domain.model.ZodiacSign

/**
 * Central repository for horoscope templates.
 * Contains zodiac compatibility data and weather influences.
 */
object HoroscopeTemplates {

    /**
     * Zodiac compatibility templates.
     * Map key: Pair of zodiac signs (normalized so A+B = B+A)
     *
     * TODO: Add all 144 combinations. Currently contains a few examples.
     */
    private val compatibilityMap: Map<Pair<ZodiacSign, ZodiacSign>, ZodiacCompatibility> = mapOf(
        // Fire + Water examples
        normalizePair(ZodiacSign.SCORPIO, ZodiacSign.VIRGO) to ZodiacCompatibility(
            sign1 = ZodiacSign.SCORPIO,
            sign2 = ZodiacSign.VIRGO,
            baseClimate = "Intense but grounded",
            baseCompatibility = "High analytical synergy with deep emotional undercurrents"
        ),

        // Fire + Fire
        normalizePair(ZodiacSign.ARIES, ZodiacSign.LEO) to ZodiacCompatibility(
            sign1 = ZodiacSign.ARIES,
            sign2 = ZodiacSign.LEO,
            baseClimate = "Passionate and dynamic",
            baseCompatibility = "Natural leaders who inspire each other"
        ),

        // Earth + Water
        normalizePair(ZodiacSign.TAURUS, ZodiacSign.CANCER) to ZodiacCompatibility(
            sign1 = ZodiacSign.TAURUS,
            sign2 = ZodiacSign.CANCER,
            baseClimate = "Nurturing and stable",
            baseCompatibility = "Emotional security meets practical support"
        ),

        // Air + Air
        normalizePair(ZodiacSign.GEMINI, ZodiacSign.AQUARIUS) to ZodiacCompatibility(
            sign1 = ZodiacSign.GEMINI,
            sign2 = ZodiacSign.AQUARIUS,
            baseClimate = "Intellectually stimulating",
            baseCompatibility = "Mental connection and endless conversations"
        ),

        // Water + Water
        normalizePair(ZodiacSign.PISCES, ZodiacSign.SCORPIO) to ZodiacCompatibility(
            sign1 = ZodiacSign.PISCES,
            sign2 = ZodiacSign.SCORPIO,
            baseClimate = "Deeply intuitive",
            baseCompatibility = "Emotional depth and psychic understanding"
        ),

        // Add more combinations here...
        // For now, unlisted combinations will use fallback logic
    )

    /**
     * Weather influence templates.
     * Defines how different weather moods affect relationships.
     */
    private val weatherInfluences: Map<String, WeatherInfluence> = mapOf(
        "energetic" to WeatherInfluence(
            mood = "energetic",
            energyDescription = "outgoing and social",
            activityType = "outdoor adventures"
        ),
        "cozy" to WeatherInfluence(
            mood = "cozy",
            energyDescription = "introspective and intimate",
            activityType = "indoor bonding activities"
        ),
        "contemplative" to WeatherInfluence(
            mood = "contemplative",
            energyDescription = "thoughtful and reflective",
            activityType = "deep conversations"
        ),
        "serene" to WeatherInfluence(
            mood = "serene",
            energyDescription = "peaceful and calm",
            activityType = "quiet togetherness"
        ),
        "mysterious" to WeatherInfluence(
            mood = "mysterious",
            energyDescription = "enigmatic and curious",
            activityType = "exploring something new"
        ),
        "balanced" to WeatherInfluence(
            mood = "balanced",
            energyDescription = "harmonious and steady",
            activityType = "collaborative projects"
        ),
        "intense" to WeatherInfluence(
            mood = "intense",
            energyDescription = "emotionally charged",
            activityType = "passionate expressions"
        ),
        "romantic" to WeatherInfluence(
            mood = "romantic",
            energyDescription = "tender and affectionate",
            activityType = "romantic gestures"
        ),
        "restless" to WeatherInfluence(
            mood = "restless",
            energyDescription = "dynamic and changeable",
            activityType = "spontaneous plans"
        ),
        "passionate" to WeatherInfluence(
            mood = "passionate",
            energyDescription = "fiery and expressive",
            activityType = "bold experiences"
        ),
        "refreshing" to WeatherInfluence(
            mood = "refreshing",
            energyDescription = "invigorating and clear",
            activityType = "fresh starts"
        ),
        "sluggish" to WeatherInfluence(
            mood = "sluggish",
            energyDescription = "low-energy and laid-back",
            activityType = "relaxed lounging"
        )
    )

    /**
     * Get zodiac compatibility for a pair of signs.
     * Returns null if not found (fallback logic will be used).
     */
    fun getCompatibility(sign1: ZodiacSign, sign2: ZodiacSign): ZodiacCompatibility? {
        return compatibilityMap[normalizePair(sign1, sign2)]
    }

    /**
     * Get weather influence for a given mood.
     */
    fun getWeatherInfluence(mood: String): WeatherInfluence? {
        return weatherInfluences[mood]
    }

    /**
     * Get fallback compatibility for any sign pair.
     * Used when specific pairing is not defined.
     */
    fun getFallbackCompatibility(sign1: ZodiacSign, sign2: ZodiacSign): ZodiacCompatibility {
        // Determine element-based compatibility
        val sameElement = sign1.element == sign2.element

        return ZodiacCompatibility(
            sign1 = sign1,
            sign2 = sign2,
            baseClimate = if (sameElement) "Naturally aligned" else "Complementary energies",
            baseCompatibility = if (sameElement)
                "Shared ${sign1.element.lowercase()} element creates mutual understanding"
            else
                "${sign1.element} meets ${sign2.element} for dynamic balance"
        )
    }

    /**
     * Normalize sign pair to ensure A+B and B+A map to same key.
     * Uses ordinal comparison for consistency.
     */
    private fun normalizePair(sign1: ZodiacSign, sign2: ZodiacSign): Pair<ZodiacSign, ZodiacSign> {
        return if (sign1.ordinal <= sign2.ordinal) {
            Pair(sign1, sign2)
        } else {
            Pair(sign2, sign1)
        }
    }
}
