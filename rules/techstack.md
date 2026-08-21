# MONEY MATTERS — TECH STACK & ARCHITECTURE

> Complete technical specification for the Money Matters Android app.
> Use this as the single source of truth for all development decisions.

---

## 1. PLATFORM OVERVIEW

| Property | Value |
| :--- | :--- |
| **Language** | Kotlin 1.9.22+ |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM + Clean Architecture |
| **Build System** | Gradle 8.5+ (Kotlin DSL) |
| **Min SDK** | 24 (Android 7.0) |
| **Target SDK** | 34 (Android 14) |
| **Monetization** | RevenueCat SDK |

---

## 2. PROJECT STRUCTURE

```
MoneyMatters/
├── build.gradle.kts                         ← Root build (plugins, repos)
├── settings.gradle.kts                      ← Module includes
├── gradle.properties                        ← SDK versions, Kotlin opts
├── gradle/
│   └── libs.versions.toml                   ← Version catalog
│
├── app/                                     ← Main app module
│   ├── build.gradle.kts                     ← Android app config
│   ├── proguard-rules.pro
│   └── src/
│       └── main/
│           ├── java/com/moneymatters/
│           │   ├── MoneyMattersApp.kt       ← Application class
│           │   ├── MainActivity.kt          ← Single activity entry
│           │   │
│           │   ├── core/
│           │   │   ├── theme/
│           │   │   │   ├── Color.kt         ← Color tokens
│           │   │   │   ├── Theme.kt        ← Material3 theme
│           │   │   │   └── Type.kt         ← Typography
│           │   │   ├── utils/
│           │   │   │   ├── Formatters.kt   ← ₹ formatting, dates
│           │   │   │   └── Extensions.kt   ← Common extensions
│           │   │   └── constants/
│           │   │       └── AppConstants.kt
│           │   │
│           │   ├── data/
│           │   │   ├── model/
│           │   │   │   ├── Lesson.kt
│           │   │   │   ├── Card.kt
│           │   │   │   ├── UserProgress.kt
│           │   │   │   └── Badge.kt
│           │   │   ├── repository/
│           │   │   │   ├── LessonRepository.kt
│           │   │   │   ├── UserRepository.kt
│           │   │   │   └── BadgeRepository.kt
│           │   │   └── source/
│           │   │       ├── local/
│           │   │       │   ├── AppDatabase.kt      ← Room database
│           │   │       │   ├── dao/
│           │   │       │   │   ├── UserDao.kt
│           │   │       │   │   ├── LessonDao.kt
│           │   │       │   │   └── ExpenseDao.kt
│           │   │       │   └── SettingsStorage.kt   ← EncryptedSharedPreferences
│           │   │       └── remote/
│           │   │           └── ApiService.kt
│           │   │
│           │   ├── domain/
│           │   │   ├── usecase/
│           │   │   │   ├── AwardXPUseCase.kt
│           │   │   │   ├── UpdateStreakUseCase.kt
│           │   │   │   └── CheckBadgeUseCase.kt
│           │   │   └── calculator/
│           │   │       ├── SIPCalculator.kt
│           │   │       ├── EmergencyFundCalc.kt
│           │   │       └── CreditCardTrapCalc.kt
│           │   │
│           │   ├── feature/
│           │   │   ├── splash/
│           │   │   │   ├── SplashScreen.kt
│           │   │   │   └── SplashViewModel.kt
│           │   │   ├── onboarding/
│           │   │   │   ├── OnboardingScreen.kt
│           │   │   │   ├── LanguageSelectScreen.kt
│           │   │   │   └── PersonaQuizScreen.kt
│           │   │   ├── home/
│           │   │   │   ├── HomeScreen.kt
│           │   │   │   └── HomeViewModel.kt
│           │   │   ├── lesson/
│           │   │   │   ├── LessonPlaybackScreen.kt
│           │   │   │   ├── LessonViewModel.kt
│           │   │   │   └── CardRenderers.kt
│           │   │   ├── track/
│           │   │   │   ├── ToolsHomeScreen.kt
│           │   │   │   ├── CalculatorScreen.kt
│           │   │   │   ├── CalculatorViewModel.kt
│           │   │   │   └── ExpenseTrackerScreen.kt
│           │   │   ├── profile/
│           │   │   │   ├── ProfileScreen.kt
│           │   │   │   ├── BadgesScreen.kt
│           │   │   │   ├── LeaderboardScreen.kt
│           │   │   │   └── SettingsScreen.kt
│           │   │   └── paywall/
│           │   │       ├── PaywallScreen.kt
│           │   │       └── PaywallViewModel.kt
│           │   │
│           │   ├── di/
│           │   │   └── AppModule.kt          ← Hilt modules
│           │   │
│           │   └── navigation/
│           │       └── NavGraph.kt           ← NavHost
│           │
│           ├── res/
│           │   ├── drawable/
│           │   ├── values/
│           │   ├── values-hi/               ← Hindi strings
│           │   ├── values-bn/               ← Bengali
│           │   └── ...
│           │
│           └── assets/
│               └── content/                  ← Lesson JSON files
│                   ├── hinglish/
│                   ├── hindi/
│                   ├── english/
│                   └── ...
│
├── .github/
│   └── workflows/
│       └── build.yml
│
└── README.md
```

