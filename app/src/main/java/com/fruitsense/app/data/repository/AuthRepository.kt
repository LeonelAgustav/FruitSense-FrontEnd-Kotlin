package com.fruitsense.app.data.repository

import android.util.Log
import com.fruitsense.app.data.*
import com.fruitsense.app.data.api.ApiService
import com.google.firebase.messaging.FirebaseMessaging
import com.fruitsense.app.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.*
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
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

    // --- RESET PASSWORD
    suspend fun resetPassword(newPassword: String): Flow<Result<BasicResponse>> = flow {
        try {
            val resetToken = userPreferences.resetTokenFlow.first()
            Log.d("AuthRepo", "ResetPassword Token: $resetToken")

            if (resetToken.isNullOrEmpty()) {
                throw Exception("Sesi reset password habis. Ulangi proses lupa password.")
            }

            val response = apiService.resetPassword("Bearer $resetToken", ResetPasswordRequest(newPassword))

            userPreferences.clearResetToken()

            emit(Result.success(response))
        } catch (e: Exception) {
            Log.e("AuthRepo", "ResetPassword Error", e)
            emit(Result.failure(e))
        }
    }

    // --- LOGOUT ---
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

    private suspend fun getToken(): String {
        return userPreferences.tokenFlow.first() ?: ""
    }

    suspend fun updateFcmToken(): Flow<Result<BasicResponse>> = flow {
        try {
            val token = getToken()
            if (token.isEmpty()) return@flow // Jangan kirim jika belum login

            // 1. Ambil Token FCM dari Firebase
            val fcmToken = try {
                FirebaseMessaging.getInstance().token.await()
            } catch (e: Exception) {
                Log.e("AuthRepo", "Gagal dapat FCM Token", e)
                throw e
            }

            Log.d("AuthRepo", "FCM Token: $fcmToken")

            // 2. Kirim ke Backend
            val request = FcmTokenRequest(fcmToken = fcmToken)
            val response = apiService.updateFcmToken(request)

            if (!response.error) {
                emit(Result.success(response))
            } else {
                emit(Result.failure(Exception(response.message)))
            }

        } catch (e: Exception) {
            // Error FCM atau API tidak perlu menghentikan flow aplikasi utama, cukup log saja
            Log.e("AuthRepo", "Gagal update FCM Token ke server", e)
            emit(Result.failure(e))
        }
    }

    suspend fun updateProfile(name: String, imageFile: File?): Flow<Result<BasicResponse>> = flow {
        try {
            // 1. Persiapan Data Nama
            val nameBody = if (name.isNotBlank())
                name.toRequestBody("text/plain".toMediaTypeOrNull())
            else null

            // 2. Persiapan Data Gambar (Avatar)
            val imagePart = if (imageFile != null) {
                val requestImage = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                // Parameter "avatar" harus sesuai dengan yang diminta backend (cek ApiService/Postman)
                MultipartBody.Part.createFormData("avatar", imageFile.name, requestImage)
            } else null

            // 3. Panggil API
            // Pastikan ApiService.updateProfile menerima (RequestBody?, MultipartBody.Part?)
            val response = apiService.updateProfile(nameBody, imagePart)

            // 4. Handle Response
            if (response.error != true) {
                emit(Result.success(response))
            } else {
                emit(Result.failure(Exception(response.message)))
            }
        } catch (e: Exception) {
            // 5. Handle Error Jaringan/Lainnya
            emit(Result.failure(e))
        }
    }
}