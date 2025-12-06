package com.example.fruitsense.ui.screen.auth

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fruitsense.R
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit, // Biasanya lanjut ke Login atau Verifikasi
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.registerState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.clearRegisterErrors()
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
            // Logo (Sama)
            Icon(painter = painterResource(id = R.drawable.fruitsense), contentDescription = null, modifier = Modifier.size(220.dp), tint = Color.Unspecified)
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Daftar", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = FruitSenseColors.GreenDark)
                    Spacer(modifier = Modifier.height(24.dp))

                    // Full Name
                    OutlinedTextField(
                        value = state.fullName,
                        onValueChange = { viewModel.onRegisterEvent(RegisterEvent.FullNameChanged(it)) },
                        label = { Text("Nama Lengkap") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = state.fullNameError != null,
                        supportingText = { if (state.fullNameError != null) Text(state.fullNameError!!) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Email
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { viewModel.onRegisterEvent(RegisterEvent.EmailChanged(it)) },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = state.emailError != null,
                        supportingText = { if (state.emailError != null) Text(state.emailError!!) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Password
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { viewModel.onRegisterEvent(RegisterEvent.PasswordChanged(it)) },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.onRegisterEvent(RegisterEvent.TogglePasswordVisibility) }) {
                                Icon(if (state.isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null)
                            }
                        },
                        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = state.passwordError != null,
                        supportingText = { if (state.passwordError != null) Text(state.passwordError!!) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Confirm Password
                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = { viewModel.onRegisterEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
                        label = { Text("Konfirmasi Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.onRegisterEvent(RegisterEvent.ToggleConfirmPasswordVisibility) }) {
                                Icon(if (state.isConfirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null)
                            }
                        },
                        visualTransformation = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = state.confirmPasswordError != null,
                        supportingText = { if (state.confirmPasswordError != null) Text(state.confirmPasswordError!!) }
                    )

                    if (state.registerError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.registerError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.register(onSuccess = onRegisterSuccess)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FruitSenseColors.GreenDark),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Daftar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FruitSenseColors.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sudah punya akun? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(onClick = onNavigateToLogin) {
                            Text("Masuk", color = FruitSenseColors.GreenOlive, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}