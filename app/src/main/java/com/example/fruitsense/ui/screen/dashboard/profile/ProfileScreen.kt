package com.example.fruitsense.ui.screen.dashboard.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.fruitsense.ui.theme.FruitSenseColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()
    val updateStatus by viewModel.updateStatus.collectAsState()
    val selectedTheme by viewModel.theme.collectAsState()

    var pushEnabled by remember { mutableStateOf(true) }

    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }

    // Toast Handler
    LaunchedEffect(updateStatus) {
        updateStatus?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            if (it.contains("berhasil", ignoreCase = true)) showEditDialog = false
            viewModel.clearUpdateStatus()
        }
    }

    // Edit Dialog
    if (showEditDialog && profileState is ProfileUiState.Success) {
        val user = (profileState as ProfileUiState.Success).user
        EditProfileDialog(
            initialName = user.name ?: "",
            initialImage = user.avatarUrl,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, uri -> viewModel.updateProfile(name, uri) },
            isLoading = isUpdating
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profil & Pengaturan", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- SECTION 1: HEADER PROFIL ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val state = profileState) {
                    is ProfileUiState.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    is ProfileUiState.Error -> Text("Gagal memuat profil", color = MaterialTheme.colorScheme.error)
                    is ProfileUiState.Success -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Avatar dengan Badge Edit
                            Box {
                                Card(
                                    shape = CircleShape,
                                    modifier = Modifier.size(110.dp),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    if (!state.user.avatarUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = state.user.avatarUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Box(
                                            Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Person, null, modifier = Modifier.size(50.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                                IconButton(
                                    onClick = { showEditDialog = true },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .offset(x = 4.dp, y = 4.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                                        .size(32.dp)
                                ) {
                                    Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(state.user.name ?: "Pengguna", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(state.user.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Divider Pemisah
            HorizontalDivider(thickness = 4.dp, color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f))

            Column(modifier = Modifier.padding(24.dp)) {

                // --- SECTION 2: TAMPILAN (THEME) ---
                SectionHeader("Tampilan Aplikasi")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ThemeOptionCard(
                        title = "Terang", icon = Icons.Outlined.LightMode,
                        isSelected = selectedTheme == "LIGHT",
                        onClick = { viewModel.updateTheme("LIGHT") }, modifier = Modifier.weight(1f)
                    )
                    ThemeOptionCard(
                        title = "Gelap", icon = Icons.Outlined.DarkMode,
                        isSelected = selectedTheme == "DARK",
                        onClick = { viewModel.updateTheme("DARK") }, modifier = Modifier.weight(1f)
                    )
                    ThemeOptionCard(
                        title = "Sistem", icon = Icons.Outlined.SettingsSystemDaydream,
                        isSelected = selectedTheme == "SYSTEM",
                        onClick = { viewModel.updateTheme("SYSTEM") }, modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- SECTION 3: NOTIFIKASI ---
                SectionHeader("Preferensi Notifikasi")
                Card(
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.5f))
                ) {
                    SwitchItem("Push Notification", "Info status buah terkini", pushEnabled) { pushEnabled = it }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // --- SECTION 4: LOGOUT ---
                Button(
                    onClick = { viewModel.logout(onLogout) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Icon(Icons.Default.Logout, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keluar dari Akun", fontWeight = FontWeight.Bold)
                }

                // Version Info
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Versi Aplikasi 1.0.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun ThemeOptionCard(title: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    val border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

    Card(
        onClick = onClick,
        modifier = modifier.height(85.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = border
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = contentColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = contentColor)
        }
    }
}

@Composable
fun SwitchItem(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = FruitSenseColors.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

// Dialog Edit (Sama seperti sebelumnya)
@Composable
fun EditProfileDialog(initialName: String, initialImage: String?, onDismiss: () -> Unit, onConfirm: (String, Uri?) -> Unit, isLoading: Boolean) {
    var name by remember { mutableStateOf(initialName) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> if(uri!=null) selectedImageUri = uri }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profil") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.size(90.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant).clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) AsyncImage(model = selectedImageUri, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    else if (initialImage != null) AsyncImage(model = initialImage, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    else Icon(Icons.Default.AddAPhoto, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, selectedImageUri) }, enabled = !isLoading, shape = MaterialTheme.shapes.extraLarge) {
                if(isLoading) CircularProgressIndicator(modifier=Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary) else Text("Simpan")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}