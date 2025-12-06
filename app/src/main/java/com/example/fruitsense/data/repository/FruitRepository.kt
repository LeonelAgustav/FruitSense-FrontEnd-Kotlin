package com.example.fruitsense.data.repository

import android.util.Log
import com.example.fruitsense.data.UserPreferences
import com.example.fruitsense.data.api.ApiService
import com.example.fruitsense.data.model.BasicResponse
import com.example.fruitsense.data.model.FruitItem
import com.example.fruitsense.data.model.RecipeItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class FruitRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    private suspend fun getToken(): String {
        return userPreferences.tokenFlow.first() ?: ""
    }

    private fun parseErrorMessage(e: Throwable): String {
        return when (e) {
            is HttpException -> {
                when (e.code()) {
                    401 -> "Sesi telah berakhir. Silakan login kembali."
                    403 -> "Akses ditolak."
                    404 -> "Data tidak ditemukan."
                    500 -> "Server sedang bermasalah (Error 500)."
                    else -> "Terjadi kesalahan: ${e.message()}"
                }
            }
            is IOException -> "Tidak ada koneksi internet."
            else -> e.message ?: "Terjadi kesalahan."
        }
    }

    // 1. Get Inventory
    fun getInventory(search: String? = null, sortBy: String? = null): Flow<Result<List<FruitItem>>> = flow {
        try {
            val token = getToken()
            if (token.isEmpty()) throw Exception("Sesi habis")

            // Pass parameter search & sortBy ke API
            val fruitList = apiService.getInventory(search, sortBy)
            emit(Result.success(fruitList))
        } catch (e: Exception) {
            emit(Result.failure(Exception(parseErrorMessage(e))))
        }
    }

    // 2. Get History
    fun getHistory(): Flow<Result<List<FruitItem>>> = flow {
        try {
            val token = getToken()
            if (token.isEmpty()) throw Exception("Sesi habis")
            val response = apiService.getHistory()
            emit(Result.success(response.data))
        } catch (e: Exception) {
            emit(Result.failure(Exception(parseErrorMessage(e))))
        }
    }

    // 3. Upload & Analyze
    fun uploadAndAnalyze(imageFile: File): Flow<Result<FruitItem>> = flow {
        try {
            val token = getToken()
            if (token.isEmpty()) throw Exception("Sesi habis")

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("image", imageFile.name, requestImageFile)
            val stockBody = "1".toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.analyzeFruit(file = multipartBody, stock = stockBody)

            if (!response.error && response.analysisResult != null) {
                val res = response.analysisResult
                val result = FruitItem(
                    id = response.savedInventory?.id ?: "temp",
                    name = res.detected,
                    grade = res.grade,
                    freshness = 85,
                    aiDescription = "Nutrisi: ${res.nutrients}",
                    storageAdvice = "Sisa waktu: ${res.daysLeft} hari",
                    imageUri = imageFile.path
                )
                emit(Result.success(result))
            } else {
                emit(Result.failure(Exception(response.message)))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(parseErrorMessage(e))))
        }
    }

    // 4. Generate Recipes
    fun generateRecipes(inventoryId: String): Flow<Result<List<String>>> = flow {
        try {
            val body = mapOf("inventory_id" to inventoryId)
            val response = apiService.generateRecipe(body)
            if (!response.error) {
                val recipeTitles = response.recipes.map { it.title }
                emit(Result.success(recipeTitles))
            } else {
                emit(Result.failure(Exception(response.message)))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(parseErrorMessage(e))))
        }
    }

    // 5. Delete Inventory (FIXED: Handle 204 No Content)
    fun deleteInventory(id: String): Flow<Result<BasicResponse>> = flow {
        try {
            val token = getToken()
            if (token.isEmpty()) throw Exception("Sesi habis")

            Log.d("FruitRepo", "Menghapus Inventory ID: $id")

            // Panggil API yang sekarang return Response<Unit>
            val response = apiService.deleteInventory(id)

            if (response.isSuccessful) {
                // Sukses (200 atau 204) - Kita buat BasicResponse manual karena body kosong
                emit(Result.success(BasicResponse(message = "Item sudah dihapus", error = false)))
            } else {
                // Gagal (4xx atau 5xx)
                // Jika 404, kita anggap sukses saja (barang sudah tidak ada)
                if (response.code() == 404) {
                    emit(Result.success(BasicResponse(message = "Item sudah dihapus", error = false)))
                } else {
                    emit(Result.failure(Exception("Gagal menghapus: ${response.message()}")))
                }
            }

        } catch (e: Exception) {
            Log.e("FruitRepo", "Gagal Hapus Inventory: ${e.message}", e)

            if (e is HttpException && e.code() == 404) {
                emit(Result.success(BasicResponse(message = "Item sudah dihapus", error = false)))
            } else {
                emit(Result.failure(Exception(parseErrorMessage(e))))
            }
        }
    }

    // 6. Delete History (FIXED: Handle 204 No Content)
    fun deleteHistory(id: String): Flow<Result<BasicResponse>> = flow {
        try {
            val token = getToken()
            if (token.isEmpty()) throw Exception("Sesi habis")

            Log.d("FruitRepo", "Menghapus History ID: $id")

            val response = apiService.deleteHistory(id)

            if (response.isSuccessful) {
                emit(Result.success(BasicResponse(message = "Riwayat sudah dihapus", error = false)))
            } else {
                if (response.code() == 404) {
                    emit(Result.success(BasicResponse(message = "Riwayat sudah dihapus", error = false)))
                } else {
                    emit(Result.failure(Exception("Gagal menghapus: ${response.message()}")))
                }
            }

        } catch (e: Exception) {
            Log.e("FruitRepo", "Gagal Hapus History: ${e.message}", e)

            if (e is HttpException && e.code() == 404) {
                emit(Result.success(BasicResponse(message = "Riwayat sudah dihapus", error = false)))
            } else {
                emit(Result.failure(Exception(parseErrorMessage(e))))
            }
        }
    }

    // 7. Get All Recipes
    fun getRecipesList(): Flow<Result<List<RecipeItem>>> = flow {
        try {
            val token = getToken()
            if (token.isEmpty()) throw Exception("Sesi habis")
            val recipes = apiService.getRecipes()
            emit(Result.success(recipes))
        } catch (e: Exception) {
            emit(Result.failure(Exception(parseErrorMessage(e))))
        }
    }

    // 8. Delete Recipe [BARU]
    fun deleteRecipe(id: String): Flow<Result<BasicResponse>> = flow {
        try {
            val token = getToken()
            if (token.isEmpty()) throw Exception("Sesi habis")
            val response = apiService.deleteRecipe(id)
            // Dokumentasi bilang return 200 dengan JSON BasicResponse
            if (response.error != true) {
                emit(Result.success(response))
            } else {
                emit(Result.failure(Exception(response.message)))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(parseErrorMessage(e))))
        }
    }
}