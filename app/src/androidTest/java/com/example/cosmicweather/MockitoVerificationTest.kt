package com.example.cosmicweather

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cosmicweather.data.repository.WeatherRepository
import com.example.cosmicweather.domain.generator.HoroscopeGenerator
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock

/**
 * Simple test to verify Mockito can mock Kotlin final classes.
 */
@RunWith(AndroidJUnit4::class)
class MockitoVerificationTest {

    @Test
    fun mockitoCanMockFinalClasses() {
        // This should not throw an exception if mockito-android is configured correctly
        val repository: WeatherRepository = mock()
        val generator: HoroscopeGenerator = mock()

        // If we get here, mocking works!
        assert(repository != null)
        assert(generator != null)
    }
}
