package com.fruitsense.app.ui.screen.dashboard.scan

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fruitsense.app.data.model.FruitItem
import com.fruitsense.app.data.repository.FruitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

// State untuk UI Scan
sealed class ScanUiState {
    object Idle : ScanUiState()
    object Loading : ScanUiState()
    data class Success(val fruitItem: FruitItem) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repository: FruitRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    // Fungsi Utama: Analisa Gambar
    fun analyzeImage(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = ScanUiState.Loading

            try {
                // Pindahkan operasi berat (kompresi) ke Background Thread (IO)
                val compressedFile = withContext(Dispatchers.IO) {
                    val file = uriToFile(uri, context)
                    reduceFileImage(file)
                }

                // Upload ke API
                repository.uploadAndAnalyze(compressedFile).collect { result ->
                    result.onSuccess { fruit ->
                        _uiState.value = ScanUiState.Success(fruit)
                    }.onFailure { error ->
                        // Berikan pesan error yang lebih spesifik
                        val msg = if (error.message?.contains("timeout", true) == true) {
                            "Koneksi lambat (Time Out). Coba lagi."
                        } else {
                            error.message ?: "Gagal menganalisa gambar"
                        }
                        _uiState.value = ScanUiState.Error(msg)
                    }
                }

            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error("Gagal memproses gambar: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = ScanUiState.Idle
    }

    // Helper: Mengubah Uri ke File
    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val myFile = File.createTempFile("scan_temp", ".jpg", context.cacheDir)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    // [FIXED] Helper: Kompresi Gambar Lebih Efisien (Resize + Compress)
    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int

        // [BARU] Resize dulu jika resolusi terlalu besar (misal lebar > 1024px)
        // Ini mempercepat proses kompresi drastis
        val maxDim = 1024
        var scaledBitmap = bitmap
        if (bitmap.width > maxDim || bitmap.height > maxDim) {
            val ratio = Math.min(maxDim.toFloat() / bitmap.width, maxDim.toFloat() / bitmap.height)
            val width = (bitmap.width * ratio).toInt()
            val height = (bitmap.height * ratio).toInt()
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        }

        do {
            val bmpStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000 && compressQuality > 5) // Target < 1MB

        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }
}