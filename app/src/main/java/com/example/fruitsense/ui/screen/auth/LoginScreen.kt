package com.example.fruitsense.ui.screen.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fruitsense.R
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel() // Gunakan Hilt
) {
    val state by viewModel.loginState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.clearLoginErrors()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.fruitsense),
                contentDescription = "Logo",
                modifier = Modifier.size(220.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Masuk",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = FruitSenseColors.GreenDark
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { viewModel.onLoginEvent(LoginEvent.EmailChanged(it)) },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        isError = state.emailError != null,
                        supportingText = { if (state.emailError != null) Text(text = state.emailError!!) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { viewModel.onLoginEvent(LoginEvent.PasswordChanged(it)) },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            val image = if (state.isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            IconButton(onClick = { viewModel.onLoginEvent(LoginEvent.TogglePasswordVisibility) }) {
                                Icon(imageVector = image, contentDescription = "Toggle")
                            }
                        },
                        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        isError = state.passwordError != null,
                        supportingText = { if (state.passwordError != null) Text(text = state.passwordError!!) }
                    )

                    // Global Error
                    if (state.loginError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.loginError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }

                    // Forgot Password Link
                    TextButton(
                        onClick = onNavigateToForgotPassword,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Lupa Password?", color = FruitSenseColors.GreenOlive, fontWeight = FontWeight.Bold)
                    }

                    // Button
                    Button(
                        onClick = {
                            // Panggil fungsi login dengan callback success
                            viewModel.login(onSuccess = onLoginSuccess)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FruitSenseColors.GreenDark),
                        enabled = !state.isLoading // Disable saat loading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Masuk", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Belum punya akun? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(onClick = onNavigateToRegister) {
                            Text("Daftar", color = FruitSenseColors.GreenOlive, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}