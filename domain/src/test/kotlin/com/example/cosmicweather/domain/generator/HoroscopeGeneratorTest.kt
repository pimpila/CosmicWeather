package com.example.cosmicweather.domain.generator

import com.example.cosmicweather.domain.model.Horoscope
import com.example.cosmicweather.domain.model.Weather
import com.example.cosmicweather.domain.model.ZodiacSign
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for HoroscopeGenerator.
 * Tests the core business logic of horoscope generation.
 */
class HoroscopeGeneratorTest {

    private lateinit var generator: HoroscopeGenerator

    private val sunnyWeather = Weather(
        id = 1,
        condition = "Sunny",
        temperature = 25,
        emoji = "☀️",
        mood = "energetic"
    )

    private val rainyWeather = Weather(
        id = 2,
        condition = "Rainy",
        temperature = 15,
        emoji = "🌧️",
        mood = "cozy"
    )

    private val stormyWeather = Weather(
        id = 6,
        condition = "Stormy",
        temperature = 14,
        emoji = "⛈️",
        mood = "intense"
    )

    @Before
    fun setup() {
        generator = HoroscopeGenerator()
    }

    @Test
    fun `generate returns valid horoscope with correct signs`() {
        // Given
        val sign1 = ZodiacSign.ARIES
        val sign2 = ZodiacSign.LEO

        // When
        val horoscope = generator.generate(sign1, sign2, sunnyWeather)

        // Then
        assertEquals(sign1, horoscope.sign1)
        assertEquals(sign2, horoscope.sign2)
        assertEquals(sunnyWeather, horoscope.weather)
    }

    @Test
    fun `generate returns non-empty relationship climate`() {
        // Given
        val sign1 = ZodiacSign.SCORPIO
        val sign2 = ZodiacSign.VIRGO

        // When
        val horoscope = generator.generate(sign1, sign2, rainyWeather)

        // Then
        assertNotNull(horoscope.relationshipClimate)
        assertTrue(horoscope.relationshipClimate.isNotEmpty())
    }

    @Test
    fun `generate returns non-empty communication advice`() {
        // Given
        val sign1 = ZodiacSign.GEMINI
        val sign2 = ZodiacSign.AQUARIUS

        // When
        val horoscope = generator.generate(sign1, sign2, sunnyWeather)

        // Then
        assertNotNull(horoscope.communication)
        assertTrue(horoscope.communication.isNotEmpty())
    }

    @Test
    fun `generate returns non-empty activity suggestion`() {
        // Given
        val sign1 = ZodiacSign.TAURUS
        val sign2 = ZodiacSign.CAPRICORN

        // When
        val horoscope = generator.generate(sign1, sign2, rainyWeather)

        // Then
        assertNotNull(horoscope.suggestedActivity)
        assertTrue(horoscope.suggestedActivity.isNotEmpty())
    }

    @Test
    fun `same element signs have naturally aligned communication`() {
        // Given - two Fire signs
        val aries = ZodiacSign.ARIES
        val leo = ZodiacSign.LEO

        // When
        val horoscope = generator.generate(aries, leo, sunnyWeather)

        // Then
        assertTrue(horoscope.communication.contains("naturally aligned"))
    }

    @Test
    fun `fire and air signs have energizing communication`() {
        // Given - Fire + Air
        val aries = ZodiacSign.ARIES
        val gemini = ZodiacSign.GEMINI

        // When
        val horoscope = generator.generate(aries, gemini, sunnyWeather)

        // Then
        assertTrue(horoscope.communication.contains("energizing and stimulating"))
    }

    @Test
    fun `earth and water signs have supportive communication`() {
        // Given - Earth + Water
        val taurus = ZodiacSign.TAURUS
        val cancer = ZodiacSign.CANCER

        // When
        val horoscope = generator.generate(taurus, cancer, sunnyWeather)

        // Then
        assertTrue(horoscope.communication.contains("supportive and grounding"))
    }

    @Test
    fun `fire and water signs require patience in communication`() {
        // Given - Fire + Water
        val aries = ZodiacSign.ARIES
        val pisces = ZodiacSign.PISCES

        // When
        val horoscope = generator.generate(aries, pisces, sunnyWeather)

        // Then
        assertTrue(horoscope.communication.contains("requiring patience and care"))
    }

