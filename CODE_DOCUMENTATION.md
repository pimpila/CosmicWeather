# Cosmic Weather - Code Documentation

Detailed technical documentation for developers working on the Cosmic Weather app.

## Table of Contents
1. [Architecture Deep Dive](#architecture-deep-dive)
2. [Domain Layer Details](#domain-layer-details)
3. [Data Layer Details](#data-layer-details)
4. [Presentation Layer Details](#presentation-layer-details)
5. [Horoscope Generation Algorithm](#horoscope-generation-algorithm)
6. [State Management](#state-management)
7. [Database Schema](#database-schema)
8. [Compose UI Patterns](#compose-ui-patterns)
9. [Code Conventions](#code-conventions)
10. [Common Patterns](#common-patterns)

---

## Architecture Deep Dive

### Multi-Module Architecture

The app is split into three Gradle modules for clear separation:

```
:app (depends on :domain, :data)
  └─> :domain (pure Kotlin, no Android dependencies)
  └─> :data (depends on :domain)
```

**Benefits:**
- **Faster builds** - Only changed modules recompile
- **Clear dependencies** - Domain layer has no Android dependencies
- **Better testing** - Domain logic tested without Android framework
- **Reusability** - Domain models can be shared across platforms

### Dependency Flow

```
┌──────────────────────────────────────────┐
│  Presentation (app module)               │
│  - Depends on: domain, data              │
│  - Android framework dependencies        │
│  - UI components, ViewModels             │
└────────────┬─────────────────────────────┘
             │
             ├─────────────────────────────┐
             │                             │
             ▼                             ▼
┌────────────────────────┐   ┌────────────────────────┐
│  Domain (domain module)│   │  Data (data module)    │
│  - Pure Kotlin         │◄──┤  - Depends on: domain │
│  - Business logic      │   │  - Room database       │
│  - Models, generators  │   │  - Repositories        │
└────────────────────────┘   └────────────────────────┘
```

**Dependency Rules:**
- ✅ App can depend on Domain and Data
- ✅ Data can depend on Domain
- ❌ Domain cannot depend on anything (pure Kotlin)
- ❌ Data cannot depend on App
- ❌ Domain cannot depend on Data

---

## Domain Layer Details

### ZodiacSign.kt

**Location:** `domain/src/main/kotlin/com/example/cosmicweather/domain/model/ZodiacSign.kt`

```kotlin
enum class ZodiacSign(
    val displayName: String,
    val dateRange: String,
    val element: String,
    val symbol: String
) {
    ARIES("Aries", "Mar 21 - Apr 19", "Fire", "♈️"),
    TAURUS("Taurus", "Apr 20 - May 20", "Earth", "♉️"),
    // ... all 12 signs

    companion object {
        fun fromDisplayName(name: String): ZodiacSign? {
            return values().find {
                it.displayName.equals(name, ignoreCase = true)
            }
        }
    }
}
```

**Design Decisions:**
- Enum provides type safety (can't have invalid signs)
- Each sign knows its element (used in compatibility calculations)
- Companion object helper for parsing user input
- Unicode symbols for visual representation

**Element Distribution:**
- Fire: Aries, Leo, Sagittarius (3 signs)
- Earth: Taurus, Virgo, Capricorn (3 signs)
- Air: Gemini, Libra, Aquarius (3 signs)
- Water: Cancer, Scorpio, Pisces (3 signs)

### Weather.kt

**Location:** `domain/src/main/kotlin/com/example/cosmicweather/domain/model/Weather.kt`

```kotlin
data class Weather(
    val id: Int,
    val condition: String,
    val temperature: Int,
    val emoji: String,
    val mood: String
)
```

**Key Properties:**
- `id` - Database primary key
- `condition` - Human-readable name ("Sunny", "Rainy")
- `temperature` - Celsius (converted to Fahrenheit in UI)
- `emoji` - Visual representation (not used in current UI)
- `mood` - Maps to weather influence templates

**Mood Mapping:**
```kotlin
"Sunny" → "energetic"
"Rainy" → "cozy"
"Cloudy" → "contemplative"
"Snowy" → "romantic"
"Foggy" → "mysterious"
"Stormy" → "intense"
"Clear Night" → "romantic"
"Windy" → "restless"
```

### Horoscope.kt

**Location:** `domain/src/main/kotlin/com/example/cosmicweather/domain/model/Horoscope.kt`

```kotlin
data class Horoscope(
    val sign1: ZodiacSign,
    val sign2: ZodiacSign,
    val weather: Weather,
    val relationshipClimate: String,
    val communication: String,
    val suggestedActivity: String
)
```

**Immutability:**
- All properties are `val` (immutable)
- Data class provides automatic `copy()` for creating modified versions
- Thread-safe by default

### HoroscopeTemplates.kt

**Location:** `domain/src/main/kotlin/com/example/cosmicweather/domain/template/HoroscopeTemplates.kt`

Contains zodiac compatibility data and weather influences.

**Key Data Structures:**

```kotlin
data class SignArchetype(
    val energyStyle: String,
    val relationalTrait: String,
    val communicationStyle: String
)

data class ZodiacCompatibility(
    val sign1: ZodiacSign,
    val sign2: ZodiacSign,
    val baseClimate: String,
    val baseCompatibility: String
)

data class WeatherInfluence(
    val mood: String,
    val energyDescription: String,
    val activityType: String
)
```

**Template Objects:**

1. **signArchetypes** - Map<ZodiacSign, SignArchetype>
   - Defines personality traits for each sign
   - Used to generate compatibility descriptions

2. **elementCompatibility** - Map<Pair<String, String>, Pair<String, String>>
   - Defines how elements interact
   - Returns (climate, description)
   - Examples:
     - Fire + Air → "Exciting and expansive"
     - Earth + Water → "Nurturing and fertile"
     - Fire + Water → "Intense and transformative"

3. **weatherInfluences** - Map<String, WeatherInfluence>
   - Maps mood to energy and activity type
   - Examples:
     - "energetic" → "outgoing and social", "outdoor adventures"
     - "cozy" → "introspective and intimate", "indoor bonding activities"

**Algorithmic Generation:**
The app doesn't store 144×8 = 1,152 pre-written horoscopes. Instead:
1. Get sign archetypes for both signs
2. Get element compatibility pattern
3. Combine with weather influence
4. Generate dynamic text based on rules

---

## Data Layer Details

### WeatherConditionEntity.kt

**Location:** `data/src/main/java/com/example/cosmicweather/data/local/entity/WeatherConditionEntity.kt`

```kotlin
@Entity(tableName = "weather_conditions")
data class WeatherConditionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "condition")
    val condition: String,

    @ColumnInfo(name = "temperature")
    val temperature: Int,

    @ColumnInfo(name = "emoji")
    val emoji: String,

    @ColumnInfo(name = "mood")
    val mood: String
)

// Mapper function
fun WeatherConditionEntity.toWeather(): Weather = Weather(
    id = id,
    condition = condition,
    temperature = temperature,
    emoji = emoji,
    mood = mood
)
```

**Design Decisions:**
- Separate entity from domain model (allows schema changes without affecting domain)
- Explicit column names for database clarity
- Auto-generate ID for easy insertion
- Extension function for clean mapping

### WeatherDao.kt

**Location:** `data/src/main/java/com/example/cosmicweather/data/local/dao/WeatherDao.kt`

```kotlin
@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_conditions ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWeather(): WeatherConditionEntity?

    @Query("SELECT * FROM weather_conditions")
    fun getAllWeather(): Flow<List<WeatherConditionEntity>>

    @Query("SELECT * FROM weather_conditions WHERE id = :id")
    suspend fun getWeatherById(id: Int): WeatherConditionEntity?

    @Query("SELECT * FROM weather_conditions")
    suspend fun getAllWeatherList(): List<WeatherConditionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg weather: WeatherConditionEntity)
}
```

**Key Methods:**
- `getRandomWeather()` - Uses SQL RANDOM() for random selection
- `getAllWeather()` - Returns Flow for reactive updates
- `getAllWeatherList()` - Synchronous list for initialization check
- `insertAll()` - Vararg for bulk insertion

**Why RANDOM() in SQL?**
- More efficient than loading all and picking random in Kotlin
- Database handles randomization
- Single query instead of two (count + get by index)

### WeatherDatabase.kt

**Location:** `data/src/main/java/com/example/cosmicweather/data/local/database/WeatherDatabase.kt`

```kotlin
@Database(
    entities = [WeatherConditionEntity::class],
    version = 2,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "cosmic_weather_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun ensureDatabasePopulated(database: WeatherDatabase) {
            val weatherDao = database.weatherDao()
            val allWeather = weatherDao.getAllWeatherList()
            if (allWeather.isEmpty()) {
                populateDatabase(weatherDao)
            }
        }

        private suspend fun populateDatabase(weatherDao: WeatherDao) {
            val weatherScenarios = listOf(
                WeatherConditionEntity(
                    condition = "Sunny",
                    temperature = 25,
                    emoji = "☀️",
                    mood = "energetic"
                ),
                // ... 7 more scenarios
            )
            weatherDao.insertAll(*weatherScenarios.toTypedArray())
        }
    }
}
```

**Database Patterns:**
- **Singleton pattern** - INSTANCE with double-checked locking
- **Lazy initialization** - Database created only when first accessed
- **Pre-population** - Ensures data exists on first run
- **Destructive migration** - Simplifies development (recreates DB on schema changes)

**Version History:**
- Version 1: Initial schema with 12 weather types
- Version 2: Removed 4 weather types (Partly Cloudy, Hot, Crisp, Humid)

### WeatherRepository.kt

**Location:** `data/src/main/java/com/example/cosmicweather/data/repository/WeatherRepository.kt`

```kotlin
class WeatherRepository(
    private val weatherDao: WeatherDao,
    private val database: WeatherDatabase
) {
    private var isInitialized = false

    private suspend fun ensureInitialized() {
        if (!isInitialized) {
            WeatherDatabase.ensureDatabasePopulated(database)
            isInitialized = true
        }
    }

    suspend fun getRandomWeather(): Weather? {
        ensureInitialized()
        return weatherDao.getRandomWeather()?.toWeather()
    }

    fun getAllWeatherScenarios(): Flow<List<Weather>> {
        return weatherDao.getAllWeather()
            .map { entities -> entities.map { it.toWeather() } }
    }

    suspend fun getWeatherById(id: Int): Weather? {
        return weatherDao.getWeatherById(id)?.toWeather()
    }
}
```

**Repository Pattern Benefits:**
- Abstracts data source (could switch from Room to API)
- Handles initialization logic
- Converts entities to domain models
- Single source of truth for weather data

**Initialization Pattern:**
```kotlin
private var isInitialized = false

private suspend fun ensureInitialized() {
    if (!isInitialized) {
        // Expensive operation (database population)
        isInitialized = true
    }
}
```
- Ensures database populated before first access
- Runs only once per app session
- Suspends until complete (proper async/await)

---

## Presentation Layer Details

### CosmicWeatherViewModel.kt

**Location:** `app/src/main/java/com/example/cosmicweather/presentation/viewmodel/CosmicWeatherViewModel.kt`

```kotlin
class CosmicWeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val horoscopeGenerator: HoroscopeGenerator
) : ViewModel() {

    // State
    private val _userSign = MutableStateFlow<ZodiacSign?>(null)
    val userSign: StateFlow<ZodiacSign?> = _userSign.asStateFlow()

    private val _partnerSign = MutableStateFlow<ZodiacSign?>(null)
    val partnerSign: StateFlow<ZodiacSign?> = _partnerSign.asStateFlow()

    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather.asStateFlow()

    private val _horoscope = MutableStateFlow<Horoscope?>(null)
    val horoscope: StateFlow<Horoscope?> = _horoscope.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadInitialWeather()
    }
}
```

**State Management Pattern:**
- **MutableStateFlow** - Private, only ViewModel can modify
- **StateFlow** - Public, read-only for UI
- **asStateFlow()** - Creates read-only view
- **Nullable states** - Represents absence of data (not loaded yet)

**ViewModel Lifecycle:**
```
1. ViewModel created
2. init block runs → loadInitialWeather()
3. Weather loaded → _weather.value = weather
4. UI collects weather.collectAsState()
5. User selects signs → horoscope generated
6. ViewModel survives configuration changes (rotation)
7. Activity destroyed → ViewModel cleared
```

**Key Methods:**

```kotlin
fun selectUserSign(sign: ZodiacSign) {
    _userSign.value = sign
    if (_partnerSign.value != null) {
        generateHoroscope()
    }
}

private fun generateHoroscope() {
    val userSignValue = _userSign.value
    val partnerSignValue = _partnerSign.value

    if (userSignValue == null || partnerSignValue == null) {
        return
    }

    viewModelScope.launch {
        _isLoading.value = true
        _error.value = null

        try {
            val currentWeather = _weather.value
                ?: weatherRepository.getRandomWeather()

            if (currentWeather == null) {
                _error.value = "Unable to load weather data"
                return@launch
            }

            if (_weather.value == null) {
                _weather.value = currentWeather
            }

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
```

**Error Handling:**
- Try-catch around async operations
- Clear error before new operation
- Set loading state in finally block (always runs)
- User-friendly error messages

### CosmicWeatherScreen.kt

**Location:** `app/src/main/java/com/example/cosmicweather/presentation/screen/CosmicWeatherScreen.kt`

**Main Composable:**

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosmicWeatherScreen(
    viewModel: CosmicWeatherViewModel,
    modifier: Modifier = Modifier
) {
    // Collect state as Compose State
    val userSign by viewModel.userSign.collectAsState()
    val partnerSign by viewModel.partnerSign.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val horoscope by viewModel.horoscope.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Location handling
    val context = LocalContext.current
    var locationText by remember { mutableStateOf("Your Location") }

    // Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            getLocation(context) { location ->
                locationText = location
            }
        }
    }

    // Request permissions on first composition
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    Scaffold(
        topBar = { /* TopAppBar */ },
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Weather background
            weather?.let { /* Background image or gradient */ }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = if (horoscope == null) {
                    Arrangement.Center  // Center when no horoscope
                } else {
                    Arrangement.spacedBy(16.dp)  // Top-aligned when horoscope shown
                }
            ) {
                // Weather display, sign selectors, horoscope
            }
        }
    }
}
```

**Compose Patterns Used:**
- `collectAsState()` - Converts Flow to Compose State
- `remember` - Preserves state across recompositions
- `LaunchedEffect(Unit)` - Runs once on first composition
- `Scaffold` - Material 3 layout structure
- Conditional layout (`if (horoscope == null)`)

**Location Handling:**

```kotlin
@SuppressLint("MissingPermission")
private fun getLocation(
    context: android.content.Context,
    onLocationReceived: (String) -> Unit
) {
    val fusedLocationClient = LocationServices
        .getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )

                    if (addresses?.isNotEmpty() == true) {
                        val address = addresses[0]
                        val cityName = address.locality
                            ?: address.subAdminArea
                            ?: address.adminArea
                            ?: "Unknown Location"
                        onLocationReceived(cityName)
                    } else {
                        onLocationReceived("Unknown Location")
                    }
                } catch (e: Exception) {
                    onLocationReceived("Your Location")
                }
            } else {
                onLocationReceived("Your Location")
            }
        }
        .addOnFailureListener {
            onLocationReceived("Your Location")
        }
}
```

**Error Handling:**
- Fallback to "Your Location" on any error
- Try locality → subAdminArea → adminArea priority
- Graceful degradation (app works without location)

---

## Horoscope Generation Algorithm

### High-Level Flow

```
Input: (sign1, sign2, weather)
  ↓
1. Get zodiac compatibility from templates
   - Element compatibility (Fire + Water, etc.)
   - Sign archetypes (personality traits)
   ↓
2. Get weather influence
   - Energy description (outgoing, introspective, etc.)
   - Activity type (outdoor, indoor, conversation, etc.)
   ↓
3. Generate relationship climate
   - Base compatibility + weather energy
   ↓
4. Generate communication advice
   - Element compatibility style
   - Modified by weather energy
   ↓
5. Generate activity suggestion
   - Sign energy style (high-energy, grounded, balanced)
   - Activity type from weather
   - Specific weather condition
   ↓
Output: Horoscope
```

### Detailed Algorithm

**Step 1: Get Compatibility**

```kotlin
fun getCompatibility(sign1: ZodiacSign, sign2: ZodiacSign): ZodiacCompatibility? {
    val archetype1 = signArchetypes[sign1] ?: return null
    val archetype2 = signArchetypes[sign2] ?: return null

    val elementPattern = elementCompatibility[
        Pair(sign1.element, sign2.element)
    ] ?: return null

    val baseClimate = elementPattern.first

    val baseCompatibility = when {
        sign1 == sign2 -> {
            // Same sign - mirror dynamic
            "Mirror souls with ${archetype1.energyStyle} energy..."
        }
        sign1.element == sign2.element -> {
            // Same element, different signs
            "${archetype1.energyStyle} meets ${archetype2.energyStyle}..."
        }
        else -> {
            // Different elements
            "${archetype1.relationalTrait} encounters ${archetype2.relationalTrait}..."
        }
    }

    return ZodiacCompatibility(sign1, sign2, baseClimate, baseCompatibility)
}
```

**Step 2: Generate Communication Advice**

```kotlin
private fun generateCommunicationAdvice(
    sign1: ZodiacSign,
    sign2: ZodiacSign,
    baseCompatibility: String,
    energyDescription: String?
): String {
    // Determine compatibility style from elements
    val compatibilityStyle = when {
        sign1.element == sign2.element ->
            "naturally aligned"
        (sign1.element == "Fire" && sign2.element == "Air") ||
        (sign1.element == "Air" && sign2.element == "Fire") ->
            "energizing and stimulating"
        (sign1.element == "Earth" && sign2.element == "Water") ||
        (sign1.element == "Water" && sign2.element == "Earth") ->
            "supportive and grounding"
        (sign1.element == "Fire" && sign2.element == "Water") ||
        (sign1.element == "Water" && sign2.element == "Fire") ->
            "requiring patience and care"
        else ->
            "bridging different perspectives"
    }

    // Modify by weather energy
    return if (energyDescription != null) {
        when {
            "introspective" in energyDescription ->
                "Thoughtful and $compatibilityStyle - take time to reflect"
            "outgoing" in energyDescription ->
                "Open and $compatibilityStyle - perfect time for big conversations"
            // ... more conditions
        }
    } else {
        "Communication is $compatibilityStyle - speak from the heart"
    }
}
```

**Step 3: Generate Activity Suggestion**

```kotlin
private fun generateActivitySuggestion(
    sign1: ZodiacSign,
    sign2: ZodiacSign,
    baseCompatibility: String,
    activityType: String,
    weatherCondition: String
): String {
    // Determine energy style from sign elements
    val energyStyle = when {
        sign1.element in listOf("Fire", "Air") &&
        sign2.element in listOf("Fire", "Air") ->
            "high-energy"
        sign1.element in listOf("Earth", "Water") &&
        sign2.element in listOf("Earth", "Water") ->
            "grounded"
        else ->
            "balanced"
    }

    // Generate suggestion based on:
    // 1. Activity type (outdoor, indoor, conversation, romantic, etc.)
    // 2. Weather condition (sunny, rainy, snowy, etc.)
    // 3. Energy style (high-energy, grounded, balanced)

    return when {
        "outdoor" in activityType -> when (weatherCondition.lowercase()) {
            "sunny" -> if (energyStyle == "high-energy") {
                "Go for an adventurous hike or try a new outdoor sport"
            } else if (energyStyle == "grounded") {
                "Take a peaceful nature walk or have a picnic in the park"
            } else {
                "Enjoy a leisurely outdoor activity that combines movement and rest"
            }
            // ... more weather conditions
        }
        "indoor" in activityType -> when (weatherCondition.lowercase()) {
            "rainy", "stormy" -> if (energyStyle == "grounded") {
                "Cook a comforting meal together and share stories"
            } else if (energyStyle == "high-energy") {
                "Try a new recipe or play board games to stay energized"
            } else {
                "Create a cozy indoor space that feels both safe and stimulating"
            }
            // ... more weather conditions
        }
        // ... more activity types
    }
}
```

### Example Calculation

**Input:**
- Sign 1: Scorpio (Water)
- Sign 2: Virgo (Earth)
- Weather: Sunny (energetic mood)

**Processing:**

1. **Compatibility:**
   - Elements: Water + Earth
   - Element pattern: "Nurturing and fertile"
   - Base compatibility: "Passionate and secretive encounters helpful and critical..."

2. **Weather Influence:**
   - Mood: "energetic"
   - Energy description: "outgoing and social"
   - Activity type: "outdoor adventures"

3. **Relationship Climate:**
   - "Nurturing and fertile with outgoing and social energy"

4. **Communication:**
   - Compatibility style: "supportive and grounding" (Earth + Water)
   - With energetic weather: "Open and supportive and grounding - perfect time for big conversations"

5. **Activity:**
   - Energy style: "grounded" (Earth + Water)
   - Activity type: "outdoor adventures"
   - Weather: "sunny"
   - Result: "Take a peaceful nature walk or have a picnic in the park"

---

## State Management

### StateFlow Pattern

**Why StateFlow over LiveData?**
- ✅ Better Kotlin coroutines integration
- ✅ Works in non-Android modules (domain layer)
- ✅ More functional/reactive approach
- ✅ No lifecycle dependency
- ✅ Better Compose integration with `collectAsState()`

**StateFlow vs MutableStateFlow:**

```kotlin
// Private - only ViewModel can change
private val _state = MutableStateFlow<T>(initialValue)

// Public - UI can only observe
val state: StateFlow<T> = _state.asStateFlow()
```

**Pattern Benefits:**
- Encapsulation - UI can't modify state
- Single source of truth
- Type-safe
- Thread-safe (atomic operations)

### Compose State Integration

```kotlin
// In ViewModel
private val _weather = MutableStateFlow<Weather?>(null)
val weather: StateFlow<Weather?> = _weather.asStateFlow()

// In Composable
val weather by viewModel.weather.collectAsState()

// Recomposition triggers automatically when weather changes
if (weather != null) {
    Text("Weather: ${weather.condition}")
}
```

**Recomposition:**
- Compose subscribes to StateFlow
- When StateFlow emits new value
- Compose recomposes only affected parts
- Efficient UI updates (minimal recomposition)

---

## Database Schema

### Tables

**weather_conditions**
```sql
CREATE TABLE weather_conditions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    condition TEXT NOT NULL,
    temperature INTEGER NOT NULL,
    emoji TEXT NOT NULL,
    mood TEXT NOT NULL
)
```

**Sample Data:**
```sql
INSERT INTO weather_conditions VALUES
    (1, 'Sunny', 25, '☀️', 'energetic'),
    (2, 'Rainy', 15, '🌧️', 'cozy'),
    (3, 'Cloudy', 18, '☁️', 'contemplative'),
    (4, 'Snowy', 0, '❄️', 'romantic'),
    (5, 'Foggy', 12, '🌫️', 'mysterious'),
    (6, 'Stormy', 14, '⛈️', 'intense'),
    (7, 'Clear Night', 10, '🌙', 'romantic'),
    (8, 'Windy', 20, '💨', 'restless');
```

### Queries

**Get Random Weather:**
```sql
SELECT * FROM weather_conditions ORDER BY RANDOM() LIMIT 1
```

**Get All Weather:**
```sql
SELECT * FROM weather_conditions
```

**Get Weather by ID:**
```sql
SELECT * FROM weather_conditions WHERE id = ?
```

---

## Compose UI Patterns

### Stateless vs Stateful Composables

**Stateless (recommended):**
```kotlin
@Composable
fun HoroscopeCard(
    horoscope: Horoscope,
    modifier: Modifier = Modifier
) {
    // No state, just displays data
    Card(modifier = modifier) {
        Text(horoscope.relationshipClimate)
        // ...
    }
}
```

**Stateful:**
```kotlin
@Composable
fun SignSelectorsCard(
    userSign: ZodiacSign?,
    partnerSign: ZodiacSign?,
    onUserSignSelected: (ZodiacSign) -> Unit,
    onPartnerSignSelected: (ZodiacSign) -> Unit
) {
    // Has internal state (dropdown expanded)
    var expanded by remember { mutableStateOf(false) }

    // But state is hoisted for important data
    ZodiacSignDropdown(
        selectedSign = userSign,
        onSignSelected = onUserSignSelected
    )
}
```

**State Hoisting Benefits:**
- Composable is reusable
- Easy to test
- Single source of truth
- Predictable behavior

### Animation Patterns

**AnimatedContent for Content Changes:**
```kotlin
@Composable
fun AnimatedHoroscopeCard(
    horoscope: Horoscope?,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = horoscope,
        transitionSpec = {
            fadeIn(animationSpec = tween(600)) togetherWith
                    fadeOut(animationSpec = tween(400))
        },
        label = "horoscope_animation"
    ) { currentHoroscope ->
        if (currentHoroscope != null) {
            HoroscopeCard(horoscope = currentHoroscope)
        }
    }
}
```

**Why AnimatedContent instead of AnimatedVisibility?**
- AnimatedVisibility: Animates visibility changes (show/hide)
- AnimatedContent: Animates between different content states
- We need the latter because horoscope changes (Aries+Leo → Taurus+Leo)

---

## Code Conventions

### Naming Conventions

**Classes:**
- PascalCase
- Descriptive names
- Suffixes: `Screen`, `Card`, `ViewModel`, `Repository`

**Functions:**
- camelCase
- Verb-noun pattern: `generateHoroscope()`, `loadWeather()`
- Composables: PascalCase (like components)

**Variables:**
- camelCase
- Descriptive names
- StateFlow: `_privateName`, `publicName`

**Constants:**
- ALL_CAPS_SNAKE_CASE
- In companion object or top-level

### File Organization

**Single class per file:**
- File name matches class name
- Exception: Small related classes (data classes in same file)

**Package structure:**
```
presentation/
├── screen/      # UI screens
├── viewmodel/   # ViewModels
└── component/   # Reusable components (if needed)

