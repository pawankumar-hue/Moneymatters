package com.moneymatters

import android.os.Build
import android.os.Bundle
import android.view.Display
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.moneymatters.core.designsystem.DeepNavyBackground
import com.moneymatters.core.designsystem.MoneyMattersTheme
import com.moneymatters.data.repository.UserPreferencesRepository
import com.moneymatters.navigation.MainNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pitch Black Status Bar & Navigation Bar to match OLED dark theme
        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK

        // Hardware Acceleration for 60fps+ rendering
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )

        // Lock display refresh rate to 90Hz max for consistent frame pacing
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                @Suppress("DEPRECATION")
                val display: Display? = windowManager.defaultDisplay
                val modes = display?.supportedModes
                // Lock to 90Hz — pick closest mode at or below 90Hz
                val targetMode = modes?.filter { it.refreshRate <= 90.1f }?.maxByOrNull { it.refreshRate }
                    ?: modes?.minByOrNull { it.refreshRate } // Fallback to lowest if no 90Hz mode

                if (targetMode != null) {
                    val lp = window.attributes
                    lp.preferredDisplayModeId = targetMode.modeId
                    window.attributes = lp
                }
            }
        } catch (e: Exception) {
            // Fallback safely if display mode switching is restricted by OS
        }

        setContent {
            val userProfile by userPreferencesRepository.userProfile.collectAsStateWithLifecycle(
                initialValue = com.moneymatters.data.model.UserProfileData()
            )

            MoneyMattersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepNavyBackground
                ) {
                    MainNavGraph(currentLanguageCode = userProfile.selectedLanguageCode)
                }
            }
        }
    }
}