    @Test
    fun `high energy signs get adventurous activities in sunny weather`() {
        // Given - Fire + Air signs (high energy)
        val sagittarius = ZodiacSign.SAGITTARIUS
        val libra = ZodiacSign.LIBRA

        // When
        val horoscope = generator.generate(sagittarius, libra, sunnyWeather)

        // Then
        val activity = horoscope.suggestedActivity.lowercase()
        assertTrue(
            activity.contains("adventurous") ||
            activity.contains("hike") ||
            activity.contains("sport") ||
            activity.contains("outdoor")
        )
    }

    @Test
    fun `grounded signs get peaceful activities in sunny weather`() {
        // Given - Earth + Water signs (grounded)
        val virgo = ZodiacSign.VIRGO
        val scorpio = ZodiacSign.SCORPIO

        // When
        val horoscope = generator.generate(virgo, scorpio, sunnyWeather)

        // Then
        val activity = horoscope.suggestedActivity.lowercase()
        assertTrue(
            activity.contains("peaceful") ||
            activity.contains("picnic") ||
            activity.contains("walk")
        )
    }

    @Test
    fun `grounded signs get cozy activities in rainy weather`() {
        // Given - Earth + Water signs
        val capricorn = ZodiacSign.CAPRICORN
        val cancer = ZodiacSign.CANCER

        // When
        val horoscope = generator.generate(capricorn, cancer, rainyWeather)

        // Then
        val activity = horoscope.suggestedActivity.lowercase()
        assertTrue(
            activity.contains("cook") ||
            activity.contains("meal") ||
            activity.contains("indoor") ||
            activity.contains("cozy")
        )
    }

    @Test
    fun `different weather moods produce different relationship climates`() {
        // Given - same sign pair, different weather
        val sign1 = ZodiacSign.LEO
        val sign2 = ZodiacSign.SAGITTARIUS

        // When
        val sunnyHoroscope = generator.generate(sign1, sign2, sunnyWeather)
        val rainyHoroscope = generator.generate(sign1, sign2, rainyWeather)

        // Then - should mention different energy
        assertNotEquals(sunnyHoroscope.relationshipClimate, rainyHoroscope.relationshipClimate)
    }

    @Test
    fun `different weather moods produce different activities`() {
        // Given - same sign pair, different weather
        val sign1 = ZodiacSign.TAURUS
        val sign2 = ZodiacSign.VIRGO

        // When
        val sunnyHoroscope = generator.generate(sign1, sign2, sunnyWeather)
        val rainyHoroscope = generator.generate(sign1, sign2, rainyWeather)

        // Then
        assertNotEquals(sunnyHoroscope.suggestedActivity, rainyHoroscope.suggestedActivity)
    }

    @Test
    fun `same sign pairing works correctly`() {
        // Given - same sign for both
        val aries = ZodiacSign.ARIES

        // When
        val horoscope = generator.generate(aries, aries, sunnyWeather)

        // Then
        assertNotNull(horoscope.relationshipClimate)
        assertNotNull(horoscope.communication)
        assertNotNull(horoscope.suggestedActivity)
    }

    @Test
    fun `all twelve zodiac signs work as first sign`() {
        // Given
        val partnerSign = ZodiacSign.LEO

        // When & Then
        ZodiacSign.entries.forEach { sign ->
            val horoscope = generator.generate(sign, partnerSign, sunnyWeather)
            assertNotNull("Failed for $sign", horoscope)
            assertTrue(horoscope.relationshipClimate.isNotEmpty())
            assertTrue(horoscope.communication.isNotEmpty())
            assertTrue(horoscope.suggestedActivity.isNotEmpty())
        }
    }

    @Test
    fun `all twelve zodiac signs work as second sign`() {
        // Given
        val userSign = ZodiacSign.ARIES

        // When & Then
        ZodiacSign.entries.forEach { sign ->
            val horoscope = generator.generate(userSign, sign, sunnyWeather)
            assertNotNull("Failed for $sign", horoscope)
            assertTrue(horoscope.relationshipClimate.isNotEmpty())
            assertTrue(horoscope.communication.isNotEmpty())
            assertTrue(horoscope.suggestedActivity.isNotEmpty())
        }
    }

