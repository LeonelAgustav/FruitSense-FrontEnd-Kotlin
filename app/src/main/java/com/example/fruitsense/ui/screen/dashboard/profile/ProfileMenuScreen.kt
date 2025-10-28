package com.example.fruitsense.ui.screen.dashboard.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun ProfileMenuScreen(
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "Profil Saya",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = FruitSenseColors.GreenOlive
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Kelola informasi akun dan pengaturan aplikasi",
                fontSize = 14.sp,
                color = FruitSenseColors.GrayDark.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Account Information Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto Profile
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(60.dp),
                    tint = FruitSenseColors.GreenOlive.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nama
            Text(
                text = "John Doe",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email
            Text(
                text = "johndoe@email.com",
                fontSize = 20.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Settings Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Pengaturan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = FruitSenseColors.GreenOlive,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                ProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifikasi",
                    subtitle = "Atur preferensi notifikasi",
                    onClick = { onNotificationClick() },
                )

                Divider(color = Color.LightGray.copy(alpha = 0.3f))

                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Pengaturan Aplikasi",
                    subtitle = "Bahasa, tema, dan lainnya",
                    onClick = { onSettingsClick() },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FruitSenseColors.GreenDark,
                contentColor = FruitSenseColors.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Logout",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Keluar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FruitSenseColors.GreenDark.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = FruitSenseColors.GreenDark,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FruitSenseColors.Black
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = FruitSenseColors.Black
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = FruitSenseColors.GrayDark.copy(alpha = 0.4f),
            modifier = Modifier.size(24.dp)
        )
    }
}