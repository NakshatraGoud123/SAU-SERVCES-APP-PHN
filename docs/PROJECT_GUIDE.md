# SAU Services App: Project Guide and Development Ideas

**Reviewed:** 3 October 2026  
**Purpose:** Explain what the Android project contains, how its main parts fit together, and what to explore next.

> This guide is based on the source files and configuration present in this checkout. A screen, repository, or backend function in the code is evidence that the capability has been started; it does not by itself prove that the complete user journey works against the live backend. The repository history could not be inspected in this environment, so “what we have done” below means what is visible in the current project snapshot.

## 1. What the app is

SAU Services is an Android app intended to bring local services and shopping into one place. The code covers customer browsing and booking, service categories, food and essential-item shopping, customer accounts, payment, and order or provider location tracking. It also contains some partner, worker, merchant, and admin route names, though their complete operational workflows should be verified separately.

The breadth is a useful foundation. The main development challenge is making the many category-specific flows behave consistently: the same identity, pricing, booking/order state, payment rules, and support experience should carry through every module.

## 2. Technology in use

| Area | Technologies found in the project | What they do here |
|---|---|---|
| Android app | Kotlin, Android Gradle Plugin, Java 17, min SDK 24 / target SDK 35 | Builds and runs the native Android application. |
| UI | Jetpack Compose, Material 3, some Android XML layouts/resources | Compose is the main screen approach; XML remains for selected resources and map/adaptor surfaces. |
| Navigation and app state | Navigation Compose, AndroidX ViewModel, Kotlin coroutines/Flow | Connects screens and holds screen-related state and asynchronous work. |
| Dependency injection | Hilt with KSP | Provides shared app dependencies such as Supabase and repositories. |
| Backend and data | Supabase Auth, PostgREST, Realtime, Storage, Edge Functions | Authentication, database access, live updates, media storage, and server-side payment operations. |
| Networking | Ktor/OkHttp and Retrofit/Gson | HTTP clients used for backend and API integrations. Both networking stacks are present. |
| Local persistence/security | Room, AndroidX Security Crypto, `SessionManager` | Local database building blocks and local session/preferences support. Room is configured; confirm which app data is actually persisted through it. |
| Maps/location | Google Maps SDK/Compose, Google Play location APIs | Address selection, maps, and delivery/provider location updates. |
| Payments | Razorpay Android Checkout plus Supabase Edge Functions | Creates payment orders and verifies payment server-side; wallet ledger SQL is also present. |
| Images and sign-in | Coil, Android Credentials/Google Identity | Image loading and credential-based sign-in integrations. |
| Tests | JUnit, coroutine test library, AndroidX JUnit, Espresso | Test dependencies and selected ViewModel/filter test files are present. |

Dependency versions are declared centrally in [`gradle/libs.versions.toml`](../gradle/libs.versions.toml), and app configuration/dependencies are in [`app/build.gradle.kts`](../app/build.gradle.kts).

## 3. What is already represented in the code

These are areas with screens, navigation, data classes, ViewModels, repositories, backend functions, or configuration in the checkout. Completion and live behavior still need end-to-end verification.

### Customer and account experience

- Splash/onboarding and sign-in/sign-up flows, including password reset and phone/OTP screens.
- Profile, edit profile, settings/theme, notifications, addresses, FAQ, contact, and wallet screens.
- Customer home/dashboard and category/service discovery.
- Search, cart, wishlist, bookings, orders, success/confirmation, and tracking-related code.

### Service and shopping categories

The source tree has dedicated UI/data modules for residential services, business services, lifestyle, technology, men's grooming, women's beauty, healthcare, education, food, home essentials, essential supplies, mechanics, and mobility. Several contain category/subcategory browsing plus booking/cart/payment screens. Each category can be followed from the Compose navigation graph into its UI, ViewModel, repository, and model files.

### Backend and infrastructure

