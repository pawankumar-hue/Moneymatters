package com.moneymatters.feature.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneymatters.R
import com.moneymatters.core.theme.SunriseAccentPrimary
import com.moneymatters.core.theme.SunriseAccentSoft
import com.moneymatters.core.theme.SunriseBgBase
import com.moneymatters.core.theme.SunriseDanger
import com.moneymatters.core.theme.SunriseSuccess
import com.moneymatters.core.theme.SunriseTextMuted
import com.moneymatters.core.theme.SunriseTextPrimary
import com.moneymatters.core.theme.SunriseTextSecondary

@Composable
fun SignupScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfileSetup: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.signupState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            if (event is AuthNavigationEvent.NavigateToProfileSetup) {
                onNavigateToProfileSetup()
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
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SunriseAccentSoft)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = SunriseTextPrimary)
            Text("Start learning smart money habits today", fontSize = 16.sp, color = SunriseTextSecondary)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.nameInput,
                onValueChange = viewModel::onSignupNameChange,
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.emailInput,
                onValueChange = viewModel::onSignupEmailChange,
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                isError = !uiState.isEmailValid,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.passwordInput,
                onValueChange = viewModel::onSignupPasswordChange,
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password Requirements Checklist
            Column(modifier = Modifier.fillMaxWidth()) {
                PasswordRequirementRow("At least 8 characters", uiState.passwordValidation.hasMinLength)
                PasswordRequirementRow("One uppercase letter", uiState.passwordValidation.hasUppercase)
                PasswordRequirementRow("One lowercase letter", uiState.passwordValidation.hasLowercase)
                PasswordRequirementRow("One number", uiState.passwordValidation.hasDigit)
                PasswordRequirementRow("One special character", uiState.passwordValidation.hasSpecialChar)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.confirmPasswordInput,
                onValueChange = viewModel::onSignupConfirmPasswordChange,
                label = { Text("Confirm Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                isError = !uiState.passwordsMatch,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Terms & Conditions Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.termsAccepted,
                    onCheckedChange = viewModel::onTermsAcceptedChange,
                    colors = CheckboxDefaults.colors(checkedColor = SunriseAccentPrimary)
                )
                Text("I agree to the Terms & Conditions and Privacy Policy", fontSize = 12.sp, color = SunriseTextSecondary)
            }

            uiState.errorMessage?.let { error ->
                Text(error, color = SunriseDanger, fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = viewModel::signupWithEmail,
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
                    Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text("Already have an account? ", color = SunriseTextSecondary, fontSize = 14.sp)
                Text("Login", color = SunriseAccentPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onNavigateToLogin))
            }
        }
    }
}

@Composable
fun PasswordRequirementRow(text: String, isFulfilled: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(
            imageVector = if (isFulfilled) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (isFulfilled) SunriseSuccess else SunriseDanger,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(text, fontSize = 12.sp, color = if (isFulfilled) SunriseSuccess else SunriseTextMuted)
    }
}
