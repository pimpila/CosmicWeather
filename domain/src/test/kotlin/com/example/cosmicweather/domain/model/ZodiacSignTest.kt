package com.example.cosmicweather.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for ZodiacSign enum.
 */
class ZodiacSignTest {

    @Test
    fun `all twelve zodiac signs exist`() {
        // When
        val signs = ZodiacSign.entries

        // Then
        assertEquals(12, signs.size)
    }

    @Test
    fun `zodiac signs have correct elements`() {
        // Fire signs
        assertEquals("Fire", ZodiacSign.ARIES.element)
        assertEquals("Fire", ZodiacSign.LEO.element)
        assertEquals("Fire", ZodiacSign.SAGITTARIUS.element)

        // Earth signs
        assertEquals("Earth", ZodiacSign.TAURUS.element)
        assertEquals("Earth", ZodiacSign.VIRGO.element)
        assertEquals("Earth", ZodiacSign.CAPRICORN.element)

        // Air signs
        assertEquals("Air", ZodiacSign.GEMINI.element)
        assertEquals("Air", ZodiacSign.LIBRA.element)
        assertEquals("Air", ZodiacSign.AQUARIUS.element)

        // Water signs
        assertEquals("Water", ZodiacSign.CANCER.element)
        assertEquals("Water", ZodiacSign.SCORPIO.element)
        assertEquals("Water", ZodiacSign.PISCES.element)
    }

    @Test
    fun `each element has exactly three signs`() {
        // When
        val fireSigns = ZodiacSign.entries.filter { it.element == "Fire" }
        val earthSigns = ZodiacSign.entries.filter { it.element == "Earth" }
        val airSigns = ZodiacSign.entries.filter { it.element == "Air" }
        val waterSigns = ZodiacSign.entries.filter { it.element == "Water" }

        // Then
        assertEquals(3, fireSigns.size)
        assertEquals(3, earthSigns.size)
        assertEquals(3, airSigns.size)
        assertEquals(3, waterSigns.size)
    }

    @Test
    fun `zodiac signs have display names`() {
        assertEquals("Aries", ZodiacSign.ARIES.displayName)
        assertEquals("Taurus", ZodiacSign.TAURUS.displayName)
        assertEquals("Gemini", ZodiacSign.GEMINI.displayName)
        assertEquals("Cancer", ZodiacSign.CANCER.displayName)
        assertEquals("Leo", ZodiacSign.LEO.displayName)
        assertEquals("Virgo", ZodiacSign.VIRGO.displayName)
        assertEquals("Libra", ZodiacSign.LIBRA.displayName)
        assertEquals("Scorpio", ZodiacSign.SCORPIO.displayName)
        assertEquals("Sagittarius", ZodiacSign.SAGITTARIUS.displayName)
        assertEquals("Capricorn", ZodiacSign.CAPRICORN.displayName)
        assertEquals("Aquarius", ZodiacSign.AQUARIUS.displayName)
        assertEquals("Pisces", ZodiacSign.PISCES.displayName)
    }

    @Test
    fun `zodiac signs have date ranges`() {
        assertEquals("Mar 21 - Apr 19", ZodiacSign.ARIES.dateRange)
        assertEquals("Apr 20 - May 20", ZodiacSign.TAURUS.dateRange)
        assertEquals("May 21 - Jun 20", ZodiacSign.GEMINI.dateRange)
        assertEquals("Jun 21 - Jul 22", ZodiacSign.CANCER.dateRange)
        assertEquals("Jul 23 - Aug 22", ZodiacSign.LEO.dateRange)
        assertEquals("Aug 23 - Sep 22", ZodiacSign.VIRGO.dateRange)
        assertEquals("Sep 23 - Oct 22", ZodiacSign.LIBRA.dateRange)
        assertEquals("Oct 23 - Nov 21", ZodiacSign.SCORPIO.dateRange)
        assertEquals("Nov 22 - Dec 21", ZodiacSign.SAGITTARIUS.dateRange)
        assertEquals("Dec 22 - Jan 19", ZodiacSign.CAPRICORN.dateRange)
        assertEquals("Jan 20 - Feb 18", ZodiacSign.AQUARIUS.dateRange)
        assertEquals("Feb 19 - Mar 20", ZodiacSign.PISCES.dateRange)
    }

    @Test
    fun `zodiac signs have unicode symbols`() {
        assertEquals("♈️", ZodiacSign.ARIES.symbol)
        assertEquals("♉️", ZodiacSign.TAURUS.symbol)
        assertEquals("♊️", ZodiacSign.GEMINI.symbol)
        assertEquals("♋️", ZodiacSign.CANCER.symbol)
        assertEquals("♌️", ZodiacSign.LEO.symbol)
        assertEquals("♍️", ZodiacSign.VIRGO.symbol)
        assertEquals("♎️", ZodiacSign.LIBRA.symbol)
        assertEquals("♏️", ZodiacSign.SCORPIO.symbol)
        assertEquals("♐️", ZodiacSign.SAGITTARIUS.symbol)
        assertEquals("♑️", ZodiacSign.CAPRICORN.symbol)
        assertEquals("♒️", ZodiacSign.AQUARIUS.symbol)
        assertEquals("♓️", ZodiacSign.PISCES.symbol)
    }

    @Test
    fun `fromDisplayName returns correct sign for valid name`() {
        assertEquals(ZodiacSign.ARIES, ZodiacSign.fromDisplayName("Aries"))
        assertEquals(ZodiacSign.LEO, ZodiacSign.fromDisplayName("Leo"))
        assertEquals(ZodiacSign.SCORPIO, ZodiacSign.fromDisplayName("Scorpio"))
    }

    @Test
    fun `fromDisplayName is case insensitive`() {
        assertEquals(ZodiacSign.ARIES, ZodiacSign.fromDisplayName("aries"))
        assertEquals(ZodiacSign.ARIES, ZodiacSign.fromDisplayName("ARIES"))
        assertEquals(ZodiacSign.ARIES, ZodiacSign.fromDisplayName("ArIeS"))
    }

    @Test
    fun `fromDisplayName returns null for invalid name`() {
        assertNull(ZodiacSign.fromDisplayName("Invalid"))
        assertNull(ZodiacSign.fromDisplayName(""))
        assertNull(ZodiacSign.fromDisplayName("NotASign"))
    }

    @Test
    fun `all zodiac signs can be found by display name`() {
        ZodiacSign.entries.forEach { sign ->
            val found = ZodiacSign.fromDisplayName(sign.displayName)
            assertEquals(sign, found)
        }
    }

    @Test
    fun `zodiac signs are ordered correctly`() {
        val signs = ZodiacSign.entries

        // Verify astrological calendar order
        assertEquals(ZodiacSign.ARIES, signs[0])
        assertEquals(ZodiacSign.TAURUS, signs[1])
        assertEquals(ZodiacSign.GEMINI, signs[2])
        assertEquals(ZodiacSign.CANCER, signs[3])
        assertEquals(ZodiacSign.LEO, signs[4])
        assertEquals(ZodiacSign.VIRGO, signs[5])
        assertEquals(ZodiacSign.LIBRA, signs[6])
        assertEquals(ZodiacSign.SCORPIO, signs[7])
        assertEquals(ZodiacSign.SAGITTARIUS, signs[8])
        assertEquals(ZodiacSign.CAPRICORN, signs[9])
        assertEquals(ZodiacSign.AQUARIUS, signs[10])
        assertEquals(ZodiacSign.PISCES, signs[11])
    }
}