---

## 3. DEPENDENCY VERSION CATALOG

### 3.1 `gradle/libs.versions.toml`

```toml
[versions]
# Kotlin & Compose
kotlin = "1.9.22"
compose-bom = "2024.02.00"
compose-compiler = "1.5.8"
activity-compose = "1.8.2"

# Android
agp = "8.2.2"
androidx-core = "1.12.0"
androidx-lifecycle = "2.7.0"
androidx-navigation = "2.7.7"
room = "2.6.1"
hilt = "2.48"
hilt-navigation = "1.1.0"

# RevenueCat
revenuecat = "7.5.0"

# Networking
ktor = "2.3.7"
kotlinx-serialization = "1.6.2"
kotlinx-datetime = "0.5.0"
kotlinx-coroutines = "1.7.3"

# UI Libraries
lottie = "6.1.0"
coil = "2.5.0"
vico = "1.13.0"
datastore = "1.0.0"

# Testing
junit = "4.13.2"
androidx-test = "1.1.5"
espresso = "3.5.1"

[libraries]
# Compose
compose-bom = "androidx.compose:compose-bom:2024.02.00"
compose-ui = "androidx.compose.ui:ui"
compose-ui-tooling = "androidx.compose.ui:ui-tooling"
compose-ui-tooling-preview = "androidx.compose.ui:ui-tooling-preview"
compose-material3 = "androidx.compose.material3:material3"
compose-material-icons = "androidx.compose.material:material-icons-extended"
compose-foundation = "androidx.compose.foundation:foundation"
compose-animation = "androidx.compose.animation:animation"

# Android
androidx-core-ktx = "androidx.core:core-ktx:1.12.0"
androidx-activity-compose = "androidx.activity:activity-compose:1.8.2"
androidx-lifecycle-viewmodel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0"
androidx-lifecycle-runtime = "androidx.lifecycle:lifecycle-runtime-compose:2.7.0"
androidx-navigation-compose = "androidx.navigation:navigation-compose:2.7.7"

# Room
room-runtime = "androidx.room:room-runtime:2.6.1"
room-ktx = "androidx.room:room-ktx:2.6.1"
room-compiler = "androidx.room:room-compiler:2.6.1"

# Hilt
hilt-android = "com.google.dagger:hilt-android:2.48"
hilt-compiler = "com.google.dagger:hilt-compiler:2.48"
hilt-navigation = "androidx.hilt:hilt-navigation-compose:1.1.0"

# RevenueCat
revenuecat = "com.revenuecat.purchases:purchases:7.5.0"

# Networking
ktor-client-core = "io.ktor:ktor-client-core:2.3.7"
ktor-client-okhttp = "io.ktor:ktor-client-okhttp:2.3.7"
ktor-content-negotiation = "io.ktor:ktor-client-content-negotiation:2.3.7"
ktor-serialization-json = "io.ktor:ktor-serialization-kotlinx-json:2.3.7"

# Serialization
kotlinx-serialization-json = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2"

# DateTime
kotlinx-datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.5.0"

# Coroutines
kotlinx-coroutines-core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3"
kotlinx-coroutines-android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"

# DataStore
datastore-preferences = "androidx.datastore:datastore-preferences:1.0.0"

# Animations
lottie-compose = "com.airbnb.android:lottie-compose:6.1.0"

# Image Loading
coil-compose = "io.coil-kt:coil-compose:2.5.0"

# Charts
vico-compose = "com.patrykandpatrick.vico:compose:1.13.0"
vico-compose-m3 = "com.patrykandpatrick.vico:compose-m3:1.13.0"

# Testing
junit = "junit:junit:4.13.2"
androidx-test-ext = "androidx.test.ext:junit:1.1.5"
androidx-test-espresso = "androidx.test.espresso:espresso-core:3.5.1"

[plugins]
kotlin-android = "org.jetbrains.kotlin.android:1.9.22"
kotlin-kapt = "org.jetbrains.kotlin.kapt:1.9.22"
kotlin-serialization = "org.jetbrains.kotlin.plugin.serialization:1.9.22"
android-application = "com.android.application:8.2.2"
hilt = "com.google.dagger.hilt.android:2.48"
ksp = "com.google.devtools.ksp:1.9.22-1.0.17"
```

