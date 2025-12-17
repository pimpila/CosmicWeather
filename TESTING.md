# Cosmic Weather Testing Guide

## Test Suite Overview

This project includes comprehensive testing coverage:

### Unit Tests (22 tests)
**Location:** `app/src/test/java/`
- Tests ViewModel logic, state management, and business rules
- Runs on the JVM (fast execution)
- Uses `mockito-core` for mocking

**Run command:**
```bash
./gradlew test
```

### Instrumented UI Tests (18 tests)
**Location:** `app/src/androidTest/java/`
- Tests Jetpack Compose UI components
- Tests user interactions and UI state changes
- Runs on Android device or emulator
- Uses `dexmaker-mockito-inline` for mocking Kotlin final classes

**Run command:**
```bash
# Requires connected device or running emulator
./gradlew connectedDebugAndroidTest
```

## Test Configuration

### Permission Handling

The app requires location permissions to display the user's city. In tests, we automatically grant these permissions using `GrantPermissionRule`:

```kotlin
@get:Rule
val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION
)
```

This means:
- ✅ Tests don't require manual permission setup
- ✅ Tests run consistently without user interaction
- ✅ Tests work in CI/CD environments
- ✅ No need to enable location on the test device

### Mocking Configuration

#### Why Special Configuration?

Kotlin classes are **final by default**, but Mockito requires classes to be non-final to mock them. There are different solutions for unit tests vs instrumented tests:

### Unit Tests (JVM)
- Uses `mockito-core` which works fine on the JVM
- Can mock final classes when configured properly

### Instrumented Tests (Android)
- Requires `dexmaker-mockito-inline` (NOT `mockito-android`)
- This library is specifically designed to mock final classes on Android devices/emulators
- Automatically handles bytecode manipulation for Android's Dalvik/ART runtime

## Dependencies

```kotlin
// Unit tests
testImplementation("org.mockito:mockito-core:5.8.0")
testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")

// Instrumented tests
androidTestImplementation("androidx.test:rules:1.6.1")  // For GrantPermissionRule
androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
androidTestImplementation("com.linkedin.dexmaker:dexmaker-mockito-inline:2.28.3")
```

## Running Tests

### All Tests
```bash
./gradlew test                      # Unit tests only
./gradlew connectedDebugAndroidTest # Instrumented tests (requires device)
./gradlew build                      # Runs all tests
```

### Specific Test Class
```bash
# Unit test
./gradlew test --tests "CosmicWeatherViewModelTest"

# Instrumented test (requires device)
./gradlew connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=\
com.example.cosmicweather.presentation.screen.CosmicWeatherScreenTest
```

### Single Test Method
```bash
# Unit test
./gradlew test --tests "CosmicWeatherViewModelTest.initial state should have null signs and null horoscope"

# Instrumented test (requires device)
./gradlew connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=\
com.example.cosmicweather.presentation.screen.CosmicWeatherScreenTest\
#topBarDisplaysTitle
```

### From Android Studio
1. Right-click on test file or test method
2. Select "Run [test name]"
3. For instrumented tests, ensure device/emulator is connected

## Test Coverage

### CosmicWeatherViewModelTest (Unit Tests)
- ✅ Initial state verification
- ✅ Sign selection logic
- ✅ Horoscope generation
- ✅ Weather loading
- ✅ Error handling
- ✅ State transitions
- ✅ Edge cases (null values, same signs, etc.)

### CosmicWeatherScreenTest (Instrumented Tests)
- ✅ UI element display
- ✅ Dropdown interactions
- ✅ Sign selection flow
- ✅ Horoscope display
- ✅ Dynamic updates
- ✅ Error message display
- ✅ All 12 zodiac signs availability

## Troubleshooting

### "Cannot mock/spy class" Error
**Problem:** Mockito can't mock Kotlin final classes.
**Solution:** Ensure `dexmaker-mockito-inline` is included in androidTest dependencies.

### "No connected devices" Error
**Problem:** Running instrumented tests without a device.
**Solution:**
- Connect a physical Android device with USB debugging enabled
- Or start an Android emulator from Android Studio

### Tests Timing Out
**Problem:** Async operations in tests not completing.
**Solution:** Tests use `advanceUntilIdle()` and `waitUntil()` to handle async operations properly.

### Flaky Tests
**Problem:** Tests occasionally fail due to timing.
**Solution:** Instrumented tests use `waitUntil()` with proper timeout values (5000ms).

## CI/CD Integration

For continuous integration, use:

```bash
# Unit tests (no device needed)
./gradlew test

# For instrumented tests, you'll need:
# 1. Firebase Test Lab, OR
# 2. Emulator running in CI environment, OR
# 3. Connected device
```

## Test Maintenance

When adding new features:
1. **Write unit tests** for ViewModel logic first
2. **Write instrumented tests** for UI interactions
3. **Mock dependencies** using `mock()` from mockito-kotlin
4. **Use `runTest` block** for coroutine tests
5. **Use `composeTestRule`** for Compose UI tests

## Example Test Patterns

### Unit Test Pattern
```kotlin
@Test
fun `feature should behave correctly`() = runTest {
    // Given
    val repository: Repository = mock()
    whenever(repository.getData()).thenReturn(expectedData)
    val viewModel = MyViewModel(repository)

    // When
    viewModel.performAction()
    advanceUntilIdle()

    // Then
    assertEquals(expectedValue, viewModel.state.value)
}
```

### Instrumented Test Pattern
```kotlin
@Test
fun userCanInteractWithUI() {
    // Given
    composeTestRule.setContent {
        MyScreen(viewModel = viewModel)
    }

    // When
    composeTestRule.onNodeWithText("Button").performClick()

    // Wait for async operation
    composeTestRule.waitUntil(timeoutMillis = 5000) {
        runBlocking { viewModel.state.first().isComplete }
    }

    // Then
    composeTestRule.onNodeWithText("Success").assertIsDisplayed()
}
```
