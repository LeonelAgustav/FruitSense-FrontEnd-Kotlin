package com.example.fruitsense.ui.screen.auth.forgot_password

import androidx.compose.foundation.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fruitsense.R
import com.example.fruitsense.ui.screen.auth.AuthViewModel
import com.example.fruitsense.ui.screen.auth.ResetPasswordEvent
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun ResetPasswordScreen(
    onPasswordResetSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.resetPasswordState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.clearResetErrors()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(painter = painterResource(id = R.drawable.fruitsense), contentDescription = "Logo", modifier = Modifier.size(180.dp), tint = Color.Unspecified)
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Buat Password Baru", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = FruitSenseColors.GreenDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Masukkan kata sandi baru Anda.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Password
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { viewModel.onResetEvent(ResetPasswordEvent.PasswordChanged(it)) },
                        label = { Text("Password Baru") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        trailingIcon = {
                            val image = if (state.isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            IconButton(onClick = { viewModel.onResetEvent(ResetPasswordEvent.TogglePasswordVisibility) }) {
                                Icon(imageVector = image, contentDescription = "Toggle", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        },
                        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FruitSenseColors.GreenDark,
                            errorBorderColor = MaterialTheme.colorScheme.error
                        ),
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                        isError = state.passwordError != null,
                        supportingText = { if (state.passwordError != null) Text(text = state.passwordError!!, color = MaterialTheme.colorScheme.error) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password
                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = { viewModel.onResetEvent(ResetPasswordEvent.ConfirmPasswordChanged(it)) },
                        label = { Text("Konfirmasi Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        trailingIcon = {
                            val image = if (state.isConfirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            IconButton(onClick = { viewModel.onResetEvent(ResetPasswordEvent.ToggleConfirmPasswordVisibility) }) {
                                Icon(imageVector = image, contentDescription = "Toggle", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        },
                        visualTransformation = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FruitSenseColors.GreenDark,
                            errorBorderColor = MaterialTheme.colorScheme.error
                        ),
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                        isError = state.confirmPasswordError != null,
                        supportingText = { if (state.confirmPasswordError != null) Text(text = state.confirmPasswordError!!, color = MaterialTheme.colorScheme.error) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.submitResetPassword(onSuccess = onPasswordResetSuccess)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FruitSenseColors.GreenDark),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(text = "Ubah Password", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}