package com.example.cosmicweather.domain.model

/**
 * Enum representing the 12 zodiac signs.
 * Ordered by astrological calendar (starting with Aries).
 */
enum class ZodiacSign(
    val displayName: String,
    val dateRange: String,
    val element: String,
    val symbol: String  // Unicode zodiac symbol
) {
    ARIES("Aries", "Mar 21 - Apr 19", "Fire", "♈️"),
    TAURUS("Taurus", "Apr 20 - May 20", "Earth", "♉️"),
    GEMINI("Gemini", "May 21 - Jun 20", "Air", "♊️"),
    CANCER("Cancer", "Jun 21 - Jul 22", "Water", "♋️"),
    LEO("Leo", "Jul 23 - Aug 22", "Fire", "♌️"),
    VIRGO("Virgo", "Aug 23 - Sep 22", "Earth", "♍️"),
    LIBRA("Libra", "Sep 23 - Oct 22", "Air", "♎️"),
    SCORPIO("Scorpio", "Oct 23 - Nov 21", "Water", "♏️"),
    SAGITTARIUS("Sagittarius", "Nov 22 - Dec 21", "Fire", "♐️"),
    CAPRICORN("Capricorn", "Dec 22 - Jan 19", "Earth", "♑️"),
    AQUARIUS("Aquarius", "Jan 20 - Feb 18", "Air", "♒️"),
    PISCES("Pisces", "Feb 19 - Mar 20", "Water", "♓️");

    companion object {
        /**
         * Get a zodiac sign by its display name (case-insensitive).
         */
        fun fromDisplayName(name: String): ZodiacSign? {
            return values().find { it.displayName.equals(name, ignoreCase = true) }
        }
    }
}
