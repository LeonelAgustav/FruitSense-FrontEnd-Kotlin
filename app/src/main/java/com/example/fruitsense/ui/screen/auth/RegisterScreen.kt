package com.example.fruitsense.ui.screen.auth

import com.example.fruitsense.ui.theme.FruitSenseColors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fruitsense.R

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                colors = CardDefaults.cardColors(
                    containerColor = FruitSenseColors.White // Menggunakan warna dari object
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Daftar",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = FruitSenseColors.GreenDark // Menggunakan warna dari object
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nama Lengkap", color = FruitSenseColors.GrayDark) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Name",
                                tint = FruitSenseColors.BrownDark // Menggunakan warna dari object
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FruitSenseColors.GreenDark, // Menggunakan warna dari object
                            unfocusedBorderColor = FruitSenseColors.GrayDark,
                            focusedLabelColor = FruitSenseColors.GreenDark
                        ),
                        textStyle = TextStyle(color = FruitSenseColors.GrayDark)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = FruitSenseColors.GrayDark) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Email",
                                tint = FruitSenseColors.BrownDark // Menggunakan warna dari object
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FruitSenseColors.GreenDark, // Menggunakan warna dari object
                            unfocusedBorderColor = FruitSenseColors.GrayDark,
                            focusedLabelColor = FruitSenseColors.GreenDark
                        ),
                        textStyle = TextStyle(color = FruitSenseColors.GrayDark)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = FruitSenseColors.GrayDark) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = FruitSenseColors.BrownDark // Menggunakan warna dari object
                            )
                        },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null, tint = FruitSenseColors.BrownDark)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FruitSenseColors.GreenDark, // Menggunakan warna dari object
                            unfocusedBorderColor = FruitSenseColors.GrayDark,
                            focusedLabelColor = FruitSenseColors.GreenDark
                        ),
                        textStyle = TextStyle(color = FruitSenseColors.GrayDark)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Konfirmasi Password", color = FruitSenseColors.GrayDark) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Confirm Password",
                                tint = FruitSenseColors.BrownDark // Menggunakan warna dari object
                            )
                        },
                        trailingIcon = {
                            val image = if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(imageVector = image, contentDescription = null, tint = FruitSenseColors.BrownDark)
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FruitSenseColors.GreenDark, // Menggunakan warna dari object
                            unfocusedBorderColor = FruitSenseColors.GrayDark,
                            focusedLabelColor = FruitSenseColors.GreenDark
                        ),
                        textStyle = TextStyle(color = FruitSenseColors.GrayDark)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (password == confirmPassword && fullName.isNotBlank() && email.isNotBlank()) {
                                onRegisterSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FruitSenseColors.GreenDark // Menggunakan warna dari object
                        )
                    ) {
                        Text(
                            text = "Daftar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FruitSenseColors.White // Menggunakan warna dari object
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sudah punya akun? ",
                            color = FruitSenseColors.GrayDark // Menggunakan warna dari object
                        )
                        TextButton(onClick = onNavigateToLogin) {
                            Text(
                                text = "Masuk",
                                color = FruitSenseColors.GreenOlive, // Menggunakan warna dari object
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}