---

## 4. BUILD CONFIGURATION

### 4.1 Root `build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
```

### 4.2 `settings.gradle.kts`

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "MoneyMatters"
include(":app")
```

### 4.3 `gradle.properties`

```properties
kotlin.code.style=official
android.useAndroidX=true
android.nonTransitiveRClass=true

# Gradle
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
```

### 4.4 `app/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.moneymatters"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.moneymatters"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Compose
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.foundation)
    implementation(libs.compose.animation)
    debugImplementation(libs.compose.ui.tooling)

    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation)

    // RevenueCat
    implementation(libs.revenuecat)

    // Networking
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.content.negotiation)
    implementation(libs.ktor.serialization.json)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // DateTime
    implementation(libs.kotlinx.datetime)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // DataStore
    implementation(libs.datastore.preferences)

    // Animations
    implementation(libs.lottie.compose)

    // Image Loading
    implementation(libs.coil.compose)

    // Charts
    implementation(libs.vico.compose.m3)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso)
}
```

---

## 5. ARCHITECTURE PATTERN

### 5.1 MVVM + Clean Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │  Composable  │  │  Composable  │  │  Composable  │         │
│  │  Screens     │  │  Screens     │  │  Screens     │         │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘         │
│         │                │                │                  │
│  ┌──────▼────────────────▼────────────────▼──────┐         │
│  │              ViewModel Layer                    │         │
│  │         (StateFlow<UIState>)                    │         │
│  └──────────────────┬────────────────────────────┘         │
├─────────────────────┼───────────────────────────────────────┤
│                     │         Domain Layer                   │
│  ┌──────────────────▼────────────────────────────┐         │
│  │              Use Cases                          │         │
│  │  AwardXP │ UpdateStreak │ CheckBadge │ Calc... │         │
│  └──────────────────┬────────────────────────────┘         │
│                     │                                        │
│  ┌──────────────────▼────────────────────────────┐         │
│  │           Repository Interfaces                 │         │
│  └──────────────────┬────────────────────────────┘         │
├─────────────────────┼───────────────────────────────────────┤
│                     │         Data Layer                     │
│  ┌──────────────────▼────────────────────────────┐         │
│  │         Repository Implementations              │         │
│  └──────┬───────────────────────────────┬────────┘         │
│         │                               │                   │
│  ┌──────▼──────┐                ┌───────▼───────┐          │
│  │   Local      │                │   Remote       │          │
│  │   Storage    │                │   API          │          │
│  │ (Room + DS)  │                │  (Ktor)        │          │
│  └─────────────┘                └───────────────┘          │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 State Management

```kotlin
// UI State (immutable)
data class HomeState(
    val isLoading: Boolean = true,
    val userProgress: UserProgress? = null,
    val todayLesson: Lesson? = null,
    val streakState: StreakState? = null,
    val error: String? = null
)

