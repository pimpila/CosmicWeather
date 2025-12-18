package com.example.cosmicweather.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for WeatherDatabase.
 * Tests our database population logic - the core custom logic we wrote.
 */
@RunWith(AndroidJUnit4::class)
class WeatherDatabaseTest {

    private lateinit var database: WeatherDatabase
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        // Use in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            context,
            WeatherDatabase::class.java
        ).allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun ensureDatabasePopulated_populatesEmptyDatabase() = runBlocking {
        // Given - empty database
        val dao = database.weatherDao()
        assertEquals(0, dao.getAllWeatherList().size)

        // When
        WeatherDatabase.ensureDatabasePopulated(database)

        // Then
        val scenarios = dao.getAllWeatherList()
        assertEquals(8, scenarios.size)
    }

    @Test
    fun ensureDatabasePopulated_isIdempotent() = runBlocking {
        // Given - populate once
        WeatherDatabase.ensureDatabasePopulated(database)
        val firstCount = database.weatherDao().getAllWeatherList().size

        // When - populate again
        WeatherDatabase.ensureDatabasePopulated(database)

        // Then - should still have same count (no duplicates)
        val secondCount = database.weatherDao().getAllWeatherList().size
        assertEquals(firstCount, secondCount)
        assertEquals(8, secondCount)
    }

    @Test
    fun ensureDatabasePopulated_createsAllEightScenariosWithCorrectData() = runBlocking {
        // When
        WeatherDatabase.ensureDatabasePopulated(database)

        // Then - verify all 8 scenarios exist with expected data
        val dao = database.weatherDao()

        // Sunny
        val sunny = dao.getWeatherById(1)
        assertNotNull(sunny)
        assertEquals("Sunny", sunny?.condition)
        assertEquals(25, sunny?.temperature)
        assertEquals("☀️", sunny?.emoji)
        assertEquals("energetic", sunny?.mood)

        // Rainy
        val rainy = dao.getWeatherById(2)
        assertNotNull(rainy)
        assertEquals("Rainy", rainy?.condition)
        assertEquals(15, rainy?.temperature)
        assertEquals("🌧️", rainy?.emoji)
        assertEquals("cozy", rainy?.mood)

        // Cloudy
        val cloudy = dao.getWeatherById(3)
        assertNotNull(cloudy)
        assertEquals("Cloudy", cloudy?.condition)
        assertEquals(18, cloudy?.temperature)
        assertEquals("☁️", cloudy?.emoji)
        assertEquals("contemplative", cloudy?.mood)

        // Snowy
        val snowy = dao.getWeatherById(4)
        assertNotNull(snowy)
        assertEquals("Snowy", snowy?.condition)
        assertEquals(0, snowy?.temperature)
        assertEquals("❄️", snowy?.emoji)
        assertEquals("romantic", snowy?.mood)

        // Foggy
        val foggy = dao.getWeatherById(5)
        assertNotNull(foggy)
        assertEquals("Foggy", foggy?.condition)
        assertEquals(12, foggy?.temperature)
        assertEquals("🌫️", foggy?.emoji)
        assertEquals("mysterious", foggy?.mood)

        // Stormy
        val stormy = dao.getWeatherById(6)
        assertNotNull(stormy)
        assertEquals("Stormy", stormy?.condition)
        assertEquals(14, stormy?.temperature)
        assertEquals("⛈️", stormy?.emoji)
        assertEquals("intense", stormy?.mood)

        // Clear Night
        val clearNight = dao.getWeatherById(7)
        assertNotNull(clearNight)
        assertEquals("Clear Night", clearNight?.condition)
        assertEquals(10, clearNight?.temperature)
        assertEquals("🌙", clearNight?.emoji)
        assertEquals("romantic", clearNight?.mood)

        // Windy
        val windy = dao.getWeatherById(8)
        assertNotNull(windy)
        assertEquals("Windy", windy?.condition)
        assertEquals(20, windy?.temperature)
        assertEquals("💨", windy?.emoji)
        assertEquals("restless", windy?.mood)
    }

    @Test
    fun database_versionIsTwo() {
        // When
        val version = database.openHelper.readableDatabase.version

        // Then
        assertEquals(2, version)
    }
}
