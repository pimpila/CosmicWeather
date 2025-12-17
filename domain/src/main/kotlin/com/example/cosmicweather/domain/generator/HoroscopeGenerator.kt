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
        val communication = generateCommunicationAdvice(
            sign1,
            sign2,
            compatibility.baseCompatibility,
            weatherInfluence?.energyDescription
        )

        // Generate activity suggestion
        val suggestedActivity = generateActivitySuggestion(
            sign1,
            sign2,
            compatibility.baseCompatibility,
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
     * Generate communication advice based on sign compatibility and weather.
     */
    private fun generateCommunicationAdvice(
        sign1: ZodiacSign,
        sign2: ZodiacSign,
        baseCompatibility: String,
        energyDescription: String?
    ): String {
        // Get element compatibility style
        val compatibilityStyle = when {
            sign1.element == sign2.element -> "naturally aligned"
            (sign1.element == "Fire" && sign2.element == "Air") ||
            (sign1.element == "Air" && sign2.element == "Fire") -> "energizing and stimulating"
            (sign1.element == "Earth" && sign2.element == "Water") ||
            (sign1.element == "Water" && sign2.element == "Earth") -> "supportive and grounding"
            (sign1.element == "Fire" && sign2.element == "Water") ||
            (sign1.element == "Water" && sign2.element == "Fire") -> "requiring patience and care"
            else -> "bridging different perspectives"
        }

        return if (energyDescription != null) {
            when {
                "introspective" in energyDescription -> "Thoughtful and $compatibilityStyle - take time to reflect before speaking"
                "outgoing" in energyDescription -> "Open and $compatibilityStyle - perfect time for big conversations"
                "emotionally charged" in energyDescription -> "Intense and $compatibilityStyle - be mindful of strong reactions"
                "peaceful" in energyDescription -> "Calm and $compatibilityStyle - easy flow of understanding"
                "dynamic" in energyDescription -> "Quick and $compatibilityStyle - go with the flow"
                "low-energy" in energyDescription -> "Patient and $compatibilityStyle - save deep talks for later"
                "tender" in energyDescription -> "Gentle and $compatibilityStyle - vulnerability is welcomed"
                else -> "Authentic and $compatibilityStyle - let the moment guide you"
            }
        } else {
            "Communication is $compatibilityStyle - speak from the heart"
        }
    }

    /**
     * Generate activity suggestion based on signs, compatibility, weather, and activity type.
     */
    private fun generateActivitySuggestion(
        sign1: ZodiacSign,
        sign2: ZodiacSign,
        baseCompatibility: String,
        activityType: String,
        weatherCondition: String
    ): String {
        // Determine sign energy style
        val energyStyle = when {
            sign1.element in listOf("Fire", "Air") && sign2.element in listOf("Fire", "Air") -> "high-energy"
            sign1.element in listOf("Earth", "Water") && sign2.element in listOf("Earth", "Water") -> "grounded"
            else -> "balanced"
        }

        // Create contextual suggestions based on activity type, weather, and sign energy
        return when {
            "outdoor" in activityType -> when (weatherCondition.lowercase()) {
                "sunny" -> if (energyStyle == "high-energy") {
                    "Go for an adventurous hike or try a new outdoor sport together"
                } else if (energyStyle == "grounded") {
                    "Take a peaceful nature walk or have a picnic in the park"
                } else {
                    "Enjoy a leisurely outdoor activity that combines movement and rest"
                }
                "windy" -> if (energyStyle == "high-energy") {
                    "Fly kites or embrace the wild energy with an outdoor adventure"
                } else if (energyStyle == "grounded") {
                    "Take a brisk walk and enjoy the fresh, moving air"
                } else {
                    "Feel the wind together - let it energize or ground you as needed"
                }
                "rainy", "stormy" -> if (energyStyle == "high-energy") {
                    "Dance in the rain or splash through puddles together"
                } else if (energyStyle == "grounded") {
                    "Bundle up for a cozy outdoor stroll and meaningful conversation"
                } else {
                    "Embrace the weather - find your own rhythm in the rain"
                }
                "snowy" -> if (energyStyle == "high-energy") {
                    "Have a snowball fight or build something creative together"
                } else if (energyStyle == "grounded") {
                    "Take a quiet winter walk and enjoy the peaceful silence"
                } else {
                    "Experience the snow at your own pace - playful or peaceful"
                }
                "foggy", "cloudy" -> if (energyStyle == "high-energy") {
                    "Turn the mystery into an adventure - explore somewhere familiar with fresh eyes"
                } else if (energyStyle == "grounded") {
                    "Wander slowly and let the soft light create an intimate atmosphere"
                } else {
                    "Let the muted weather match your energy - calm or curious"
                }
                else -> if (energyStyle == "high-energy") {
                    "Get outside and let your combined energy guide the activity"
                } else if (energyStyle == "grounded") {
                    "Spend time in nature, moving at a comfortable, connected pace"
                } else {
                    "Find an outdoor activity that honors both your energies"
                }
            }

            "indoor" in activityType -> when (weatherCondition.lowercase()) {
                "rainy", "stormy" -> if (energyStyle == "grounded") {
                    "Cook a comforting meal together and share stories"
                } else if (energyStyle == "high-energy") {
                    "Try a new recipe or play board games to stay energized"
                } else {
                    "Create a cozy indoor space that feels both safe and stimulating"
                }
                "snowy", "foggy" -> if (energyStyle == "grounded") {
                    "Create a cozy sanctuary with blankets, tea, and quiet time"
                } else if (energyStyle == "high-energy") {
                    "Build a blanket fort and watch your favorite movies"
                } else {
                    "Design an indoor haven that balances comfort with engagement"
                }
                else -> if (energyStyle == "high-energy") {
                    "Try an energetic indoor activity - dancing, working out, or tackling a project"
                } else if (energyStyle == "grounded") {
                    "Enjoy quiet indoor activities - reading, crafts, or relaxed conversation"
                } else {
                    "Find an indoor balance - mix active projects with restful moments"
                }
            }

            "conversation" in activityType -> if (energyStyle == "grounded") {
                "Find a quiet spot for deep, meaningful dialogue"
            } else if (energyStyle == "high-energy") {
                "Engage in lively discussion over coffee or a shared activity"
            } else {
                "Talk at whatever pace feels natural - deep or light, serious or playful"
            }

            "romantic" in activityType -> if (sign1.element == "Water" || sign2.element == "Water") {
                if (energyStyle == "grounded") {
                    "Set the mood with candles, music, and emotional intimacy"
                } else {
                    "Create romance through emotional depth and shared vulnerability"
                }
            } else {
                if (energyStyle == "high-energy") {
                    "Bring excitement to romance - adventure, surprise, and spontaneity"
                } else {
                    "Create romance through thoughtful gestures and undivided attention"
                }
            }

            "passionate" in activityType || "expressions" in activityType -> if (energyStyle == "high-energy") {
                "Express yourselves through dance, art, or physical activity"
            } else if (energyStyle == "grounded") {
                "Channel passion into a shared creative or sensory experience"
            } else {
                "Find your own way to express passion - active or contemplative"
            }

            "spontaneous" in activityType -> if (energyStyle == "high-energy") {
                "Say yes to adventure - no plans, just go where energy takes you"
            } else if (energyStyle == "grounded") {
                "Follow your intuition to whatever feels right in the moment"
            } else {
                "Be spontaneous in your own way - impulsive or gently unplanned"
            }

            "exploring" in activityType || "new" in activityType -> if (energyStyle == "high-energy") {
                "Explore a new neighborhood, restaurant, or activity together"
            } else if (energyStyle == "grounded") {
                "Discover something new at a comfortable pace - a museum, gallery, or bookstore"
            } else {
                "Find new experiences that work for both of you - adventurous or gentle"
            }

            "quiet" in activityType || "togetherness" in activityType -> if (energyStyle == "grounded") {
                "Simply be together in comfortable silence or gentle presence"
            } else if (energyStyle == "high-energy") {
                "Find stillness together, even if it's through shared activity first"
            } else {
                "Enjoy time together without pressure - quiet or gently interactive"
            }

            else -> if (energyStyle == "balanced") {
                "Balance activity and rest - honor both your energies"
            } else if (energyStyle == "grounded") {
                "Spend quality time in comfortable routine or peaceful presence"
            } else {
                "Keep things lively and engaging - variety is your friend"
            }
        }
    }
}
