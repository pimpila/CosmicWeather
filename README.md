# Cosmic Weather ☀️✨

A unique Android app that combines zodiac sign compatibility with current weather conditions to generate personalized relationship horoscopes and activity suggestions.

## Overview

Cosmic Weather creates dynamic relationship insights by blending astrological compatibility with weather influences. Users select their zodiac sign and their partner's sign, then receive personalized advice that adapts to current weather conditions.

### Example Output
```
Scorpio + Virgo
Sunny, 77°F
San Francisco, CA
Tuesday, December 17, 2024

Relationship Climate: Intense but grounded with outgoing and social energy
Communication: Thoughtful and supportive and grounding - take time to reflect before speaking
Suggested Activity: Take a peaceful nature walk or have a picnic in the park
```

## Features

✨ **Zodiac Compatibility** - All 144 zodiac sign pair combinations
🌤️ **Weather Integration** - 8 different weather scenarios (Sunny, Rainy, Cloudy, Snowy, Foggy, Stormy, Clear Night, Windy)
🎨 **Dynamic Backgrounds** - Beautiful weather-specific background images
📱 **Material You Design** - Dynamic theming that adapts to your device
🎭 **Animated Transitions** - Smooth horoscope card animations
📍 **Location Detection** - Displays your city using device location
🔮 **Smart Advice** - Context-aware suggestions based on sign elements and weather

## Architecture

The app follows **Clean Architecture** principles with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (UI, ViewModels, Compose Screens)     │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│          Domain Layer                   │
│   (Models, Business Logic, Generator)  │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│           Data Layer                    │
│  (Repository, Room Database, DAOs)     │
└─────────────────────────────────────────┘
```

### Module Structure

```
CosmicWeather/
├── app/                      # Application module
│   ├── presentation/        # UI layer (Compose screens, ViewModels)
│   └── ui/                  # Theme, colors, typography
├── domain/                  # Business logic module
│   ├── model/              # Domain models (ZodiacSign, Weather, Horoscope)
│   ├── generator/          # HoroscopeGenerator (core business logic)
│   └── template/           # Compatibility templates and data
└── data/                   # Data access module
    ├── repository/         # WeatherRepository
    ├── local/
    │   ├── entity/        # Room entities
    │   ├── dao/           # Room DAOs
    │   └── database/      # Room database
    └── mapper/            # Entity to domain model mappers
```

## Key Components

### Domain Layer

#### ZodiacSign (Enum)
Represents the 12 zodiac signs with their properties:
```kotlin
enum class ZodiacSign(
    val displayName: String,
    val dateRange: String,
    val element: String,      // Fire, Earth, Air, Water
    val symbol: String        // Unicode symbol ♈️
)
```

**Elements:**
- 🔥 **Fire** (Aries, Leo, Sagittarius) - Passionate, energetic, spontaneous
- 🌍 **Earth** (Taurus, Virgo, Capricorn) - Practical, grounded, reliable
- 💨 **Air** (Gemini, Libra, Aquarius) - Intellectual, social, communicative
- 💧 **Water** (Cancer, Scorpio, Pisces) - Emotional, intuitive, nurturing

#### Weather (Data Class)
```kotlin
data class Weather(
    val id: Int,
    val condition: String,    // "Sunny", "Rainy", etc.
    val temperature: Int,     // Celsius
    val emoji: String,        // Weather emoji
    val mood: String          // Weather mood for horoscope influence
)
```

**Weather Moods:**
- `energetic` - Sunny weather, encourages outdoor activities
- `cozy` - Rainy weather, promotes indoor bonding
- `contemplative` - Cloudy weather, thoughtful atmosphere
- `serene` - Clear night, peaceful energy
- `mysterious` - Foggy weather, enigmatic mood
- `intense` - Stormy weather, emotionally charged
- `romantic` - Snowy weather, tender and affectionate
- `restless` - Windy weather, dynamic and changeable

#### Horoscope (Data Class)
```kotlin
data class Horoscope(
    val sign1: ZodiacSign,
    val sign2: ZodiacSign,
    val weather: Weather,
    val relationshipClimate: String,    // Overall compatibility + weather
    val communication: String,           // Communication style advice
    val suggestedActivity: String        // Activity recommendation
)
```

#### HoroscopeGenerator
**Location:** `domain/src/main/kotlin/com/example/cosmicweather/domain/generator/HoroscopeGenerator.kt`

Core business logic that generates personalized horoscopes by combining:
1. **Zodiac Compatibility** - Base compatibility from sign elements
2. **Weather Influence** - Current weather mood and energy
3. **Element Interactions** - How Fire/Earth/Air/Water elements interact

**Key Methods:**

```kotlin
fun generate(
    sign1: ZodiacSign,
    sign2: ZodiacSign,
    weather: Weather
): Horoscope
```

**Algorithm:**
1. Get zodiac compatibility from templates (element + archetype)
2. Get weather influence (mood, energy, activity type)
3. Generate relationship climate by combining base + weather
4. Generate communication advice based on element compatibility
5. Generate activity suggestion based on sign energy + weather + activity type

**Example Logic:**
```kotlin
// Element compatibility determines communication style
Fire + Water → "requiring patience and care"
Earth + Water → "supportive and grounding"
Air + Fire → "energizing and stimulating"