domain/
├── model/       # Domain models
├── generator/   # Business logic
└── template/    # Data templates

data/
├── repository/  # Repositories
├── local/       # Local data sources
│   ├── entity/
│   ├── dao/
│   └── database/
└── remote/      # Remote data sources (future)
```

### Documentation

**KDoc for public APIs:**
```kotlin
/**
 * Generates horoscopes by combining zodiac compatibility with weather conditions.
 */
class HoroscopeGenerator {
    /**
     * Generate a horoscope for a zodiac pair and weather condition.
     *
     * @param sign1 First zodiac sign
     * @param sign2 Second zodiac sign (partner)
     * @param weather Current weather conditions
     * @return Generated horoscope
     */
    fun generate(sign1: ZodiacSign, sign2: ZodiacSign, weather: Weather): Horoscope
}
```

**Comments for complex logic:**
```kotlin
// Get element compatibility style
val compatibilityStyle = when {
    sign1.element == sign2.element -> "naturally aligned"
    // ...
}
```

---

## Common Patterns

### Repository Pattern

```kotlin
class SomeRepository(private val dao: SomeDao) {
    suspend fun getData(): DomainModel? {
        return dao.getEntity()?.toDomainModel()
    }

    fun observeData(): Flow<List<DomainModel>> {
        return dao.observeEntities()
            .map { entities -> entities.map { it.toDomainModel() } }
    }
}
```

### ViewModel Pattern

```kotlin
class SomeViewModel(
    private val repository: SomeRepository
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Initial)
    val state: StateFlow<State> = _state.asStateFlow()

    fun performAction() {
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                val result = repository.getData()
                _state.value = State.Success(result)
            } catch (e: Exception) {
                _state.value = State.Error(e.message)
            }
        }
    }
}
```

### Composable Pattern

```kotlin
@Composable
fun MyScreen(
    viewModel: MyViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    when (state) {
        is State.Loading -> LoadingIndicator()
        is State.Success -> SuccessContent(state.data)
        is State.Error -> ErrorMessage(state.message)
    }
}
```

---

## Best Practices

1. **Immutability** - Prefer `val` over `var`
2. **Null Safety** - Use nullable types explicitly
3. **Coroutines** - Use suspend functions for async work
4. **State Hoisting** - Keep composables stateless when possible
5. **Single Responsibility** - Each class/function does one thing
6. **Dependency Injection** - Pass dependencies through constructor
7. **Error Handling** - Always handle errors gracefully
8. **Testing** - Write tests for business logic
9. **Documentation** - Document public APIs
10. **Code Review** - Review changes before merging
