package com.fruitsense.app.data.model

import com.google.gson.annotations.SerializedName

// --- REQUESTS ---

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

// Khusus verifikasi email & recovery
data class OtpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val otp: String
)

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String
)

data class ResetPasswordRequest(
    @SerializedName("new_password") val newPassword: String
)

data class FcmTokenRequest(
    @SerializedName("fcm_token") val fcmToken: String
)

// --- RESPONSES ---

// Response dasar (hanya status)
data class BasicResponse(
    @SerializedName("error") val error: Boolean, // Asumsi server kirim flag error (true/false)
    @SerializedName("message") val message: String
)

// [BARU] Response Verifikasi (mengandung token untuk reset)
data class VerifyResponse(
    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String?, // Token Reset ada di sini
    @SerializedName("error") val error: Boolean? = false
)

// [FIX] Response Login Sesuai JSON Asli
data class LoginResponse(
    @SerializedName("message") val message: String,
    @SerializedName("session") val session: SessionData?, // Token ada di sini
    @SerializedName("user") val user: UserProfile?
)

// [FIX] Wrapper untuk Session
data class SessionData(
    @SerializedName("access_token") val accessToken: String, // INI TOKENNYA
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("token_type") val tokenType: String
)

// Response Profil User
data class ProfileResponse(
    @SerializedName("user") val user: UserProfile?
)

data class UserProfile(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String?, // Bisa null dari meta data
    @SerializedName("avatar_url") val avatarUrl: String? = null
)