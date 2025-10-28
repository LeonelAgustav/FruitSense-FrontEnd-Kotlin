package com.example.fruitsense.ui.screen.auth

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
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Masuk",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = FruitSenseColors.GreenDark // Hijau Tua
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = FruitSenseColors.GrayDark) }, // Abu-abu Gelap
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Email",
                                tint = FruitSenseColors.BrownDark // Cokelat Gelap
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FruitSenseColors.GreenDark, // Hijau Tua
                            unfocusedBorderColor = FruitSenseColors.GrayDark, // Abu-abu Gelap
                            focusedLabelColor = FruitSenseColors.GreenDark // Hijau Tua
                        ),
                        textStyle = TextStyle(color = FruitSenseColors.GrayDark) // Abu-abu Gelap
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = FruitSenseColors.GrayDark) }, // Abu-abu Gelap
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = FruitSenseColors.BrownDark // Cokelat Gelap
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (passwordVisible) "Hide" else "Show",
                                    tint = FruitSenseColors.BrownDark // Cokelat Gelap
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FruitSenseColors.GreenDark, // Hijau Tua
                            unfocusedBorderColor = FruitSenseColors.GrayDark, // Abu-abu Gelap
                            focusedLabelColor = FruitSenseColors.GreenDark // Hijau Tua
                        ),
                        textStyle = TextStyle(color = FruitSenseColors.GrayDark) // Abu-abu Gelap
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (email == "admin@gmail.com" && password == "admin") {
                                onLoginSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FruitSenseColors.GreenDark // Hijau Tua
                        )
                    ) {
                        Text(
                            text = "Masuk",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Belum punya akun? ",
                            color = FruitSenseColors.GrayDark,
                            fontSize = 16.sp
                        )
                        TextButton(onClick = onNavigateToRegister) {
                            Text(
                                text = "Daftar",
                                color = FruitSenseColors.GreenOlive, // Hijau Zaitun
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}