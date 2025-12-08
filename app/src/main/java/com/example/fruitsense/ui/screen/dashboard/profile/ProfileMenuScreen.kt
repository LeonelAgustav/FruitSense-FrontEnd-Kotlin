package com.example.fruitsense.ui.screen.dashboard.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.fruitsense.ui.theme.FruitSenseColors

@Composable
fun ProfileMenuScreen(
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()
    val updateStatus by viewModel.updateStatus.collectAsState()

    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }

    // Handle Toast status update
    LaunchedEffect(updateStatus) {
        updateStatus?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            if (it.contains("berhasil")) showEditDialog = false
            viewModel.clearUpdateStatus()
        }
    }

    if (showEditDialog && profileState is ProfileUiState.Success) {
        val currentUser = (profileState as ProfileUiState.Success).user
        EditProfileDialog(
            initialName = currentUser.name ?: "",
            initialImage = currentUser.avatarUrl,
            onDismiss = { showEditDialog = false },
            onConfirm = { newName, newImageUri ->
                viewModel.updateProfile(newName, newImageUri)
            },
            isLoading = isUpdating
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Profile Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                text = "Profil Saya",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Kelola informasi akun dan pengaturan aplikasi",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

        // Konten di bawah Divider
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 2. Account Information Section (DYNAMIC DATA)
            when (val state = profileState) {
                is ProfileUiState.Loading -> {
                    Box(modifier = Modifier.height(150.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = FruitSenseColors.GreenDark)
                    }
                }
                is ProfileUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Gagal memuat profil", color = MaterialTheme.colorScheme.error)
                        TextButton(onClick = { viewModel.loadProfile() }) { Text("Coba Lagi") }
                    }
                }
                is ProfileUiState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Foto Profile & Tombol Edit
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!state.user.avatarUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = state.user.avatarUrl,
                                        contentDescription = "Avatar",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier.size(60.dp),
                                        tint = FruitSenseColors.GreenOlive.copy(alpha = 0.6f)
                                    )
                                }
                            }
                            // Tombol Edit Kecil
                            IconButton(
                                onClick = { showEditDialog = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(FruitSenseColors.GreenDark, CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = state.user.name ?: "Pengguna",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = state.user.email,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Settings Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
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

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    ProfileMenuItem(
                        icon = Icons.Default.Settings,
                        title = "Pengaturan Aplikasi",
                        subtitle = "Bahasa, tema, dan lainnya",
                        onClick = { onSettingsClick() },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Logout Button
            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FruitSenseColors.GreenDark,
                    contentColor = FruitSenseColors.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Keluar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    initialName: String,
    initialImage: String?,
    onDismiss: () -> Unit,
    onConfirm: (String, Uri?) -> Unit,
    isLoading: Boolean
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profil") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Image Picker
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(model = selectedImageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else if (initialImage != null) {
                        AsyncImage(model = initialImage, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, selectedImageUri) },
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                else Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
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
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FruitSenseColors.GreenDark.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = FruitSenseColors.GreenDark, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Navigate", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(24.dp))
    }
}