package com.example.cosmicweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cosmicweather.data.local.database.WeatherDatabase
import com.example.cosmicweather.data.repository.WeatherRepository
import com.example.cosmicweather.domain.generator.HoroscopeGenerator
import com.example.cosmicweather.presentation.screen.CosmicWeatherScreen
import com.example.cosmicweather.presentation.viewmodel.CosmicWeatherViewModel
import com.example.cosmicweather.ui.theme.CosmicWeatherTheme

/**
 * Main activity - entry point for the Cosmic Weather app.
 * Sets up database, repository, horoscope generator, and launches the UI.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize database and repository
        val database = WeatherDatabase.getDatabase(applicationContext)
        val weatherRepository = WeatherRepository(database.weatherDao(), database)

        // Initialize horoscope generator
        val horoscopeGenerator = HoroscopeGenerator()

        setContent {
            CosmicWeatherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Create ViewModel with dependencies
                    val viewModel = viewModel<CosmicWeatherViewModel>(
                        factory = CosmicWeatherViewModelFactory(
                            weatherRepository = weatherRepository,
                            horoscopeGenerator = horoscopeGenerator
                        )
                    )

                    CosmicWeatherScreen(viewModel = viewModel)
                }
            }
        }
    }
}

/**
 * ViewModelFactory for manual dependency injection.
 * Creates CosmicWeatherViewModel with required dependencies.
 */
class CosmicWeatherViewModelFactory(
    private val weatherRepository: WeatherRepository,
    private val horoscopeGenerator: HoroscopeGenerator
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CosmicWeatherViewModel::class.java)) {
            return CosmicWeatherViewModel(weatherRepository, horoscopeGenerator) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}