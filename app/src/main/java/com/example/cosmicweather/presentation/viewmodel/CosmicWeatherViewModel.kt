package com.example.cosmicweather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosmicweather.data.repository.WeatherRepository
import com.example.cosmicweather.domain.generator.HoroscopeGenerator
import com.example.cosmicweather.domain.model.Horoscope
import com.example.cosmicweather.domain.model.ZodiacSign
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

    // Generated horoscope
    private val _horoscope = MutableStateFlow<Horoscope?>(null)
    val horoscope: StateFlow<Horoscope?> = _horoscope.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Update user's zodiac sign and regenerate horoscope if both signs are selected.
     */
    fun selectUserSign(sign: ZodiacSign) {
        _userSign.value = sign
        // Only generate if both signs are selected
        if (_partnerSign.value != null) {
            generateNewReading()
        }
    }

    /**
     * Update partner's zodiac sign and regenerate horoscope if both signs are selected.
     */
    fun selectPartnerSign(sign: ZodiacSign) {
        _partnerSign.value = sign
        // Only generate if both signs are selected
        if (_userSign.value != null) {
            generateNewReading()
        }
    }

    /**
     * Generate a new horoscope reading with random weather.
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