- Supabase client setup installs Auth, PostgREST, Realtime, Storage, and Functions.
- Supabase Edge Functions create Razorpay orders and verify payment signatures.
- A wallet migration defines balances, a transaction ledger, row-level security policies, and an RPC for wallet transactions.
- Google Maps and fused location APIs are configured; location service code sends live coordinates to Supabase, and tracking code listens for location updates.
- Hilt application setup and dependency providers are present.
- Unit and Android test source sets contain example and selected feature tests.

### Useful entry points when exploring

- App startup: [`MainActivity.kt`](../app/src/main/java/com/nisr/sauservices/MainActivity.kt), [`SauApplication.kt`](../app/src/main/java/com/nisr/sauservices/SauApplication.kt)
- Root navigation: [`NavHost.kt`](../app/src/main/java/com/nisr/sauservices/navigation/NavHost.kt), feature graphs in `app/src/main/java/com/nisr/sauservices/navigation/`
- Backend client: [`SupabaseClient.kt`](../app/src/main/java/com/nisr/sauservices/data/api/SupabaseClient.kt)
- Dependency setup: [`AppModule.kt`](../app/src/main/java/com/nisr/sauservices/di/AppModule.kt)
- Payment client: [`RazorpayRepository.kt`](../app/src/main/java/com/nisr/sauservices/data/repository/RazorpayRepository.kt)
- Payment server functions: `supabase/functions/create-razorpay-order/` and `supabase/functions/verify-razorpay-payment/`
- Wallet schema/security: [`20240510000000_wallet_security.sql`](../supabase/migrations/20240510000000_wallet_security.sql)
- Android permissions and app configuration: [`AndroidManifest.xml`](../app/src/main/AndroidManifest.xml)

## 4. How a typical flow is intended to work

For a service booking, the intended path is: customer opens a category → chooses a service → supplies address and time → reviews price → confirms a booking → pays or chooses an available payment option → sees confirmation and later tracking/status. In the architecture, Compose screens handle interaction, a ViewModel holds screen state, a repository performs data operations, and Supabase stores or updates shared records. Razorpay payment creation/verification passes through Supabase Edge Functions so payment secrets can stay server-side.

For shopping, the path is similar but uses product browsing, a cart, checkout, an order, and delivery status. The project has separate carts or flows for some categories, so check whether cart contents and order states are deliberately shared or intentionally separate.

## 5. Recommended development plan

Prioritize correctness and security before adding more categories. A dependable booking/payment flow is more valuable than many screens that do not share consistent rules.

### Priority 1: Make one complete journey reliable

Choose one common service and one shopping order, and document their expected states from start to finish. Define allowed transitions, such as `pending → confirmed → in_progress → completed` or `placed → processing → shipped → delivered`, including cancellation and payment failure. Make the backend authoritative for final prices, ownership, provider assignment, and payment status. Ensure retries cannot create duplicate bookings, orders, or wallet credits.

### Priority 2: Review security and data access

- Audit every Supabase table and RPC for Row Level Security and least-privilege policies. The checked-in schema in `app/src/main/xml/supabase_schema.sql` enables RLS for `users`, but does not show policies for every table it creates. Confirm the deployed database has the policies the app requires.
- Keep Razorpay secret keys and Supabase service-role keys only in server-side secrets. Verify the payment function checks the authenticated user, expected order, amount, currency, and one-time processing before it changes a booking or wallet.
- Review client-side API keys and restrictions. The manifest contains a Google Maps key; restrict it to the correct Android package/signing certificate and APIs, and move environment-specific values into appropriate configuration. Do not commit private credentials.
- Revisit `usesCleartextTraffic="true"` and retain it only if a documented development requirement exists.
- Add authorization checks for partner/admin actions; hiding a screen is not a security boundary.

### Priority 3: Simplify architecture and state

