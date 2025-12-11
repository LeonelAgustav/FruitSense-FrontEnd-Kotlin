package com.fruitsense.app.ui.screen.auth.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fruitsense.app.R
import com.fruitsense.app.ui.screen.auth.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailVerificationScreen(
    isFromForgotPassword: Boolean,
    onVerificationSuccess: () -> Unit,
    onCancel: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val registerState by viewModel.registerState.collectAsState()
    val forgotState by viewModel.forgotPasswordState.collectAsState()
    val emailToVerify = if (isFromForgotPassword) forgotState.email else registerState.email

    var otpCode by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var timeLeft by remember { mutableIntStateOf(60) }
    var isTimerRunning by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = isTimerRunning) {
        if (isTimerRunning) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            isTimerRunning = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
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
                    modifier = Modifier.size(120.dp),
                    tint = Color.Unspecified
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Verifikasi Email",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Masukkan 6 digit kode yang telah kami kirimkan ke email Anda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- INPUT OTP Custom ---
                BasicTextField(
                    value = otpCode,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            otpCode = it
                            isError = false
                            errorMessage = ""
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    decorationBox = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            repeat(6) { index ->
                                val char = if (index < otpCode.length) otpCode[index].toString() else ""
                                val isFocused = index == otpCode.length

                                // Warna Border Logis
                                val borderColor = when {
                                    isError -> MaterialTheme.colorScheme.error
                                    isFocused || char.isNotEmpty() -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.outline
                                }

                                Box(
                                    modifier = Modifier
                                        .width(45.dp)
                                        .height(56.dp)
                                        .border(
                                            width = if (isFocused || char.isNotEmpty()) 2.dp else 1.dp,
                                            color = borderColor,
                                            shape = MaterialTheme.shapes.small // 12.dp rounded
                                        )
                                        .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char,
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                )

                if (isError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage.ifEmpty { "Kode verifikasi salah" },
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Timer & Resend
                if (timeLeft > 0) {
                    Text(
                        text = "Kirim ulang kode dalam 00:${timeLeft.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    TextButton(
                        onClick = {
                            timeLeft = 60
                            isTimerRunning = true
                            // Logic resend code bisa dipanggil disini jika ada di VM
                        }
                    ) {
                        Text(
                            "Kirim Ulang Kode",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Button Verify
                Button(
                    onClick = {
                        if (otpCode.length < 6) {
                            isError = true
                            errorMessage = "Masukkan 6 digit kode"
                        } else {
                            isLoading = true
                            viewModel.verifyEmail(
                                email = emailToVerify,
                                code = otpCode,
                                isRecovery = isFromForgotPassword,
                                onSuccess = {
                                    isLoading = false
                                    onVerificationSuccess()
                                },
                                onError = { msg ->
                                    isLoading = false
                                    isError = true
                                    errorMessage = msg
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Verifikasi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}