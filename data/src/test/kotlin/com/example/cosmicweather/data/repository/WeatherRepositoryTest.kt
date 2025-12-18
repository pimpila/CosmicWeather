package com.example.cosmicweather.data.repository

import com.example.cosmicweather.data.local.dao.WeatherDao
import com.example.cosmicweather.data.local.database.WeatherDatabase
import com.example.cosmicweather.data.local.entity.WeatherConditionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Unit tests for WeatherRepository.
 * Tests entity-to-domain mapping logic with mocked DAO.
 * Note: Database initialization is tested in WeatherDatabaseTest.
 */
class WeatherRepositoryTest {

    private lateinit var weatherDao: WeatherDao
    private lateinit var database: WeatherDatabase
    private lateinit var repository: WeatherRepository

    private val sunnyEntity = WeatherConditionEntity(1, "Sunny", 25, "☀️", "energetic")
    private val rainyEntity = WeatherConditionEntity(2, "Rainy", 15, "🌧️", "cozy")

    @Before
    fun setup() {
        weatherDao = mock()
        database = mock()

        // Mock database.weatherDao() to return our mocked DAO (needed for ensureInitialized)
        whenever(database.weatherDao()).thenReturn(weatherDao)

        // Stub getAllWeatherList to simulate database already populated (avoids initialization)
        runBlocking {
            whenever(weatherDao.getAllWeatherList()).thenReturn(listOf(sunnyEntity))
        }

        repository = WeatherRepository(weatherDao, database)
    }

    @Test
    fun getRandomWeather_mapsEntityToDomain() = runBlocking {
        // Given
        whenever(weatherDao.getRandomWeather()).thenReturn(sunnyEntity)

        // When
        val weather = repository.getRandomWeather()

        // Then
        assertNotNull(weather)
        assertEquals(1, weather?.id)
        assertEquals("Sunny", weather?.condition)
        assertEquals(25, weather?.temperature)
        assertEquals("☀️", weather?.emoji)
        assertEquals("energetic", weather?.mood)
    }

    @Test
    fun getRandomWeather_returnsNullWhenDaoReturnsNull() = runBlocking {
        // Given
        whenever(weatherDao.getRandomWeather()).thenReturn(null)

        // When
        val weather = repository.getRandomWeather()

        // Then
        assertNull(weather)
    }

    @Test
    fun getWeatherById_mapsEntityToDomain() = runBlocking {
        // Given
        whenever(weatherDao.getWeatherById(2)).thenReturn(rainyEntity)

        // When
        val weather = repository.getWeatherById(2)

        // Then
        assertNotNull(weather)
        assertEquals(2, weather?.id)
        assertEquals("Rainy", weather?.condition)
        assertEquals(15, weather?.temperature)
        assertEquals("🌧️", weather?.emoji)
        assertEquals("cozy", weather?.mood)
    }

    @Test
    fun getWeatherById_returnsNullWhenDaoReturnsNull() = runBlocking {
        // Given
        whenever(weatherDao.getWeatherById(999)).thenReturn(null)

        // When
        val weather = repository.getWeatherById(999)

        // Then
        assertNull(weather)
    }

    @Test
    fun getAllWeatherScenarios_mapsEntitiesListToDomainList() = runBlocking {
        // Given
        val entities = listOf(sunnyEntity, rainyEntity)
        whenever(weatherDao.getAllWeather()).thenReturn(flowOf(entities))

        // When
        val weatherList = repository.getAllWeatherScenarios().first()

        // Then
        assertEquals(2, weatherList.size)

        assertEquals(1, weatherList[0].id)
        assertEquals("Sunny", weatherList[0].condition)

        assertEquals(2, weatherList[1].id)
        assertEquals("Rainy", weatherList[1].condition)
    }

    @Test
    fun getAllWeatherScenarios_mapsEmptyListCorrectly() = runBlocking {
        // Given
        whenever(weatherDao.getAllWeather()).thenReturn(flowOf(emptyList()))

        // When
        val weatherList = repository.getAllWeatherScenarios().first()

        // Then
        assertEquals(0, weatherList.size)
    }
}
