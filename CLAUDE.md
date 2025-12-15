# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build the project
./gradlew build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew testDebugUnitTest --tests "com.example.cosmicweather.ExampleUnitTest"

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean
```

## Architecture

This is an Android application built with:
- **Kotlin** as the primary language
- **Jetpack Compose** for UI with Material 3 design system
- **Gradle Kotlin DSL** for build configuration with version catalogs (`gradle/libs.versions.toml`)

### Project Structure

- `app/` - Main application module
  - `src/main/java/com/example/cosmicweather/` - Application source code
    - `MainActivity.kt` - Entry point activity using Compose
    - `ui/theme/` - Material 3 theme configuration (colors, typography, theme)
  - `src/test/` - Unit tests
  - `src/androidTest/` - Instrumented tests

### Key Configuration

- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Java/Kotlin target**: JVM 11
- **Compose**: Enabled with Kotlin Compose plugin
- **Dynamic colors**: Supported on Android 12+ via Material You