// ViewModel
class HomeViewModel @Inject constructor(
    private val getUserProgress: GetUserProgressUseCase,
    private val getTodayLesson: GetTodayLessonUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val progress = getUserProgress()
                val lesson = getTodayLesson()
                _state.update {
                    it.copy(isLoading = false, userProgress = progress, todayLesson = lesson)
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}

// Composable
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when {
        state.isLoading -> LoadingContent()
        state.error != null -> ErrorContent(state.error)
        else -> HomeContent(state)
    }
}
```

---

## 6. DATA MODELS

### 6.1 Core Models

```kotlin
@Serializable
data class Lesson(
    val lessonId: String,
    val moduleId: String,
    val title: String,
    val subtitle: String = "",
    val estimatedMinutes: Int = 3,
    val xpReward: Int = 30,
    val difficulty: String = "easy",
    val isFree: Boolean = true,
    val cards: List<Card>,
    val spacedRepetitionDays: List<Int> = listOf(1, 3, 7, 21)
)

@Serializable
sealed class Card {
    abstract val cardId: String
    abstract val order: Int
    abstract val emoji: String

    @Serializable
    data class Hook(
        override val cardId: String,
        override val order: Int,
        override val emoji: String,
        val title: String,
        val content: String
    ) : Card()

    @Serializable
    data class Story(
        override val cardId: String,
        override val order: Int,
        override val emoji: String,
        val title: String,
        val content: String,
        val character: String = "Rahul"
    ) : Card()

    @Serializable
    data class Concept(
        override val cardId: String,
        override val order: Int,
        override val emoji: String,
        val title: String,
        val content: List<String>,
        val chartData: ChartData? = null
    ) : Card()

    @Serializable
    data class Math(
        override val cardId: String,
        override val order: Int,
        override val emoji: String,
        val title: String,
        val formula: String,
        val explanation: String,
        val example: String
    ) : Card()

    @Serializable
    data class Quiz(
        override val cardId: String,
        override val order: Int,
        override val emoji: String,
        val title: String,
        val question: String,
        val options: List<String>,
        val correctIndex: Int,
        val explanation: String,
        val xpReward: Int = 5
    ) : Card()

    @Serializable
    data class Action(
        override val cardId: String,
        override val order: Int,
        override val emoji: String,
        val title: String,
        val prompt: String,
        val inputType: String = "NUMBER",
        val challenge: String = ""
    ) : Card()
}

@Serializable
data class UserProgress(
    val userId: String,
    val xpTotal: Int,
    val level: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val streakFreezes: Int,
    val lastActiveDate: String,
    val completedLessons: List<String>,
    val unlockedBadges: List<String>,
    val persona: String,
    val language: String
)

@Serializable
data class StreakState(
    val currentStreak: Int,
    val longestStreak: Int,
    val streakFreezes: Int,
    val lastActiveDate: String,
    val nextReminderAt: String
)

@Serializable
data class Badge(
    val badgeId: String,
    val title: String,
    val description: String,
    val emoji: String,
    val category: String,
    val tier: String,
    val isUnlocked: Boolean = false,
    val unlockedDate: String? = null
)
```

### 6.2 Room Entities

```kotlin
@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val userId: String,
    val xpTotal: Int,
    val level: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val streakFreezes: Int,
    val lastActiveDate: String,
    val completedLessons: String,  // JSON string
    val unlockedBadges: String,    // JSON string
    val persona: String,
    val language: String
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val category: String,
    val amount: Double,
    val description: String,
    val date: String
)
```

---

## 7. REVENUECAT INTEGRATION

### 7.1 Where to Use RevenueCat SDK

| Location | File | Purpose |
| :--- | :--- | :--- |
| **App Initialization** | `MoneyMattersApp.kt` | Initialize RevenueCat SDK with API key |
| **Paywall Screen** | `PaywallScreen.kt` | Show purchase options, handle transactions |
| **Paywall ViewModel** | `PaywallViewModel.kt` | Fetch offerings, purchase, restore |
| **Home Screen** | `HomeScreen.kt` | Check if user has Pro access |
| **Lesson Screen** | `LessonPlaybackScreen.kt` | Lock Module 7+ for free users |
| **Calculator Screen** | `CalculatorScreen.kt` | Lock advanced features for free users |
| **Profile Screen** | `ProfileScreen.kt` | Show upgrade button, subscription status |
| **DI Module** | `di/AppModule.kt` | Provide RevenueCat instance via Hilt |

### 7.2 Entitlement Model

| Entitlement ID | Access Level | What It Unlocks |
| :--- | :--- | :--- |
| `free_tier` | Free (default) | Modules 1-6, 3 basic calculators, Quiz Level 1 |
| `pro_access` | Rupaiya Pro | Modules 7-24, all 10 calculators, full assessments, certificates |

### 7.3 Product Packages (RevenueCat Dashboard)

| Package ID | Product ID | Price | Target |
| :--- | :--- | :--- | :--- |
| `$rc_monthly` | `rupaiya_pro_monthly` | ₹99/month | Students testing |
| `$rc_annual` | `rupaiya_pro_annual` | ₹499/year | Best value (7-day trial) |
| `$rc_lifetime` | `rupaiya_pro_lifetime` | ₹1,499 one-time | Power users |

### 7.4 Paywall Trigger Points

| Trigger | Screen | When to Show |
| :--- | :--- | :--- |
| Module 7 tap | Lesson list | User taps locked module |
| Advanced calc output | Calculator | User taps "Export PDF / Report" |
| Assessment detail | Quiz result | User taps "View detailed analysis" |
| Profile upgrade | Profile | User taps "Upgrade to Pro" |
| Settings upgrade | Settings | User taps subscription banner |

### 7.5 Implementation Code

#### `MoneyMattersApp.kt` — Initialize SDK

```kotlin
package com.moneymatters

