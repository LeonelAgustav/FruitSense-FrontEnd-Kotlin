package com.example.fruitsense.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// --- RESPONSES ---

data class InventoryResponse(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val listFruit: List<FruitItem> = emptyList()
)

// Response Analisa AI (Scan)
data class AnalyzeResponse(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("analysis_result") val analysisResult: AnalysisResult?,
    @SerializedName("inventory") val savedInventory: FruitItem?
)

data class AnalysisResult(
    @SerializedName("detected") val detected: String,
    @SerializedName("grade") val grade: String,
    @SerializedName("nutrients") val nutrients: String,
    @SerializedName("days_left") val daysLeft: Int
)

data class RecipesResponse(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("recipes") val recipes: List<RecipeItem> = emptyList()
)

data class RecipeItem(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("ingredients") val ingredients: String,
    @SerializedName("instructions") val instructions: String,
    @SerializedName("cooking_time") val cookingTime: String
)

data class HistoryResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<FruitItem>
)

data class UpdateInventoryRequest(
    @SerializedName("stock_quantity") val stockQuantity: Int,
    @SerializedName("fruit_name") val fruitName: String
)


// --- ENTITY UTAMA ---

@Parcelize
data class FruitItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("fruit_name")
    val name: String,

    @SerializedName("image_url")
    val imageUri: String? = null,

    @SerializedName("created_at")
    val dateAdded: String? = null,

    @SerializedName("expiration_date")
    val expiryDate: String? = null,

    @SerializedName("expiration_days")
    val expiryDays: Int? = null,

    @SerializedName("grade")
    val grade: String? = "Unknown",

    @SerializedName("stock_quantity")
    val stock: Int? = null,

    @SerializedName("result_summary")
    val aiDescription: String = "",

    @SerializedName("storage_advice")
    val storageAdvice: String = "",

    @SerializedName("recipes")
    val recipes: List<String> = emptyList()
) : Parcelable