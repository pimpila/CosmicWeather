package com.example.cosmicweather.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.cosmicweather.data.repository.WeatherRepository
import com.example.cosmicweather.domain.generator.HoroscopeGenerator
import com.example.cosmicweather.domain.model.Horoscope
import com.example.cosmicweather.domain.model.Weather
import com.example.cosmicweather.domain.model.ZodiacSign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for CosmicWeatherViewModel.
 * Tests all state management, sign selection, and horoscope generation logic.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CosmicWeatherViewModelTest {

    // Rule to execute LiveData operations instantly on the same thread
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test dispatcher for coroutines
    private val testDispatcher = StandardTestDispatcher()

    // Mocked dependencies
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var horoscopeGenerator: HoroscopeGenerator

    // ViewModel under test
    private lateinit var viewModel: CosmicWeatherViewModel

    // Sample test data
    private val sampleWeather = Weather(
        id = 1,
        condition = "Sunny",
        temperature = 25,
        emoji = "☀️",
        mood = "energetic"
    )

    private val sampleHoroscope = Horoscope(
        sign1 = ZodiacSign.ARIES,
        sign2 = ZodiacSign.LEO,
        weather = sampleWeather,
        relationshipClimate = "Passionate and dynamic",
        communication = "Open and expressive",
        suggestedActivity = "Go for a hike together"
    )

    @Before
    fun setup() {
        // Set main dispatcher to test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Create mocks
        weatherRepository = mock()
        horoscopeGenerator = mock()

        // Setup default mock behavior
        runTest {
            whenever(weatherRepository.getRandomWeather()).thenReturn(sampleWeather)
        }
    }

    @After
    fun tearDown() {
        // Reset main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have null signs and null horoscope`() = runTest {
        // Given & When
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()

        // Then
        assertNull(viewModel.userSign.value)
        assertNull(viewModel.partnerSign.value)
        assertNull(viewModel.horoscope.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `initial weather should be loaded on initialization`() = runTest {
        // Given & When
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()

        // Then
        assertEquals(sampleWeather, viewModel.weather.value)
        verify(weatherRepository, times(1)).getRandomWeather()
    }

    @Test
    fun `selecting user sign should update userSign state`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()

        // When
        viewModel.selectUserSign(ZodiacSign.ARIES)
        advanceUntilIdle()

        // Then
        assertEquals(ZodiacSign.ARIES, viewModel.userSign.value)
    }

    @Test
    fun `selecting partner sign should update partnerSign state`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()

        // When
        viewModel.selectPartnerSign(ZodiacSign.LEO)
        advanceUntilIdle()

        // Then
        assertEquals(ZodiacSign.LEO, viewModel.partnerSign.value)
    }

    @Test
    fun `selecting user sign only should not generate horoscope`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()

        // When
        viewModel.selectUserSign(ZodiacSign.ARIES)
        advanceUntilIdle()

        // Then
        assertNull(viewModel.horoscope.value)
        verify(horoscopeGenerator, never()).generate(any(), any(), any())
    }

    @Test
    fun `selecting partner sign only should not generate horoscope`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()

        // When
        viewModel.selectPartnerSign(ZodiacSign.LEO)
        advanceUntilIdle()

        // Then
        assertNull(viewModel.horoscope.value)
        verify(horoscopeGenerator, never()).generate(any(), any(), any())
    }

    @Test
    fun `selecting both signs should generate horoscope`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()
        whenever(horoscopeGenerator.generate(any(), any(), any())).thenReturn(sampleHoroscope)

        // When
        viewModel.selectUserSign(ZodiacSign.ARIES)
        advanceUntilIdle()
        viewModel.selectPartnerSign(ZodiacSign.LEO)
        advanceUntilIdle()

        // Then
        assertEquals(sampleHoroscope, viewModel.horoscope.value)
        verify(horoscopeGenerator).generate(ZodiacSign.ARIES, ZodiacSign.LEO, sampleWeather)
    }

    @Test
    fun `selecting both signs should use existing weather`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()
        whenever(horoscopeGenerator.generate(any(), any(), any())).thenReturn(sampleHoroscope)

        // Verify initial weather was loaded
        assertEquals(sampleWeather, viewModel.weather.value)

        // When
        viewModel.selectUserSign(ZodiacSign.ARIES)
        viewModel.selectPartnerSign(ZodiacSign.LEO)
        advanceUntilIdle()

        // Then - should use the same weather, not fetch new one
        assertEquals(sampleWeather, viewModel.weather.value)
        verify(weatherRepository, times(1)).getRandomWeather() // Only initial load
    }

    @Test
    fun `changing user sign should regenerate horoscope with new sign`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()
        whenever(horoscopeGenerator.generate(any(), any(), any())).thenReturn(sampleHoroscope)

        viewModel.selectUserSign(ZodiacSign.ARIES)
        viewModel.selectPartnerSign(ZodiacSign.LEO)
        advanceUntilIdle()

        // When - change user sign
        viewModel.selectUserSign(ZodiacSign.TAURUS)
        advanceUntilIdle()

        // Then
        verify(horoscopeGenerator).generate(ZodiacSign.TAURUS, ZodiacSign.LEO, sampleWeather)
    }

    @Test
    fun `changing partner sign should regenerate horoscope with new sign`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()
        whenever(horoscopeGenerator.generate(any(), any(), any())).thenReturn(sampleHoroscope)

        viewModel.selectUserSign(ZodiacSign.ARIES)
        viewModel.selectPartnerSign(ZodiacSign.LEO)
        advanceUntilIdle()

        // When - change partner sign
        viewModel.selectPartnerSign(ZodiacSign.VIRGO)
        advanceUntilIdle()

        // Then
        verify(horoscopeGenerator).generate(ZodiacSign.ARIES, ZodiacSign.VIRGO, sampleWeather)
    }

    @Test
    fun `generateNewReading should fetch new weather and regenerate horoscope`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()

        viewModel.selectUserSign(ZodiacSign.ARIES)
        viewModel.selectPartnerSign(ZodiacSign.LEO)
        advanceUntilIdle()

        val newWeather = Weather(2, "Rainy", 15, "🌧️", "cozy")
        whenever(weatherRepository.getRandomWeather()).thenReturn(newWeather)
        whenever(horoscopeGenerator.generate(any(), any(), any())).thenReturn(sampleHoroscope)

        // When
        viewModel.generateNewReading()
        advanceUntilIdle()

        // Then
        assertEquals(newWeather, viewModel.weather.value)
        verify(weatherRepository, times(2)).getRandomWeather() // Initial + new reading
        verify(horoscopeGenerator).generate(ZodiacSign.ARIES, ZodiacSign.LEO, newWeather)
    }

    @Test
    fun `generateNewReading should not generate horoscope if user sign is null`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()

        viewModel.selectPartnerSign(ZodiacSign.LEO)
        advanceUntilIdle()

        // When
        viewModel.generateNewReading()
        advanceUntilIdle()

        // Then
        verify(horoscopeGenerator, never()).generate(any(), any(), any())
    }

    @Test
    fun `generateNewReading should not generate horoscope if partner sign is null`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()

        viewModel.selectUserSign(ZodiacSign.ARIES)
        advanceUntilIdle()

        // When
        viewModel.generateNewReading()
        advanceUntilIdle()

        // Then
        verify(horoscopeGenerator, never()).generate(any(), any(), any())
    }

    @Test
    fun `loading state should be false after horoscope generation completes`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()
        whenever(horoscopeGenerator.generate(any(), any(), any())).thenReturn(sampleHoroscope)

        viewModel.selectUserSign(ZodiacSign.ARIES)
        advanceUntilIdle()

        // When
        viewModel.selectPartnerSign(ZodiacSign.LEO)
        advanceUntilIdle()

        // Then - after completion, loading should be false
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `error should be set when weather repository throws exception`() = runTest {
        // Given
        whenever(weatherRepository.getRandomWeather()).thenThrow(RuntimeException("Network error"))

        // When
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()

        // Then
        assertNotNull(viewModel.error.value)
        assertTrue(viewModel.error.value?.contains("Failed to load weather") == true)
    }

    @Test
    fun `error should be set when horoscope generation throws exception`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()
        whenever(horoscopeGenerator.generate(any(), any(), any()))
            .thenThrow(RuntimeException("Generation error"))

        // When
        viewModel.selectUserSign(ZodiacSign.ARIES)
        viewModel.selectPartnerSign(ZodiacSign.LEO)
        advanceUntilIdle()

        // Then
        assertNotNull(viewModel.error.value)
        assertTrue(viewModel.error.value?.contains("Failed to generate horoscope") == true)
    }

    @Test
    fun `clearError should set error to null`() = runTest {
        // Given
        whenever(weatherRepository.getRandomWeather()).thenThrow(RuntimeException("Network error"))
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()
        assertNotNull(viewModel.error.value)

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.error.value)
    }

    @Test
    fun `weather should be null when repository returns null`() = runTest {
        // Given
        whenever(weatherRepository.getRandomWeather()).thenReturn(null)

        // When
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()

        // Then
        assertNull(viewModel.weather.value)
    }

    @Test
    fun `horoscope generation should handle null weather gracefully`() = runTest {
        // Given
        whenever(weatherRepository.getRandomWeather()).thenReturn(null)
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()

        // When
        viewModel.selectUserSign(ZodiacSign.ARIES)
        viewModel.selectPartnerSign(ZodiacSign.LEO)
        advanceUntilIdle()

        // Then
        assertNotNull(viewModel.error.value)
        assertTrue(viewModel.error.value?.contains("Unable to load weather data") == true)
    }

    @Test
    fun `selecting same zodiac sign for both should still generate horoscope`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()
        whenever(horoscopeGenerator.generate(any(), any(), any())).thenReturn(sampleHoroscope)

        // When - both select ARIES
        viewModel.selectUserSign(ZodiacSign.ARIES)
        viewModel.selectPartnerSign(ZodiacSign.ARIES)
        advanceUntilIdle()

        // Then
        assertEquals(sampleHoroscope, viewModel.horoscope.value)
        verify(horoscopeGenerator).generate(ZodiacSign.ARIES, ZodiacSign.ARIES, sampleWeather)
    }

    @Test
    fun `all twelve zodiac signs should work correctly`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()
        whenever(horoscopeGenerator.generate(any(), any(), any())).thenReturn(sampleHoroscope)

        // When & Then - test each sign
        ZodiacSign.entries.forEach { sign ->
            viewModel.selectUserSign(sign)
            advanceUntilIdle()
            assertEquals(sign, viewModel.userSign.value)
        }

        ZodiacSign.entries.forEach { sign ->
            viewModel.selectPartnerSign(sign)
            advanceUntilIdle()
            assertEquals(sign, viewModel.partnerSign.value)
        }
    }

    @Test
    fun `multiple rapid sign changes should use latest signs`() = runTest {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)
        advanceUntilIdle()
        whenever(horoscopeGenerator.generate(any(), any(), any())).thenReturn(sampleHoroscope)

        // When - rapidly change signs
        viewModel.selectUserSign(ZodiacSign.ARIES)
        viewModel.selectUserSign(ZodiacSign.TAURUS)
        viewModel.selectUserSign(ZodiacSign.GEMINI)
        viewModel.selectPartnerSign(ZodiacSign.LEO)
        viewModel.selectPartnerSign(ZodiacSign.VIRGO)
        advanceUntilIdle()

        // Then - should use latest selections
        assertEquals(ZodiacSign.GEMINI, viewModel.userSign.value)
        assertEquals(ZodiacSign.VIRGO, viewModel.partnerSign.value)
    }
}