import android.app.Application
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MoneyMattersApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize RevenueCat
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(this, "rc_public_key_here")
                .appUserID(null)  // Auto-generate
                .build()
        )
    }
}
```

#### `di/AppModule.kt` — Provide RevenueCat

```kotlin
package com.moneymatters.di

import com.revenuecat.purchases.Purchases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RevenueCatModule {

    @Provides
    @Singleton
    fun providePurchases(): Purchases = Purchases.sharedInstance
}
```

#### `feature/paywall/PaywallViewModel.kt`

```kotlin
package com.moneymatters.feature.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.purchasePackageWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaywallState(
    val isLoading: Boolean = true,
    val offerings: Offerings? = null,
    val selectedPackage: Package? = null,
    val isPurchasing: Boolean = false,
    val purchaseSuccess: Boolean = false,
    val error: String? = null,
    val hasProAccess: Boolean = false
)

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val purchases: Purchases
) : ViewModel() {

    private val _state = MutableStateFlow(PaywallState())
    val state: StateFlow<PaywallState> = _state.asStateFlow()

    init {
        loadOfferings()
        checkProAccess()
    }

    private fun loadOfferings() {
        viewModelScope.launch {
            purchases.getOfferingsWith(
                onError = { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                },
                onSuccess = { offerings ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            offerings = offerings,
                            selectedPackage = offerings.current?.availablePackages?.firstOrNull()
                        )
                    }
                }
            )
        }
    }

    private fun checkProAccess() {
        viewModelScope.launch {
            purchases.getCustomerInfoWith(
                onSuccess = { customerInfo ->
                    val hasPro = customerInfo.entitlements.active.containsKey("pro_access")
                    _state.update { it.copy(hasProAccess = hasPro) }
                },
                onError = { /* Handle error */ }
            )
        }
    }

    fun selectPackage(packageItem: Package) {
        _state.update { it.copy(selectedPackage = packageItem) }
    }

    fun purchase() {
        val packageItem = _state.value.selectedPackage ?: return

        viewModelScope.launch {
            _state.update { it.copy(isPurchasing = true) }

            purchases.purchasePackageWith(
                packageToPurchase = packageItem,
                onError = { error, _ ->
                    _state.update { it.copy(isPurchasing = false, error = error.message) }
                },
                onSuccess = { _, customerInfo ->
                    val hasPro = customerInfo.entitlements.active.containsKey("pro_access")
                    _state.update {
                        it.copy(isPurchasing = false, purchaseSuccess = true, hasProAccess = hasPro)
                    }
                }
            )
        }
    }

    fun restore() {
        viewModelScope.launch {
            _state.update { it.copy(isPurchasing = true) }

            purchases.restorePurchasesWith(
                onError = { error ->
                    _state.update { it.copy(isPurchasing = false, error = error.message) }
                },
                onSuccess = { customerInfo ->
                    val hasPro = customerInfo.entitlements.active.containsKey("pro_access")
                    _state.update {
                        it.copy(isPurchasing = false, hasProAccess = hasPro)
                    }
                }
            )
        }
    }
}
```

#### `feature/paywall/PaywallScreen.kt`

```kotlin
package com.moneymatters.feature.paywall

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneymatters.core.theme.*
import com.revenuecat.purchases.Package