    @Test
    fun `energetic weather produces outgoing communication tone`() {
        // Given - energetic weather (Sunny)
        val sign1 = ZodiacSign.LIBRA
        val sign2 = ZodiacSign.AQUARIUS

        // When
        val horoscope = generator.generate(sign1, sign2, sunnyWeather)

        // Then
        val communication = horoscope.communication.lowercase()
        assertTrue(
            communication.contains("open") ||
            communication.contains("perfect time")
        )
    }

    @Test
    fun `cozy weather produces thoughtful communication tone`() {
        // Given - cozy weather (Rainy)
        val sign1 = ZodiacSign.SCORPIO
        val sign2 = ZodiacSign.PISCES

        // When
        val horoscope = generator.generate(sign1, sign2, rainyWeather)

        // Then
        val communication = horoscope.communication.lowercase()
        assertTrue(
            communication.contains("thoughtful") ||
            communication.contains("reflect")
        )
    }

    @Test
    fun `intense weather produces mindful communication tone`() {
        // Given - intense weather (Stormy)
        val sign1 = ZodiacSign.ARIES
        val sign2 = ZodiacSign.LIBRA

        // When
        val horoscope = generator.generate(sign1, sign2, stormyWeather)

        // Then
        val communication = horoscope.communication.lowercase()
        assertTrue(
            communication.contains("intense") ||
            communication.contains("mindful") ||
            communication.contains("strong")
        )
    }

    @Test
    fun `relationship climate includes weather energy description`() {
        // Given
        val sign1 = ZodiacSign.GEMINI
        val sign2 = ZodiacSign.SAGITTARIUS

        // When
        val horoscope = generator.generate(sign1, sign2, sunnyWeather)

        // Then
        val climate = horoscope.relationshipClimate.lowercase()
        assertTrue(
            climate.contains("outgoing") ||
            climate.contains("social") ||
            climate.contains("energy")
        )
    }

    @Test
    fun `all 144 sign combinations generate valid horoscopes`() {
        // Given
        var successCount = 0

        // When - test all combinations
        ZodiacSign.entries.forEach { sign1 ->
            ZodiacSign.entries.forEach { sign2 ->
                val horoscope = generator.generate(sign1, sign2, sunnyWeather)

                // Then - each should be valid
                assertNotNull(horoscope)
                assertTrue(horoscope.relationshipClimate.isNotEmpty())
                assertTrue(horoscope.communication.isNotEmpty())
                assertTrue(horoscope.suggestedActivity.isNotEmpty())
                successCount++
            }
        }

        // Verify we tested all 144 combinations
        assertEquals(144, successCount)
    }

    @Test
    fun `water signs in romantic weather get emotional activity suggestions`() {
        // Given - Water signs with romantic weather
        val romanticWeather = Weather(7, "Clear Night", 10, "🌙", "romantic")
        val cancer = ZodiacSign.CANCER
        val pisces = ZodiacSign.PISCES

        // When
        val horoscope = generator.generate(cancer, pisces, romanticWeather)

        // Then
        val activity = horoscope.suggestedActivity.lowercase()
        assertTrue(
            activity.contains("candles") ||
            activity.contains("music") ||
            activity.contains("emotional") ||
            activity.contains("intimacy") ||
            activity.contains("romance")
        )
    }

    @Test
    fun `mixed element signs get balanced activity suggestions`() {
        // Given - Fire + Earth (balanced energy)
        val aries = ZodiacSign.ARIES
        val taurus = ZodiacSign.TAURUS

        // When
        val horoscope = generator.generate(aries, taurus, sunnyWeather)

        // Then
        val activity = horoscope.suggestedActivity.lowercase()
        assertTrue(
            activity.contains("balance") ||
            activity.contains("both") ||
            activity.contains("combines") ||
            activity.contains("leisurely")
        )
    }

    @Test
    fun `generator is stateless and produces consistent results`() {
        // Given
        val sign1 = ZodiacSign.VIRGO
        val sign2 = ZodiacSign.CAPRICORN

        // When - generate multiple times
        val horoscope1 = generator.generate(sign1, sign2, rainyWeather)
        val horoscope2 = generator.generate(sign1, sign2, rainyWeather)

        // Then - should be identical (stateless)
        assertEquals(horoscope1.relationshipClimate, horoscope2.relationshipClimate)
        assertEquals(horoscope1.communication, horoscope2.communication)
        assertEquals(horoscope1.suggestedActivity, horoscope2.suggestedActivity)
    }
}
