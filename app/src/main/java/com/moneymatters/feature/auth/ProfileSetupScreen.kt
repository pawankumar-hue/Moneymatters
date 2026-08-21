package com.moneymatters.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneymatters.core.theme.SunriseAccentPrimary
import com.moneymatters.core.theme.SunriseAccentSoft
import com.moneymatters.core.theme.SunriseBgBase
import com.moneymatters.core.theme.SunriseDanger
import com.moneymatters.core.theme.SunriseTextMuted
import com.moneymatters.core.theme.SunriseTextPrimary
import com.moneymatters.core.theme.SunriseTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupScreen(
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.profileSetupState.collectAsStateWithLifecycle()

    val incomeOptions = listOf(
        "Less than ₹3,000",
        "₹3,000 - ₹10,000",
        "₹10,000 - ₹20,000",
        "More than ₹20,000",
        "Prefer not to say"
    )

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            if (event is AuthNavigationEvent.NavigateToHome) {
                onNavigateToHome()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SunriseBgBase)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome to Money Matters!", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = SunriseTextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Let's personalize your learning experience", fontSize = 16.sp, color = SunriseTextSecondary)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.college,
                onValueChange = viewModel::onCollegeChange,
                label = { Text("College / University Name") },
                leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Text("Monthly Income / Pocket Money Range", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SunriseTextPrimary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Income Range Chip Group
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                incomeOptions.forEach { range ->
                    val isSelected = uiState.selectedIncomeRange == range
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onIncomeRangeSelect(range) },
                        label = { Text(range, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SunriseAccentSoft,
                            selectedLabelColor = SunriseAccentPrimary,
                            containerColor = Color.White,
                            labelColor = SunriseTextSecondary
                        )
                    )
                }
            }

            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(error, color = SunriseDanger, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = viewModel::saveProfileSetup,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunriseAccentPrimary)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Get Started", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Skip for now",
                color = SunriseTextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onNavigateToHome)
            )
        }
    }
}
