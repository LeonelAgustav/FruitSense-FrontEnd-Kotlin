package com.fruitsense.app.data.model

import com.google.gson.annotations.SerializedName

data class ScanResponse(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val result: FruitItem?
)