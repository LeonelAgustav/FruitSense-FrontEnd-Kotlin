package com.example.fruitsense.data.repository

import android.util.Log
import com.example.fruitsense.data.UserPreferences
import com.example.fruitsense.data.api.ApiService
import com.example.fruitsense.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {
    // --- LOGIN ---
    suspend fun login(email: String, pass: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.login(LoginRequest(email, pass))

            // Ambil token dari session.accessToken
            val token = response.session?.accessToken

            if (!token.isNullOrEmpty()) {
                // Simpan token LOGIN ke DataStore (Key: user_token)
                userPreferences.saveToken(token)
                emit(Result.success(response))
            } else {
                emit(Result.failure(Exception(response.message ?: "Login gagal: Token tidak ditemukan")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // ... (Register, Verify Email, Forgot Password TETAP SAMA) ...
    suspend fun register(name: String, email: String, pass: String): Flow<Result<BasicResponse>> = flow {
        try {
            val response = apiService.register(RegisterRequest(name, email, pass))
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun verifyEmail(email: String, otp: String): Flow<Result<BasicResponse>> = flow {
        try {
            val response = apiService.verifyOtp(OtpRequest(email, otp))
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun forgotPassword(email: String): Flow<Result<BasicResponse>> = flow {
        try {
            val response = apiService.forgotPassword(ForgotPasswordRequest(email))
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // --- VERIFY RECOVERY (Simpan Token Khusus Reset) ---
    suspend fun verifyRecovery(email: String, otp: String): Flow<Result<BasicResponse>> = flow {
        try {
            val response = apiService.verifyRecovery(OtpRequest(email, otp))

            // Tangkap token dari session.accessToken (bukan .token)
            val token = response.session?.accessToken
            Log.d("AuthRepo", "VerifyRecovery Token: $token")

            if (!token.isNullOrEmpty()) {
                // [FIX] Simpan ke RESET_TOKEN_KEY, BUKAN TOKEN_KEY (Login)
                userPreferences.saveResetToken(token)

                emit(Result.success(BasicResponse(message = response.message, error = false)))
            } else {
                Log.w("AuthRepo", "VerifyRecovery sukses tapi token kosong!")
                emit(Result.success(BasicResponse(message = response.message, error = false)))
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "VerifyRecovery Error", e)
            emit(Result.failure(e))
        }
    }

    // --- RESET PASSWORD (Pakai Token Khusus Reset) ---
    suspend fun resetPassword(newPassword: String): Flow<Result<BasicResponse>> = flow {
        try {
            // [FIX] Ambil token dari RESET TOKEN key
            val resetToken = userPreferences.resetTokenFlow.first()
            Log.d("AuthRepo", "ResetPassword Token: $resetToken")

            if (resetToken.isNullOrEmpty()) {
                throw Exception("Sesi reset password habis. Ulangi proses lupa password.")
            }

            // [FIX] Kirim token reset secara MANUAL di header
            // Interceptor di AppModule harus cukup pintar untuk tidak menimpa header ini,
            // ATAU kita update ApiService agar menerima @Header("Authorization") yang akan override interceptor.
            val response = apiService.resetPassword("Bearer $resetToken", ResetPasswordRequest(newPassword))

            // Bersihkan token reset setelah dipakai
            userPreferences.clearResetToken()

            emit(Result.success(response))
        } catch (e: Exception) {
            Log.e("AuthRepo", "ResetPassword Error", e)
            emit(Result.failure(e))
        }
    }

    // ... (Logout, Profile, Update Profile TETAP SAMA) ...
    suspend fun logout() {
        try {
            apiService.logout()
        } catch (e: Exception) {
        } finally {
            userPreferences.clearToken()
        }
    }

    fun getUserProfile(): Flow<Result<UserProfile>> = flow {
        try {
            val response = apiService.getProfile()
            if (response.user != null) {
                emit(Result.success(response.user))
            } else {
                emit(Result.failure(Exception("Profil kosong")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getSessionToken(): Flow<String?> {
        return userPreferences.tokenFlow
    }

    suspend fun updateProfile(name: String, imageFile: java.io.File?): kotlinx.coroutines.flow.Flow<Result<com.example.fruitsense.data.model.BasicResponse>> = kotlinx.coroutines.flow.flow {
        try {
            val nameBody = if (name.isNotBlank())
                name.toRequestBody("text/plain".toMediaTypeOrNull())
            else null

            val imagePart = if (imageFile != null) {
                val requestImage = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                okhttp3.MultipartBody.Part.createFormData("avatar", imageFile.name, requestImage)
            } else null

            val response = apiService.updateProfile(nameBody, imagePart)

            if (response.error != true) {
                emit(Result.success(response))
            } else {
                emit(Result.failure(Exception(response.message)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}