// Sign energy determines activity suggestions
Fire + Air signs → "high-energy" → adventurous activities
Earth + Water signs → "grounded" → peaceful, cozy activities
Mixed elements → "balanced" → honors both energies
```

### Data Layer

#### WeatherDatabase (Room)
**Location:** `data/src/main/java/com/example/cosmicweather/data/local/database/WeatherDatabase.kt`

Room database pre-populated with 8 weather scenarios:

```kotlin
@Database(entities = [WeatherConditionEntity::class], version = 2)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        suspend fun ensureDatabasePopulated(database: WeatherDatabase)
    }
}
```

**Weather Scenarios:**
1. Sunny (25°C, energetic)
2. Rainy (15°C, cozy)
3. Cloudy (18°C, contemplative)
4. Snowy (0°C, romantic)
5. Foggy (12°C, mysterious)
6. Stormy (14°C, intense)
7. Clear Night (10°C, romantic)
8. Windy (20°C, restless)

#### WeatherRepository
**Location:** `data/src/main/java/com/example/cosmicweather/data/repository/WeatherRepository.kt`

Abstracts data access from the rest of the app:

```kotlin
class WeatherRepository(
    private val weatherDao: WeatherDao,
    private val database: WeatherDatabase
) {
    suspend fun getRandomWeather(): Weather?
    fun getAllWeatherScenarios(): Flow<List<Weather>>
    suspend fun getWeatherById(id: Int): Weather?
}
```

### Presentation Layer

#### CosmicWeatherViewModel
**Location:** `app/src/main/java/com/example/cosmicweather/presentation/viewmodel/CosmicWeatherViewModel.kt`

Manages UI state and orchestrates horoscope generation:

**State Management:**
```kotlin
// User selections
val userSign: StateFlow<ZodiacSign?>
val partnerSign: StateFlow<ZodiacSign?>

// Current weather (for background)
val weather: StateFlow<Weather?>

// Generated horoscope
val horoscope: StateFlow<Horoscope?>

// UI states
val isLoading: StateFlow<Boolean>
val error: StateFlow<String?>
```

**Key Methods:**
```kotlin
fun selectUserSign(sign: ZodiacSign)        // Updates user sign
fun selectPartnerSign(sign: ZodiacSign)     // Updates partner sign
fun generateNewReading()                     // Fetches new weather & regenerates
fun clearError()                             // Clears error state
```

**Business Rules:**
- Initial weather loads on startup (for background display)
- Horoscope only generates when BOTH signs are selected
- Selecting signs uses existing weather (keeps background consistent)
- "New Reading" button fetches new random weather
- All operations are async using Kotlin coroutines

#### CosmicWeatherScreen
**Location:** `app/src/main/java/com/example/cosmicweather/presentation/screen/CosmicWeatherScreen.kt`

Main UI screen built with Jetpack Compose:

**Components:**
1. **TopAppBar** - Displays app title
2. **Weather Display** - Shows condition, temperature, location, date (white text overlay)
3. **SignSelectorsCard** - Two dropdown selectors for zodiac signs
4. **AnimatedHoroscopeCard** - Horoscope content with fade animation
5. **Background** - Dynamic weather-specific images

**UI States:**
- **Initial:** Weather background + Sign selectors centered
- **Signs Selected:** Weather display + Selectors + Animated horoscope card
- **Loading:** CircularProgressIndicator
- **Error:** Error card with message

**Key Composables:**
```kotlin
@Composable
fun CosmicWeatherScreen(viewModel: CosmicWeatherViewModel)

