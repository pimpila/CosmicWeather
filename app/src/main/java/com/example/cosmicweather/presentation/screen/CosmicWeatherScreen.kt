package com.example.cosmicweather.presentation.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cosmicweather.R
import com.example.cosmicweather.domain.model.Horoscope
import com.example.cosmicweather.domain.model.Weather
import com.example.cosmicweather.domain.model.ZodiacSign
import com.example.cosmicweather.presentation.viewmodel.CosmicWeatherViewModel
import com.example.cosmicweather.ui.theme.CosmicWeatherTheme

/**
 * Maps zodiac signs to their vector drawable resources.
 */
@DrawableRes
private fun ZodiacSign.toDrawableRes(): Int = when (this) {
    ZodiacSign.ARIES -> R.drawable.ic_zodiac_aries
    ZodiacSign.TAURUS -> R.drawable.ic_zodiac_taurus
    ZodiacSign.GEMINI -> R.drawable.ic_zodiac_gemini
    ZodiacSign.CANCER -> R.drawable.ic_zodiac_cancer
    ZodiacSign.LEO -> R.drawable.ic_zodiac_leo
    ZodiacSign.VIRGO -> R.drawable.ic_zodiac_virgo
    ZodiacSign.LIBRA -> R.drawable.ic_zodiac_libra
    ZodiacSign.SCORPIO -> R.drawable.ic_zodiac_scorpio
    ZodiacSign.SAGITTARIUS -> R.drawable.ic_zodiac_sagittarius
    ZodiacSign.CAPRICORN -> R.drawable.ic_zodiac_capricorn
    ZodiacSign.AQUARIUS -> R.drawable.ic_zodiac_aquarius
    ZodiacSign.PISCES -> R.drawable.ic_zodiac_pisces
}

/**
 * Main Cosmic Weather screen.
 * Displays zodiac sign pickers, weather, and generated horoscope.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosmicWeatherScreen(
    viewModel: CosmicWeatherViewModel,
    modifier: Modifier = Modifier
) {
    val userSign by viewModel.userSign.collectAsState()
    val partnerSign by viewModel.partnerSign.collectAsState()
    val horoscope by viewModel.horoscope.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cosmic Weather",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.generateNewReading() },
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "New Reading"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Zodiac Sign Selectors
            SignSelectorsCard(
                userSign = userSign,
                partnerSign = partnerSign,
                onUserSignSelected = { viewModel.selectUserSign(it) },
                onPartnerSignSelected = { viewModel.selectPartnerSign(it) }
            )

            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(32.dp)
                )
            }

            // Error message
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Horoscope Content
            horoscope?.let { horo ->
                HoroscopeCard(horoscope = horo)
            }
        }
    }
}

/**
 * Card containing zodiac sign selectors.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignSelectorsCard(
    userSign: ZodiacSign,
    partnerSign: ZodiacSign,
    onUserSignSelected: (ZodiacSign) -> Unit,
    onPartnerSignSelected: (ZodiacSign) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Select Your Signs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // User sign selector
            ZodiacSignDropdown(
                label = "Your Sign",
                selectedSign = userSign,
                onSignSelected = onUserSignSelected
            )

            // Partner sign selector
            ZodiacSignDropdown(
                label = "Partner's Sign",
                selectedSign = partnerSign,
                onSignSelected = onPartnerSignSelected
            )
        }
    }
}

/**
 * Dropdown for selecting a zodiac sign.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZodiacSignDropdown(
    label: String,
    selectedSign: ZodiacSign,
    onSignSelected: (ZodiacSign) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val signs = ZodiacSign.values()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedSign.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            signs.forEach { sign ->
                DropdownMenuItem(
                    text = {
                        Text("${sign.displayName} (${sign.dateRange})")
                    },
                    onClick = {
                        onSignSelected(sign)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Card displaying the generated horoscope.
 */
@Composable
fun HoroscopeCard(
    horoscope: Horoscope,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sign pairing with zodiac icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(horoscope.sign1.toDrawableRes()),
                    contentDescription = horoscope.sign1.displayName,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "+",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    painter = painterResource(horoscope.sign2.toDrawableRes()),
                    contentDescription = horoscope.sign2.displayName,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Weather display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = horoscope.weather.emoji,
                    style = MaterialTheme.typography.displaySmall
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${horoscope.weather.condition}, ${horoscope.weather.temperature}°C",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            HorizontalDivider()

            // Relationship Climate
            HoroscopeSection(
                title = "Relationship Climate",
                content = horoscope.relationshipClimate
            )

            // Communication
            HoroscopeSection(
                title = "Communication",
                content = horoscope.communication
            )

            // Suggested Activity
            HoroscopeSection(
                title = "Suggested Activity",
                content = horoscope.suggestedActivity
            )
        }
    }
}

/**
 * Section within the horoscope card.
 */
@Composable
fun HoroscopeSection(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// ============================================================================
// Previews
// ============================================================================

/**
 * Preview of HoroscopeCard with sample data.
 */
@Preview(showBackground = true)
@Composable
fun HoroscopeCardPreview() {
    CosmicWeatherTheme {
        val sampleHoroscope = Horoscope(
            sign1 = ZodiacSign.SCORPIO,
            sign2 = ZodiacSign.VIRGO,
            weather = Weather(
                id = 2,
                condition = "Rainy",
                temperature = 15,
                emoji = "🌧️",
                mood = "cozy"
            ),
            relationshipClimate = "Intense but grounded with introspective and intimate energy",
            communication = "Thoughtful, possibly delayed - take time to reflect before speaking",
            suggestedActivity = "Cook something together and keep conversation low-pressure"
        )

        HoroscopeCard(horoscope = sampleHoroscope)
    }
}

/**
 * Preview of SignSelectorsCard.
 */
@Preview(showBackground = true)
@Composable
fun SignSelectorsCardPreview() {
    CosmicWeatherTheme {
        SignSelectorsCard(
            userSign = ZodiacSign.ARIES,
            partnerSign = ZodiacSign.LEO,
            onUserSignSelected = {},
            onPartnerSignSelected = {}
        )
    }
}

/**
 * Preview of HoroscopeCard with different weather.
 */
@Preview(showBackground = true, name = "Sunny Weather")
@Composable
fun HoroscopeCardSunnyPreview() {
    CosmicWeatherTheme {
        val sampleHoroscope = Horoscope(
            sign1 = ZodiacSign.ARIES,
            sign2 = ZodiacSign.LEO,
            weather = Weather(
                id = 1,
                condition = "Sunny",
                temperature = 25,
                emoji = "☀️",
                mood = "energetic"
            ),
            relationshipClimate = "Passionate and dynamic with outgoing and social energy",
            communication = "Open and expressive - perfect time for big conversations",
            suggestedActivity = "Go for a hike, have a picnic, or explore a new neighborhood"
        )

        HoroscopeCard(horoscope = sampleHoroscope)
    }
}

/**
 * Preview with dark theme.
 */
@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun HoroscopeCardDarkPreview() {
    CosmicWeatherTheme(darkTheme = true) {
        val sampleHoroscope = Horoscope(
            sign1 = ZodiacSign.PISCES,
            sign2 = ZodiacSign.SCORPIO,
            weather = Weather(
                id = 8,
                condition = "Clear Night",
                temperature = 10,
                emoji = "🌙",
                mood = "romantic"
            ),
            relationshipClimate = "Deeply intuitive with tender and affectionate energy",
            communication = "Gentle and affectionate - vulnerability is welcomed",
            suggestedActivity = "Set the mood with candles, music, and undivided attention"
        )

        HoroscopeCard(horoscope = sampleHoroscope)
    }
}
