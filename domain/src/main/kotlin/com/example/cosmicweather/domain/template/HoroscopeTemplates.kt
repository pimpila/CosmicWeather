package com.example.cosmicweather.domain.template

import com.example.cosmicweather.domain.model.ZodiacSign

/**
 * Represents the personality archetype of a zodiac sign.
 */
data class SignArchetype(
    val energyStyle: String,  // How they express themselves
    val relationalTrait: String,  // How they relate to others
    val communicationStyle: String  // How they communicate
)

/**
 * Central repository for horoscope templates.
 * Contains zodiac compatibility data and weather influences.
 * Covers all 144 zodiac sign combinations through algorithmic generation.
 */
object HoroscopeTemplates {

    /**
     * Individual zodiac sign archetypes.
     * Used to generate compatibility for all combinations.
     */
    private val signArchetypes = mapOf(
        // Fire Signs - Passionate, energetic, spontaneous
        ZodiacSign.ARIES to SignArchetype(
            energyStyle = "bold and initiating",
            relationalTrait = "direct and competitive",
            communicationStyle = "assertive and straightforward"
        ),
        ZodiacSign.LEO to SignArchetype(
            energyStyle = "warm and expressive",
            relationalTrait = "generous and dramatic",
            communicationStyle = "confident and theatrical"
        ),
        ZodiacSign.SAGITTARIUS to SignArchetype(
            energyStyle = "adventurous and philosophical",
            relationalTrait = "optimistic and freedom-loving",
            communicationStyle = "candid and expansive"
        ),

        // Earth Signs - Practical, grounded, reliable
        ZodiacSign.TAURUS to SignArchetype(
            energyStyle = "steady and sensual",
            relationalTrait = "loyal and possessive",
            communicationStyle = "deliberate and practical"
        ),
        ZodiacSign.VIRGO to SignArchetype(
            energyStyle = "analytical and precise",
            relationalTrait = "helpful and critical",
            communicationStyle = "detailed and thoughtful"
        ),
        ZodiacSign.CAPRICORN to SignArchetype(
            energyStyle = "disciplined and ambitious",
            relationalTrait = "responsible and reserved",
            communicationStyle = "structured and goal-oriented"
        ),

        // Air Signs - Intellectual, social, communicative
        ZodiacSign.GEMINI to SignArchetype(
            energyStyle = "curious and adaptable",
            relationalTrait = "social and versatile",
            communicationStyle = "quick-witted and playful"
        ),
        ZodiacSign.LIBRA to SignArchetype(
            energyStyle = "harmonious and diplomatic",
            relationalTrait = "partnership-focused and balanced",
            communicationStyle = "charming and considerate"
        ),
        ZodiacSign.AQUARIUS to SignArchetype(
            energyStyle = "innovative and unconventional",
            relationalTrait = "independent and humanitarian",
            communicationStyle = "intellectual and detached"
        ),

        // Water Signs - Emotional, intuitive, nurturing
        ZodiacSign.CANCER to SignArchetype(
            energyStyle = "protective and nurturing",
            relationalTrait = "sensitive and moody",
            communicationStyle = "indirect and emotional"
        ),
        ZodiacSign.SCORPIO to SignArchetype(
            energyStyle = "intense and transformative",
            relationalTrait = "passionate and secretive",
            communicationStyle = "penetrating and powerful"
        ),
        ZodiacSign.PISCES to SignArchetype(
            energyStyle = "dreamy and compassionate",
            relationalTrait = "empathetic and boundary-less",
            communicationStyle = "poetic and non-verbal"
        )
    )