@Composable
fun SignSelectorsCard(
    userSign: ZodiacSign?,
    partnerSign: ZodiacSign?,
    onUserSignSelected: (ZodiacSign) -> Unit,
    onPartnerSignSelected: (ZodiacSign) -> Unit
)

@Composable
fun HoroscopeCard(horoscope: Horoscope)

@Composable
fun AnimatedHoroscopeCard(horoscope: Horoscope?)
```

**Animations:**
- Horoscope card fades in (600ms) when both signs selected
- Horoscope card fades between different horoscopes (600ms in, 400ms out)
- Uses `AnimatedContent` for smooth transitions

## Data Flow

### Initial App Launch
```
1. MainActivity creates WeatherDatabase
2. Database populates with weather scenarios (if empty)
3. WeatherRepository initialized with database
4. HoroscopeGenerator created
5. ViewModel initialized with repository + generator
6. ViewModel.init loads random weather for background
7. Screen displays with weather background + empty sign selectors
```

### Selecting Signs
```
1. User clicks "Your Sign" dropdown
2. User selects a zodiac sign (e.g., Aries)
3. ViewModel.selectUserSign(ARIES) called
4. State updated: userSign = ARIES
5. If partnerSign is null → stop (wait for both)
6. User selects "Partner's Sign" (e.g., Leo)
7. ViewModel.selectPartnerSign(LEO) called
8. State updated: partnerSign = LEO
9. Both signs selected → generateHoroscope() called
10. Uses existing weather from state
11. HoroscopeGenerator.generate(ARIES, LEO, weather)
    - Gets compatibility template
    - Gets weather influence
    - Generates relationship climate
    - Generates communication advice
    - Generates activity suggestion
12. State updated: horoscope = generated horoscope
13. UI recomposes → HoroscopeCard animates in
```

### Changing Sign
```
1. User changes selection (e.g., Aries → Taurus)
2. ViewModel.selectUserSign(TAURUS) called
3. State updated: userSign = TAURUS
4. generateHoroscope() called (both signs still selected)
5. New horoscope generated with updated sign
6. State updated: horoscope = new horoscope
7. UI recomposes → HoroscopeCard fades to new content
```

## Dependency Injection

The app uses **manual dependency injection** (no Hilt/Dagger) for simplicity:

```kotlin
// MainActivity.kt
val database = WeatherDatabase.getDatabase(applicationContext)
val weatherRepository = WeatherRepository(database.weatherDao(), database)
val horoscopeGenerator = HoroscopeGenerator()

val viewModel = viewModel<CosmicWeatherViewModel>(
    factory = CosmicWeatherViewModelFactory(
        weatherRepository = weatherRepository,
        horoscopeGenerator = horoscopeGenerator
    )
)
```

## Building & Running

### Prerequisites
- Android Studio Ladybug or later
- Android SDK 24+ (Android 7.0+)
- Kotlin 2.0.21
- JDK 11

### Build Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug

# Build and run
./gradlew build
```

### Run Configurations
**From Android Studio:**
1. Open project in Android Studio
2. Select "app" run configuration
3. Choose device/emulator
4. Click Run ▶️

**From Command Line:**
```bash
# Install and launch
./gradlew installDebug
adb shell am start -n com.example.cosmicweather/.MainActivity
```

## Testing

The app includes comprehensive testing coverage. See [TESTING.md](TESTING.md) for detailed testing documentation.

