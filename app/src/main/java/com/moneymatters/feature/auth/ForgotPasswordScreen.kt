package com.moneymatters.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.moneymatters.core.theme.SunriseBgBase
import com.moneymatters.core.theme.SunriseDanger
import com.moneymatters.core.theme.SunriseSuccess
import com.moneymatters.core.theme.SunriseTextPrimary
import com.moneymatters.core.theme.SunriseTextSecondary

@Composable
fun ForgotPasswordScreen(
    onNavigateBackToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.forgotPasswordState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SunriseBgBase)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Reset Password", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = SunriseTextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Enter your email to receive a password reset link",
                fontSize = 16.sp,
                color = SunriseTextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = uiState.emailInput,
                onValueChange = viewModel::onForgotPasswordEmailChange,
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                isError = !uiState.isEmailValid,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.isSuccess) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Reset link sent to your email! Check your inbox.",
                    color = SunriseSuccess,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(error, color = SunriseDanger, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::sendPasswordResetEmail,
                enabled = !uiState.isLoading && uiState.isEmailValid && uiState.emailInput.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SunriseAccentPrimary)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Send Reset Link", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Back to Login",
                color = SunriseAccentPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onNavigateBackToLogin)
            )
        }
    }
}
