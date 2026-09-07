# Implementation Plan - Modernization & Bug Fixes for SAU SOLUTIONS

This plan covers essential updates to modernize the app architecture, improve performance through local caching, and fix reported runtime crashes.

## User Review Required

> [!IMPORTANT]
> **Hilt Setup**: Adding Hilt requires modifications to the `Application` class and `build.gradle` files. I will need to create a new `SauApplication` class.
> **Database Schema**: The Room database will mirror key Supabase tables (`users`, `categories`, `products`). Any future changes to the remote schema should be reflected here.
> **Supabase UUIDs**: The "invalid input syntax for type uuid" error suggests a mismatch between Auth IDs and PostgREST table expectations. I'll implement a safety check.

## Open Questions

- Should I prioritize any specific feature for the offline mode? (e.g., just categories/products, or also user bookings?)

## Proposed Changes

### Core Architecture (Hilt & DI)

#### [NEW] [SauApplication.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/SauApplication.kt)
Create the Hilt Application class.

#### [NEW] [AppModule.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/di/AppModule.kt)
Define Hilt modules for Supabase clients and Repositories.

#### [MODIFY] [build.gradle.kts](file:///E:/ANDROID-PROJECTS/app/build.gradle.kts)
Add Hilt and Room dependencies.

---

### Local Persistence (Room)

#### [NEW] [SauDatabase.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/data/local/SauDatabase.kt)
Define the Room database and its entities.

#### [NEW] [UserDao.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/data/local/dao/UserDao.kt)
DAO for user profile caching.

---

### Navigation & UI Fixes

#### [MODIFY] [NavHost.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/navigation/NavHost.kt)
- Register the missing `email_login` route.
- Modularize the `NavHost` by extracting sub-graphs for better maintainability.

#### [MODIFY] [MainActivity.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/MainActivity.kt)
- Annotate with `@AndroidEntryPoint`.
- Add `enableEdgeToEdge()` for modern immersive UI.

---

### Supabase & Auth Improvements

#### [MODIFY] [SupabaseRepository.kt](file:///E:/ANDROID-PROJECTS/app/src/main/java/com/nisr/sauservices/data/repository/SupabaseRepository.kt)
Add UUID validation to prevent "anonymous" or malformed strings from crashing Supabase queries.

## Verification Plan

### Automated Tests
- Run `gradlew test` to ensure DI and Repository logic are working.
- Verify Room migrations (if any).

### Manual Verification
- Deploy to the device and verify that navigating to Email Login no longer crashes.
- Verify that the app works (viewing cached categories) even with no internet connection.
- Check the UI for proper edge-to-edge rendering in both Light and Dark modes.