- Establish one naming and package convention; the source has both `data.model` and `data.models`, plus `ui.viewmodel` and `ui.viewmodels`.
- Use a consistent screen → ViewModel → repository → backend boundary. Avoid UI screens calling Supabase directly.
- Consolidate duplicate models and overlapping repositories after mapping which flows use them.
- Pick a primary HTTP stack (Ktor or Retrofit) for new work, unless a clear integration need calls for both.
- Adopt a single source of truth for cart, booking/order state, session state, and payment results; represent loading, success, and error explicitly.
- Confirm Room is used for a concrete offline/cache requirement or remove unused setup to reduce maintenance.

### Priority 4: Add confidence through targeted verification

- Write tests around booking/order state transitions, cart totals, discounts/tax/shipping, cancellation/refund rules, and payment verification outcomes.
- Test repository behavior with fake clients and ViewModels with coroutine test dispatchers.
- Add UI tests for the most important journey: sign in → book/order → pay or fail safely → see status.
- Verify on slow/no network, process recreation, expired session, denied location permission, duplicate taps, and app restart.
- Keep a small manual QA checklist for physical devices and a sandbox payment account.

### Priority 5: Improve usability and operations

- Give every network-driven screen clear loading, empty, retry, and offline states.
- Use string resources and consistent formatting for currency, dates, addresses, and accessibility labels.
- Add booking/order history filters, cancellation rules, rescheduling, provider profiles/reviews, and clear support escalation.
- Add push notifications for booking confirmation, provider assignment, arrival, payment outcome, and delivery changes.
- Add structured, privacy-conscious crash/error analytics and backend logs with request IDs; never log tokens, payment secrets, or sensitive user details.

## 6. Ideas to explore after the foundation

1. **Provider availability and scheduling:** service areas, working hours, slot capacity, holidays, booking conflicts, and rescheduling.
2. **Marketplace quality:** verified providers, transparent quotes, ratings, reviews, repeat booking, favorites, and dispute handling.
3. **Smart discovery:** location-aware availability, service filters, price ranges, estimated arrival, and recently used services.
4. **Reliable live tracking:** consent-aware tracking only during active jobs, battery-aware update intervals, stale-location indicators, and automatic stop conditions.
5. **Commerce operations:** stock reservation, out-of-stock substitutions, delivery fees, returns, refunds, and order receipts.
6. **Localization/accessibility:** multiple languages, larger text, screen-reader semantics, and accessible color contrast.
7. **Offline resilience:** cache catalog/address data, preserve an in-progress cart, and queue safe non-payment updates for retry.
8. **Partner tools:** separate worker/merchant workflows for accepting jobs, updating status, managing availability, catalog/stock, and earnings.
9. **Admin and reporting:** role-protected service/provider management, issue resolution, audit trails, and operational metrics.
10. **Release readiness:** separate dev/staging/production Supabase environments, CI build checks, signed release process, database migration discipline, privacy policy, and Play Store data-safety review.

## 7. Suggested first milestones

| Milestone | Deliverable | Done when |
|---|---|---|
| A. Inventory | A single documented booking and order state model | Product/backend decisions and cancellation/payment rules are written down. |
| B. Secure baseline | RLS and server-side payment checks reviewed for all tables and functions | Customer and partner test accounts can only access permitted records; payment tampering/replay is rejected. |
| C. Golden path | One service booking flow completed end-to-end | Success, failure, retry, cancellation, and app restart behave predictably. |
| D. Shared foundations | Common error/loading patterns, repository interfaces, and model conventions | A new category can reuse the shared patterns without copying core booking/payment logic. |
| E. Expand safely | Apply verified flows to the remaining categories | Each category passes the same focused acceptance checklist. |

## 8. How to keep this guide accurate

Update the feature inventory when a user journey is completed and verified against the backend. Record the tested app/build version, environment, expected state changes, and any known limitation. Keep “implemented in source” separate from “tested end-to-end” so future contributors can tell what is ready to rely on.

