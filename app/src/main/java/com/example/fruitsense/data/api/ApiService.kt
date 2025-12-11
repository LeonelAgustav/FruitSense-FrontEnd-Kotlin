package com.example.fruitsense.data.api

import com.example.fruitsense.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import retrofit2.Response

interface ApiService {

    // --- 1. AUTENTIKASI ---
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): BasicResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpRequest): BasicResponse

    @POST("auth/logout")
    suspend fun logout(): BasicResponse

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): BasicResponse

    @POST("auth/verify-recovery")
    suspend fun verifyRecovery(@Body request: OtpRequest): LoginResponse

    @PUT("auth/reset-password")
    suspend fun resetPassword(
        @Header("Authorization") token: String, // Token Reset
        @Body request: ResetPasswordRequest
    ): BasicResponse

    // --- 2. ANALISIS AI ---
    @Multipart
    @POST("analyze")
    suspend fun analyzeFruit(
        @Part file: MultipartBody.Part, // key: "image"
        @Part("stock_quantity") stock: RequestBody
    ): AnalyzeResponse

    // --- 3. INVENTORY ---
    @GET("inventory")
    suspend fun getInventory(
        @Query("search") search: String? = null,
        @Query("sortBy") sortBy: String? = null
    ): List<FruitItem>

    @Multipart
    @POST("inventory")
    suspend fun addInventoryManual(
        @Part file: MultipartBody.Part,
        @Part("fruit_name") name: RequestBody,
        @Part("stock_quantity") stock: RequestBody,
        @Part("grade") grade: RequestBody? = null
    ): BasicResponse

    @PUT("inventory/{id}")
    suspend fun updateInventory(
        @Path("id") id: String,
        @Body body: UpdateInventoryRequest
    ): BasicResponse

    @DELETE("inventory/{id}")
    suspend fun deleteInventory(@Path("id") id: String): Response<Unit>

    // --- 4. RECIPES ---
    @POST("recipes/generate")
    suspend fun generateRecipe(@Body inventoryIdMap: Map<String, String>): RecipesResponse // Body: { "inventory_id": "..." }

    @GET("recipes")
    suspend fun getRecipes(): List<RecipeItem>

    @DELETE("recipes/{id}")
    suspend fun deleteRecipe(@Path("id") id: String): BasicResponse

    // --- 5. HISTORY ---
    @GET("history")
    suspend fun getHistory(): HistoryResponse

    @DELETE("history/{id}")
    suspend fun deleteHistory(@Path("id") id: String): Response<Unit>

    // --- 6. PROFIL USER ---
    @GET("user-profile")
    suspend fun getProfile(): ProfileResponse

    @Multipart
    @PUT("user-profile")
    suspend fun updateProfile(
        @Part("name") name: RequestBody?,
        @Part avatar: MultipartBody.Part?
    ): BasicResponse

    // --- 7. NOTIFIKASI ---
    @PUT("fcm/token")
    suspend fun updateFcmToken(@Body request: FcmTokenRequest): BasicResponse
}