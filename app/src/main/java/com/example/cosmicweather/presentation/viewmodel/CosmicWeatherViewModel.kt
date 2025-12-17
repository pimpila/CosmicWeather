package com.example.cosmicweather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosmicweather.data.repository.WeatherRepository
import com.example.cosmicweather.domain.generator.HoroscopeGenerator
import com.example.cosmicweather.domain.model.Horoscope
import com.example.cosmicweather.domain.model.Weather
import com.example.cosmicweather.domain.model.ZodiacSign
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Cosmic Weather screen.
 * Manages zodiac sign selection, weather state, and horoscope generation.
 */
class CosmicWeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val horoscopeGenerator: HoroscopeGenerator
) : ViewModel() {

    // User's zodiac sign (nullable - starts as null)
    private val _userSign = MutableStateFlow<ZodiacSign?>(null)
    val userSign: StateFlow<ZodiacSign?> = _userSign.asStateFlow()

    // Partner's zodiac sign (nullable - starts as null)
    private val _partnerSign = MutableStateFlow<ZodiacSign?>(null)
    val partnerSign: StateFlow<ZodiacSign?> = _partnerSign.asStateFlow()

    // Current weather (for background display)
    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather.asStateFlow()

    // Generated horoscope
    private val _horoscope = MutableStateFlow<Horoscope?>(null)
    val horoscope: StateFlow<Horoscope?> = _horoscope.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Load initial weather for background
        loadInitialWeather()
    }

    /**
     * Load initial weather on startup for background display.
     * Retries if database is not populated yet.
     */
    private fun loadInitialWeather() {
        viewModelScope.launch {
            try {
                var weather = weatherRepository.getRandomWeather()
                var attempts = 0

                // Retry if null (database might still be populating)
                while (weather == null && attempts < 5) {
                    delay(200) // Wait 200ms
                    weather = weatherRepository.getRandomWeather()
                    attempts++
                }

                _weather.value = weather
            } catch (e: Exception) {
                // Silently fail - background will use default
            }
        }
    }

    /**
     * Update user's zodiac sign and regenerate horoscope if both signs are selected.
     */
    fun selectUserSign(sign: ZodiacSign) {
        _userSign.value = sign
        // Only generate if both signs are selected
        if (_partnerSign.value != null) {
            generateHoroscope()
        }
    }

    /**
     * Update partner's zodiac sign and regenerate horoscope if both signs are selected.
     */
    fun selectPartnerSign(sign: ZodiacSign) {
        _partnerSign.value = sign
        // Only generate if both signs are selected
        if (_userSign.value != null) {
            generateHoroscope()
        }
    }

    /**
     * Generate horoscope with current weather (used when signs are selected).
     * Uses the existing weather to keep the background consistent.
     */
    private fun generateHoroscope() {
        val userSignValue = _userSign.value
        val partnerSignValue = _partnerSign.value

        // Both signs must be selected to generate horoscope
        if (userSignValue == null || partnerSignValue == null) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                var currentWeather = _weather.value
                var attempts = 0

                // Wait for weather to load if null (database might still be populating)
                while (currentWeather == null && attempts < 5) {
                    delay(200)
                    currentWeather = weatherRepository.getRandomWeather()
                    if (currentWeather != null) {
                        _weather.value = currentWeather
                    }
                    attempts++
                }

                if (currentWeather == null) {
                    _error.value = "Unable to load weather data"
                    return@launch
                }

                // Generate horoscope with existing weather
                val newHoroscope = horoscopeGenerator.generate(
                    sign1 = userSignValue,
                    sign2 = partnerSignValue,
                    weather = currentWeather
                )
                _horoscope.value = newHoroscope
            } catch (e: Exception) {
                _error.value = "Failed to generate horoscope: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Generate a new horoscope reading with random weather.
     * Gets new random weather and updates both background and horoscope.
     * Only generates if both signs are selected.
     */
    fun generateNewReading() {
        val userSignValue = _userSign.value
        val partnerSignValue = _partnerSign.value

        // Both signs must be selected to generate horoscope
        if (userSignValue == null || partnerSignValue == null) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Get random weather from repository
                val weather = weatherRepository.getRandomWeather()

                if (weather != null) {
                    // Update weather state for background
                    _weather.value = weather

                    // Generate horoscope
                    val newHoroscope = horoscopeGenerator.generate(
                        sign1 = userSignValue,
                        sign2 = partnerSignValue,
                        weather = weather
                    )
                    _horoscope.value = newHoroscope
                } else {
                    _error.value = "No weather data available"
                }
            } catch (e: Exception) {
                _error.value = "Failed to generate horoscope: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        _error.value = null
    }
}