@Composable
fun PaywallScreen(
    onDismiss: () -> Unit,
    viewModel: PaywallViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Auto-dismiss on success
    LaunchedEffect(state.purchaseSuccess) {
        if (state.purchaseSuccess) onDismiss()
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Sunrise_BgBase, Color(0xFFFFF3E8))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Close button
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) {
                    Text("✕", fontSize = 20.sp, color = Sunrise_TextMuted)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Title
            Text("🏆", fontSize = 48.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                "Rupaiya Pro",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Sunrise_TextPrimary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Unlock all 24 modules + 10 calculators",
                fontSize = 16.sp,
                color = Sunrise_TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // Offerings
            state.offerings?.current?.availablePackages?.forEach { packageItem ->
                PackageCard(
                    packageItem = packageItem,
                    isSelected = state.selectedPackage?.identifier == packageItem.identifier,
                    onSelect = { viewModel.selectPackage(packageItem) }
                )
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.weight(1f))

            // Purchase button
            Button(
                onClick = { viewModel.purchase() },
                enabled = !state.isPurchasing && state.selectedPackage != null,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Sunrise_AccentPrimary)
            ) {
                if (state.isPurchasing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Subscribe Now", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Restore
            TextButton(onClick = { viewModel.restore() }) {
                Text("Restore Purchases", color = Sunrise_TextMuted, fontSize = 14.sp)
            }

            // Continue free
            TextButton(onClick = onDismiss) {
                Text("Continue Free", color = Sunrise_TextSecondary, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun PackageCard(
    packageItem: Package,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (isSelected) Sunrise_AccentPrimary else Color(0xFFE8E0D4)
    val bgColor = if (isSelected) Sunrise_AccentSoft else Sunrise_BgElevated

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .background(bgColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onSelect)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                packageItem.product.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Sunrise_TextPrimary
            )
            Text(
                packageItem.product.priceString,
                fontSize = 14.sp,
                color = Sunrise_TextSecondary
            )
        }
        if (isSelected) {
            Icon(Icons.Filled.Check, contentDescription = "Selected", tint = Sunrise_AccentPrimary)
        }
    }
}
```

#### `feature/lesson/LessonPlaybackScreen.kt` — Check Pro Access

```kotlin
@Composable
fun LessonPlaybackScreen(
    lessonId: String,
    onShowPaywall: () -> Unit,
    viewModel: LessonViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Check if lesson is locked
    LaunchedEffect(state.lesson) {
        val lesson = state.lesson ?: return@LaunchedEffect
        if (!lesson.isFree && !state.hasProAccess) {
            onShowPaywall()
        }
    }

    // ... rest of lesson screen
}
```

#### `feature/home/HomeScreen.kt` — Show Pro Badge

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column {
        // Pro badge for subscribers
        if (state.hasProAccess) {
            Surface(
                color = Sunrise_Gold.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "⭐ Pro Member",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Sunrise_Gold
                )
            }
        }

        // ... rest of home screen
    }
}
```

---

## 8. LOCAL STORAGE

### 8.1 Room Database

```kotlin
@Database(
    entities = [UserProgressEntity::class, ExpenseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun lessonDao(): LessonDao
    abstract fun expenseDao(): ExpenseDao
}
```

### 8.2 DataStore Preferences

```kotlin
class SettingsStorageImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsStorage {

    private object Keys {
        val HAS_ONBOARDING = booleanPreferencesKey("hasCompletedOnboarding")
        val USER_ID = stringPreferencesKey("userId")
        val XP_TOTAL = intPreferencesKey("xpTotal")
        val CURRENT_STREAK = intPreferencesKey("currentStreak")
        val LAST_ACTIVE = stringPreferencesKey("lastActiveDate")
        val LANGUAGE = stringPreferencesKey("language")
        val NOTIFICATIONS = booleanPreferencesKey("notificationsEnabled")
        val REMINDER_TIME = stringPreferencesKey("reminderTime")
        val REDUCED_MOTION = booleanPreferencesKey("reducedMotion")
    }

    override var hasCompletedOnboarding: Boolean
        get() = runBlocking { dataStore.data.first()[Keys.HAS_ONBOARDING] ?: false }
        set(value) { runBlocking { dataStore.edit { it[Keys.HAS_ONBOARDING] = value } } }

    override var xpTotal: Int
        get() = runBlocking { dataStore.data.first()[Keys.XP_TOTAL] ?: 0 }
        set(value) = runBlocking { dataStore.edit { it[Keys.XP_TOTAL] = value } }

    override var language: String
        get() = runBlocking { dataStore.data.first()[Keys.LANGUAGE] ?: "hinglish" }
        set(value) = runBlocking { dataStore.edit { it[Keys.LANGUAGE] = value } }

    override fun clear() = runBlocking { dataStore.edit { it.clear() } }
}
```

---

## 9. NAVIGATION

```kotlin
// Navigation destinations
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Lesson : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }
    object Calculator : Screen("calculator/{calcId}") {
        fun createRoute(calcId: String) = "calculator/$calcId"
    }
    object Profile : Screen("profile")
    object Badges : Screen("badges")
    object Settings : Screen("settings")
    object Paywall : Screen("paywall")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToNext = { target ->
                    when (target) {
                        SplashNavigationTarget.ONBOARDING -> {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                        SplashNavigationTarget.HOME -> {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onLessonClick = { lessonId ->
                    navController.navigate(Screen.Lesson.createRoute(lessonId))
                },
                onCalculatorClick = { calcId ->
                    navController.navigate(Screen.Calculator.createRoute(calcId))
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onShowPaywall = {
                    navController.navigate(Screen.Paywall.route)
                }
            )
        }

        composable(Screen.Lesson.route) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
            LessonPlaybackScreen(
                lessonId = lessonId,
                onShowPaywall = { navController.navigate(Screen.Paywall.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Paywall.route) {
            PaywallScreen(onDismiss = { navController.popBackStack() })
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBadgesClick = { navController.navigate(Screen.Badges.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onUpgradeClick = { navController.navigate(Screen.Paywall.route) }
            )
        }

        // ... more routes
    }
}
```

---

## 10. DEPENDENCY INJECTION (Hilt)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "moneymatters.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("settings")
        }
    }

    @Provides
    @Singleton
    fun provideSettingsStorage(dataStore: DataStore<Preferences>): SettingsStorage {
        return SettingsStorageImpl(dataStore)
    }

    @Provides
    @Singleton
    fun providePurchases(): Purchases = Purchases.sharedInstance

    @Provides
    fun provideUserDao(db: AppDatabase) = db.userDao()

    @Provides
    fun provideLessonDao(db: AppDatabase) = db.lessonDao()

    @Provides
    fun provideExpenseDao(db: AppDatabase) = db.expenseDao()
}
```

---

## 11. DEVELOPMENT WORKFLOW

### 11.1 Daily Commands

```bash
# Start continuous build (auto-build on save)
./gradlew installDebug --continuous

# Build once
./gradlew assembleDebug

# Run tests
./gradlew testDebug

# Clean build
./gradlew clean assembleDebug
```

### 11.2 ADB Wireless Setup

```bash
# One-time setup
adb tcpip 5555
adb connect 192.168.1.XXX:5555

# Verify
adb devices

# Install APK
adb install app/build/outputs/apk/debug/app-debug.apk

# View logs
adb logcat | grep MoneyMatters
```

---

## 12. SHIPATHON SUBMISSION CHECKLIST

- [ ] Android APK builds successfully
- [ ] All 24 modules have content (JSON)
- [ ] Splash screen works with animation
- [ ] Lesson playback (card swipe) works
- [ ] At least 1 calculator works
- [ ] Gamification (XP, streak, badges) works
- [ ] RevenueCat integration works (purchase, restore, entitlement check)
- [ ] 10 languages render correctly
- [ ] Dark mode works
- [ ] Reduced motion respected
- [ ] No crashes on mid-range device
- [ ] APK size < 15 MB
- [ ] Demo video recorded
- [ ] Pitch deck ready

---

*Last updated: August 2026*
*Version: 1.0.0*
*For: Money Matters — Shipathon 2026*
