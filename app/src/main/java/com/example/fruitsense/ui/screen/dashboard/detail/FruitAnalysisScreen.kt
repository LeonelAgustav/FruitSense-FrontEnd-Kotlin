package com.example.fruitsense.ui.screen.dashboard.detail

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.fruitsense.data.model.FruitItem
import com.example.fruitsense.ui.theme.FruitSenseColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FruitAnalysisScreen(
    fruitItem: FruitItem,
    onBackClick: () -> Unit,
    autoGenerate: Boolean = false,
    // [FIX] Explicitly specify ViewModel type if inference fails
    recipesViewModel: RecipesViewModel = hiltViewModel<RecipesViewModel>()
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // [FIX] Explicitly specify type for collectAsState to help compiler inference
    val generatedRecipes: List<String> by recipesViewModel.generationState.collectAsState()
    val isGenerating: Boolean by recipesViewModel.isGenerating.collectAsState()
    val generateError: String? by recipesViewModel.generateError.collectAsState()

    // If hasGenerated is missing in ViewModel, this line causes error.
    // Ensure RecipesViewModel has this field. If not, remove it or update VM.
    // Assuming you updated VM:
    val hasGenerated: Boolean by recipesViewModel.hasGenerated.collectAsState()

    val safeItemRecipes = (fruitItem.recipes as? List<String>) ?: emptyList()
    val displayRecipes = if (generatedRecipes.isNotEmpty()) generatedRecipes else safeItemRecipes

    LaunchedEffect(Unit) {
        if (autoGenerate) {
            if (!fruitItem.id.isNullOrEmpty() && fruitItem.id != "temp_id") {
                recipesViewModel.generateRecommendations(fruitItem.id)
            } else {
                Toast.makeText(context, "Tidak bisa generate untuk item yang belum disimpan.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(generateError) {
        generateError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            recipesViewModel.clearGenerationState()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Analisa AI", fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            // Header Image & Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(80.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    if (fruitItem.imageUri.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(FruitSenseColors.GreenDark.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, null, tint = FruitSenseColors.GreenDark)
                        }
                    } else {
                        AsyncImage(
                            model = fruitItem.imageUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = fruitItem.name ?: "Tanpa Nama",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Grade ${fruitItem.grade ?: "-"} • ${fruitItem.freshness}% Segar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Resep
            SectionTitle("Ide Olahan Resep")

            if (isGenerating) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = FruitSenseColors.GreenDark)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Sedang mencari resep...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else if (displayRecipes.isNotEmpty()) {
                displayRecipes.forEach { recipe ->
                    RecipeCard(recipeName = recipe)
                }

                if (generatedRecipes.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            if (!fruitItem.id.isNullOrEmpty() && fruitItem.id != "temp_id") {
                                recipesViewModel.generateRecommendations(fruitItem.id)
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Cari Resep Lain", color = FruitSenseColors.GreenOlive)
                    }
                }
            } else if (hasGenerated) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Maaf, AI tidak menemukan resep untuk buah ini.", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { recipesViewModel.generateRecommendations(fruitItem.id) }) {
                            Text("Coba Lagi", color = FruitSenseColors.GreenOlive)
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Belum ada rekomendasi resep.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Button(
                        onClick = {
                            if (!fruitItem.id.isNullOrEmpty() && fruitItem.id != "temp_id") {
                                recipesViewModel.generateRecommendations(fruitItem.id)
                            } else {
                                Toast.makeText(context, "Simpan buah ke inventory dulu untuk generate resep.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FruitSenseColors.GreenDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.RestaurantMenu, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buat Resep dengan AI")
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = FruitSenseColors.GreenDark,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun InfoCard(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun RecipeCard(recipeName: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = FruitSenseColors.GreenDark),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.RestaurantMenu, null, tint = Color.White)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = recipeName, style = MaterialTheme.typography.bodyLarge, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}