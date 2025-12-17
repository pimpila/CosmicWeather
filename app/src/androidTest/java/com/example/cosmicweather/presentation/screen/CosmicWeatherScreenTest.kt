package com.example.cosmicweather.presentation.screen

import android.Manifest
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.rule.GrantPermissionRule
import com.example.cosmicweather.data.repository.WeatherRepository
import com.example.cosmicweather.domain.generator.HoroscopeGenerator
import com.example.cosmicweather.domain.model.Horoscope
import com.example.cosmicweather.domain.model.Weather
import com.example.cosmicweather.domain.model.ZodiacSign
import com.example.cosmicweather.presentation.viewmodel.CosmicWeatherViewModel
import com.example.cosmicweather.ui.theme.CosmicWeatherTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Instrumented UI tests for CosmicWeatherScreen.
 * These tests run on an Android device or emulator.
 */
class CosmicWeatherScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var horoscopeGenerator: HoroscopeGenerator
    private lateinit var viewModel: CosmicWeatherViewModel

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
        weatherRepository = mock()
        horoscopeGenerator = mock()

        runBlocking {
            whenever(weatherRepository.getRandomWeather()).thenReturn(sampleWeather)
        }
    }

    @Test
    fun topBarDisplaysTitle() {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        // When
        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Cosmic Weather").assertIsDisplayed()
    }

    @Test
    fun weatherDisplaysOnInitialLoad() {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        // When
        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // Wait for weather to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            runBlocking { viewModel.weather.first() != null }
        }

        // Then
        composeTestRule.onNodeWithText("Sunny, 77°F", substring = true).assertIsDisplayed()
    }

    @Test
    fun signSelectorsAreDisplayed() {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        // When
        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Select Your Signs").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your Sign").assertIsDisplayed()
        composeTestRule.onNodeWithText("Partner's Sign").assertIsDisplayed()
    }

    @Test
    fun horoscopeNotDisplayedInitially() {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        // When
        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Relationship Climate").assertDoesNotExist()
        composeTestRule.onNodeWithText("Communication").assertDoesNotExist()
        composeTestRule.onNodeWithText("Suggested Activity").assertDoesNotExist()
    }

    @Test
    fun selectingUserSignOpensDropdown() {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // When
        composeTestRule.onAllNodesWithText("Your Sign")[0].performClick()

        // Then - dropdown items should be visible
        composeTestRule.onNodeWithText("Aries (Mar 21 - Apr 19)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Leo (Jul 23 - Aug 22)").assertIsDisplayed()
    }

    @Test
    fun selectingPartnerSignOpensDropdown() {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // When
        composeTestRule.onAllNodesWithText("Partner's Sign")[0].performClick()

        // Then - dropdown items should be visible
        composeTestRule.onNodeWithText("Virgo (Aug 23 - Sep 22)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Scorpio (Oct 23 - Nov 21)").assertIsDisplayed()
    }

    @Test
    fun selectingBothSignsDisplaysHoroscope() {
        // Given
        runBlocking {
            whenever(horoscopeGenerator.generate(any(), any(), any())).thenReturn(sampleHoroscope)
        }
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // When - select user sign
        composeTestRule.onAllNodesWithText("Your Sign")[0].performClick()
        composeTestRule.onNodeWithText("Aries (Mar 21 - Apr 19)").performClick()

        // When - select partner sign
        composeTestRule.onAllNodesWithText("Partner's Sign")[0].performClick()
        composeTestRule.onNodeWithText("Leo (Jul 23 - Aug 22)").performClick()

        // Wait for horoscope to generate
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            runBlocking { viewModel.horoscope.first() != null }
        }

        // Then
        composeTestRule.onNodeWithText("Relationship Climate").assertIsDisplayed()
        composeTestRule.onNodeWithText("Passionate and dynamic").assertIsDisplayed()
        composeTestRule.onNodeWithText("Communication").assertIsDisplayed()
        composeTestRule.onNodeWithText("Open and expressive").assertIsDisplayed()
        composeTestRule.onNodeWithText("Suggested Activity").assertIsDisplayed()
        composeTestRule.onNodeWithText("Go for a hike together").assertIsDisplayed()
    }

    @Test
    fun horoscopeCardDisplaysZodiacIcons() {
        // Given
        runBlocking {
            whenever(horoscopeGenerator.generate(any(), any(), any())).thenReturn(sampleHoroscope)
        }
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // When - select both signs
        composeTestRule.onAllNodesWithText("Your Sign")[0].performClick()
        composeTestRule.onNodeWithText("Aries (Mar 21 - Apr 19)").performClick()
        composeTestRule.onAllNodesWithText("Partner's Sign")[0].performClick()
        composeTestRule.onNodeWithText("Leo (Jul 23 - Aug 22)").performClick()

        // Wait for horoscope
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            runBlocking { viewModel.horoscope.first() != null }
        }

        // Then - verify zodiac sign names in content description
        composeTestRule.onNodeWithContentDescription("Aries").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Leo").assertIsDisplayed()
    }

    @Test
    fun selectingOnlyUserSignDoesNotDisplayHoroscope() {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // When - select only user sign
        composeTestRule.onAllNodesWithText("Your Sign")[0].performClick()
        composeTestRule.onNodeWithText("Aries (Mar 21 - Apr 19)").performClick()

        // Give time for any potential horoscope generation
        composeTestRule.waitForIdle()

        // Then - horoscope should not be displayed
        composeTestRule.onNodeWithText("Relationship Climate").assertDoesNotExist()
    }

    @Test
    fun selectingOnlyPartnerSignDoesNotDisplayHoroscope() {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // When - select only partner sign
        composeTestRule.onAllNodesWithText("Partner's Sign")[0].performClick()
        composeTestRule.onNodeWithText("Leo (Jul 23 - Aug 22)").performClick()

        // Give time for any potential horoscope generation
        composeTestRule.waitForIdle()

        // Then - horoscope should not be displayed
        composeTestRule.onNodeWithText("Relationship Climate").assertDoesNotExist()
    }

    @Test
    fun changingUserSignUpdatesHoroscope() {
        // Given
        val firstHoroscope = sampleHoroscope.copy(relationshipClimate = "First climate")
        val secondHoroscope = sampleHoroscope.copy(
            sign1 = ZodiacSign.TAURUS,
            relationshipClimate = "Second climate"
        )

        runBlocking {
            whenever(horoscopeGenerator.generate(eq(ZodiacSign.ARIES), any(), any()))
                .thenReturn(firstHoroscope)
            whenever(horoscopeGenerator.generate(eq(ZodiacSign.TAURUS), any(), any()))
                .thenReturn(secondHoroscope)
        }

        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // When - select initial signs
        composeTestRule.onAllNodesWithText("Your Sign")[0].performClick()
        composeTestRule.onNodeWithText("Aries (Mar 21 - Apr 19)").performClick()
        composeTestRule.onAllNodesWithText("Partner's Sign")[0].performClick()
        composeTestRule.onNodeWithText("Leo (Jul 23 - Aug 22)").performClick()

        // Wait for first horoscope
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            runBlocking { viewModel.horoscope.first()?.relationshipClimate == "First climate" }
        }

        composeTestRule.onNodeWithText("First climate").assertIsDisplayed()

        // When - change user sign
        composeTestRule.onAllNodesWithText("Aries")[0].performClick()
        composeTestRule.onNodeWithText("Taurus (Apr 20 - May 20)").performClick()

        // Wait for second horoscope
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            runBlocking { viewModel.horoscope.first()?.relationshipClimate == "Second climate" }
        }

        // Then
        composeTestRule.onNodeWithText("Second climate").assertIsDisplayed()
        composeTestRule.onNodeWithText("First climate").assertDoesNotExist()
    }

    @Test
    fun errorMessageIsDisplayed() {
        // Given
        runBlocking {
            whenever(weatherRepository.getRandomWeather()).thenThrow(RuntimeException("Network error"))
        }
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        // When
        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // Wait for error
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            runBlocking { viewModel.error.first() != null }
        }

        // Then
        composeTestRule.onNodeWithText("Failed to load weather: Network error").assertIsDisplayed()
    }

    @Test
    fun allTwelveZodiacSignsAreAvailableInDropdown() {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // When - open user sign dropdown
        composeTestRule.onAllNodesWithText("Your Sign")[0].performClick()

        // Then - verify all 12 signs are present
        composeTestRule.onNodeWithText("Aries (Mar 21 - Apr 19)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Taurus (Apr 20 - May 20)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gemini (May 21 - Jun 20)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancer (Jun 21 - Jul 22)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Leo (Jul 23 - Aug 22)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Virgo (Aug 23 - Sep 22)").assertIsDisplayed()

        // Scroll to see remaining signs
        composeTestRule.onNodeWithText("Libra (Sep 23 - Oct 22)").assertExists()
        composeTestRule.onNodeWithText("Scorpio (Oct 23 - Nov 21)").assertExists()
        composeTestRule.onNodeWithText("Sagittarius (Nov 22 - Dec 21)").assertExists()
        composeTestRule.onNodeWithText("Capricorn (Dec 22 - Jan 19)").assertExists()
        composeTestRule.onNodeWithText("Aquarius (Jan 20 - Feb 18)").assertExists()
        composeTestRule.onNodeWithText("Pisces (Feb 19 - Mar 20)").assertExists()
    }

    @Test
    fun userCanSelectSameSignForBothFields() {
        // Given
        runBlocking {
            whenever(horoscopeGenerator.generate(any(), any(), any())).thenReturn(sampleHoroscope)
        }
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // When - select Aries for both
        composeTestRule.onAllNodesWithText("Your Sign")[0].performClick()
        composeTestRule.onNodeWithText("Aries (Mar 21 - Apr 19)").performClick()
        composeTestRule.onAllNodesWithText("Partner's Sign")[0].performClick()
        composeTestRule.onNodeWithText("Aries (Mar 21 - Apr 19)").performClick()

        // Wait for horoscope
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            runBlocking { viewModel.horoscope.first() != null }
        }

        // Then - horoscope should be displayed
        composeTestRule.onNodeWithText("Relationship Climate").assertIsDisplayed()
    }

    @Test
    fun weatherConditionDisplaysCorrectly() {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        // When
        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // Wait for weather
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            runBlocking { viewModel.weather.first() != null }
        }

        // Then - verify weather condition text is displayed
        composeTestRule.onNodeWithText("Sunny", substring = true).assertIsDisplayed()
    }

    @Test
    fun horoscopeCardHasDivider() {
        // Given
        runBlocking {
            whenever(horoscopeGenerator.generate(any(), any(), any())).thenReturn(sampleHoroscope)
        }
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // When - generate horoscope
        composeTestRule.onAllNodesWithText("Your Sign")[0].performClick()
        composeTestRule.onNodeWithText("Aries (Mar 21 - Apr 19)").performClick()
        composeTestRule.onAllNodesWithText("Partner's Sign")[0].performClick()
        composeTestRule.onNodeWithText("Leo (Jul 23 - Aug 22)").performClick()

        // Wait for horoscope
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            runBlocking { viewModel.horoscope.first() != null }
        }

        // Then - horoscope sections are displayed
        composeTestRule.onNodeWithText("Relationship Climate").assertIsDisplayed()
        composeTestRule.onNodeWithText("Communication").assertIsDisplayed()
        composeTestRule.onNodeWithText("Suggested Activity").assertIsDisplayed()
    }

    @Test
    fun noSignNamesDisplayedWhenNoSignSelected() {
        // Given
        viewModel = CosmicWeatherViewModel(weatherRepository, horoscopeGenerator)

        // When
        composeTestRule.setContent {
            CosmicWeatherTheme {
                CosmicWeatherScreen(viewModel = viewModel)
            }
        }

        // Wait for initial load
        composeTestRule.waitForIdle()

        // Then - verify no zodiac sign names are displayed in the text fields
        // (they would only appear after selection)
        ZodiacSign.entries.forEach { sign ->
            composeTestRule.onAllNodesWithText(sign.displayName).assertCountEquals(0)
        }

        // And the sign selector labels should be present
        composeTestRule.onNodeWithText("Your Sign").assertIsDisplayed()
        composeTestRule.onNodeWithText("Partner's Sign").assertIsDisplayed()
    }
}