**Summary:**
- ✅ 22 unit tests (ViewModel logic)
- ✅ 18 instrumented UI tests (Compose screens)
- ✅ Automatic permission handling in tests
- ✅ Mocking support for Kotlin final classes

**Run Tests:**
```bash
# Unit tests
./gradlew test

# Instrumented tests (requires device/emulator)
./gradlew connectedDebugAndroidTest

# All tests
./gradlew build
```

## Project Structure

```
CosmicWeather/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/cosmicweather/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── screen/
│   │   │   │   │   │   └── CosmicWeatherScreen.kt
│   │   │   │   │   └── viewmodel/
│   │   │   │   │       └── CosmicWeatherViewModel.kt
│   │   │   │   └── ui/
│   │   │   │       └── theme/
│   │   │   │           ├── Color.kt
│   │   │   │           ├── Theme.kt
│   │   │   │           └── Type.kt
│   │   │   ├── res/
│   │   │   │   ├── drawable/              # Zodiac icons (SVG vectors)
│   │   │   │   ├── drawable-nodpi/        # Weather backgrounds (PNG)
│   │   │   │   └── values/
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                          # Unit tests
│   │   └── androidTest/                   # Instrumented tests
│   └── build.gradle.kts
├── domain/
│   └── src/main/kotlin/com/example/cosmicweather/domain/
│       ├── model/
│       │   ├── ZodiacSign.kt
│       │   ├── Weather.kt
│       │   └── Horoscope.kt
│       ├── generator/
│       │   └── HoroscopeGenerator.kt
│       └── template/
│           └── HoroscopeTemplates.kt
├── data/
│   └── src/main/java/com/example/cosmicweather/data/
│       ├── repository/
│       │   └── WeatherRepository.kt
│       └── local/
│           ├── entity/
│           │   └── WeatherConditionEntity.kt
│           ├── dao/
│           │   └── WeatherDao.kt
│           └── database/
│               └── WeatherDatabase.kt
├── gradle/
│   └── libs.versions.toml              # Centralized dependency versions
├── TESTING.md                          # Testing documentation
└── README.md                           # This file
```

## Technologies Used

### Core
- **Kotlin** 2.0.21 - Modern, concise, safe language
- **Jetpack Compose** - Declarative UI framework
- **Material 3** - Latest Material Design components
- **Coroutines** 1.9.0 - Async programming

### Architecture Components
- **ViewModel** - UI state management
- **StateFlow** - Reactive state holder
- **Room** 2.6.1 - Local database (SQLite wrapper)
- **KSP** - Kotlin Symbol Processing for Room

### Other
- **Google Play Services Location** 21.3.0 - Device location
- **Mockito** 5.8.0 - Unit testing
- **Dexmaker** 2.28.3 - Android instrumented testing

## Theming

The app uses **Material You** dynamic theming:

```kotlin
CosmicWeatherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true
) {
    // App content
}
```

**Colors:**
- Adapts to device wallpaper colors (Android 12+)
- Purple/Pink cosmic theme on older devices
- Translucent surfaces (70-75% opacity) for weather backgrounds
- White text with shadows for readability over backgrounds

**Typography:**
- Default Material 3 type scale
- Bold headlines for weather and horoscope titles
- Medium weight for labels
- Regular weight for body text

## Future Enhancements

Potential features for future versions:

- 🌐 **Online Weather API** - Real weather data instead of random scenarios
- 💾 **Save Favorite Pairs** - Bookmark frequently checked sign combinations
- 📊 **Compatibility Meter** - Visual compatibility percentage
- 🎨 **Theme Customization** - Custom color schemes
- 🔔 **Daily Horoscope Notifications** - Push notifications
- 📱 **Widget Support** - Home screen widget with daily horoscope
- 🌍 **Multi-language Support** - Localization
- 📈 **Analytics** - Track most popular sign combinations

## License

This project was created as a demonstration application.

## Credits

- Weather background images: Custom designs
- Zodiac icons: Vector SVG icons
- Horoscope templates: Algorithmic generation based on astrological elements
