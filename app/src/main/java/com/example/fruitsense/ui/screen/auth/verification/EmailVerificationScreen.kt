package com.example.fruitsense.ui.screen.auth.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fruitsense.R
import com.example.fruitsense.ui.screen.auth.AuthViewModel
import com.example.fruitsense.ui.theme.FruitSenseColors
import kotlinx.coroutines.delay

@Composable
fun EmailVerificationScreen(
    isFromForgotPassword: Boolean, // Parameter untuk tahu konteks
    onVerificationSuccess: () -> Unit,
    onCancel: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // 1. Ambil State dari ViewModel (Shared)
    val registerState by viewModel.registerState.collectAsState()
    val forgotState by viewModel.forgotPasswordState.collectAsState()

    val emailToVerify = if (isFromForgotPassword) forgotState.email else registerState.email

    // State Lokal UI
    var otpCode by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Timer
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tombol Back
        IconButton(
            onClick = onCancel,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

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
                modifier = Modifier.size(150.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Verifikasi Email",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = FruitSenseColors.GreenDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Masukkan 6 digit kode yang telah kami kirimkan ke email Anda.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- INPUT OTP ---
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
                            Box(
                                modifier = Modifier
                                    .width(45.dp)
                                    .height(56.dp)
                                    .border(
                                        width = if (index == otpCode.length) 2.dp else 1.dp,
                                        color = if (isError) MaterialTheme.colorScheme.error else if (index == otpCode.length) FruitSenseColors.GreenDark else MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char,
                                    style = MaterialTheme.typography.titleLarge,
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
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Timer
            if (timeLeft > 0) {
                Text(
                    text = "Kirim ulang kode dalam 00:${timeLeft.toString().padStart(2, '0')}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            } else {
                TextButton(
                    onClick = {
                        timeLeft = 60
                        isTimerRunning = true
                        // TODO: Panggil resend code
                    }
                ) {
                    Text("Kirim Ulang Kode", color = FruitSenseColors.GreenOlive, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL VERIFIKASI ---
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
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FruitSenseColors.GreenDark),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Verifikasi", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}