    /**
     * Element compatibility patterns.
     * Defines how different elemental energies interact.
     */
    private val elementCompatibility = mapOf(
        // Same element - natural understanding
        Pair("Fire", "Fire") to Pair("Passionate and dynamic", "Two flames that either ignite or compete"),
        Pair("Earth", "Earth") to Pair("Grounded and stable", "Practical partnership built on shared values"),
        Pair("Air", "Air") to Pair("Intellectually stimulating", "Mental connection with endless ideas"),
        Pair("Water", "Water") to Pair("Deeply intuitive", "Emotional resonance and psychic understanding"),

        // Fire + Earth - challenging but growth-oriented
        Pair("Fire", "Earth") to Pair("Inspiring yet grounding", "Passion meets practicality in creative tension"),
        Pair("Earth", "Fire") to Pair("Stabilizing yet enlivening", "Security provides foundation for boldness"),

        // Fire + Air - harmonious and energizing
        Pair("Fire", "Air") to Pair("Exciting and expansive", "Ideas fuel action in mutually supportive flow"),
        Pair("Air", "Fire") to Pair("Stimulating and encouraging", "Communication fans the flames of passion"),

        // Fire + Water - steamy but complex
        Pair("Fire", "Water") to Pair("Intense and transformative", "Heat and moisture create powerful alchemy"),
        Pair("Water", "Fire") to Pair("Soulful yet volatile", "Depth meets intensity in passionate dance"),

        // Earth + Air - different perspectives
        Pair("Earth", "Air") to Pair("Complementary but contrasting", "Grounded reality meets abstract thinking"),
        Pair("Air", "Earth") to Pair("Balancing and educational", "Logic grounds imagination, routine inspires innovation"),

        // Earth + Water - nurturing and compatible
        Pair("Earth", "Water") to Pair("Nurturing and fertile", "Emotional depth meets tangible support"),
        Pair("Water", "Earth") to Pair("Comforting and secure", "Feelings find safe container in stability"),

        // Air + Water - gentle but disconnected
        Pair("Air", "Water") to Pair("Curious yet elusive", "Thoughts try to grasp feelings in delicate balance"),
        Pair("Water", "Air") to Pair("Flowing and conceptual", "Emotions seek expression through ideas")
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
     * Get zodiac compatibility for any pair of signs.
     * Generates compatibility using sign archetypes and element patterns.
     * Covers all 144 combinations algorithmically.
     */
    fun getCompatibility(sign1: ZodiacSign, sign2: ZodiacSign): ZodiacCompatibility? {
        val archetype1 = signArchetypes[sign1] ?: return null
        val archetype2 = signArchetypes[sign2] ?: return null

        // Get element compatibility pattern
        val elementPattern = elementCompatibility[Pair(sign1.element, sign2.element)]
            ?: return null

        // Build relationship climate
        val baseClimate = elementPattern.first

        // Build compatibility description combining individual traits and element dynamics
        val baseCompatibility = when {
            sign1 == sign2 -> {
                // Same sign - mirror dynamic
                "Mirror souls with ${archetype1.energyStyle} energy and ${archetype1.relationalTrait} approach. ${elementPattern.second}"
            }
            sign1.element == sign2.element -> {
                // Same element but different signs
                "${archetype1.energyStyle.replaceFirstChar { it.uppercase() }} meets ${archetype2.energyStyle}. ${elementPattern.second}"
            }
            else -> {
                // Different elements
                "${archetype1.relationalTrait.replaceFirstChar { it.uppercase() }} encounters ${archetype2.relationalTrait}. ${elementPattern.second}"
            }
        }

        return ZodiacCompatibility(
            sign1 = sign1,
            sign2 = sign2,
            baseClimate = baseClimate,
            baseCompatibility = baseCompatibility
        )
    }

    /**
     * Get weather influence for a given mood.
     */
    fun getWeatherInfluence(mood: String): WeatherInfluence? {
        return weatherInfluences[mood]
    }

    /**
     * Get fallback compatibility (now uses main algorithm).
     * Kept for backward compatibility.
     */
    fun getFallbackCompatibility(sign1: ZodiacSign, sign2: ZodiacSign): ZodiacCompatibility {
        return getCompatibility(sign1, sign2)
            ?: ZodiacCompatibility(
                sign1 = sign1,
                sign2 = sign2,
                baseClimate = "Unique connection",
                baseCompatibility = "Every pairing creates its own path"
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
