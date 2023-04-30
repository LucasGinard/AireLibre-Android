package com.lucasginard.airelibre.modules.home.model

import com.google.gson.annotations.SerializedName

data class SensorResponse(
    @SerializedName("sensor") var sensor: String,
    @SerializedName("source") var source: String,
    @SerializedName("description") var description: String,
    @SerializedName("longitude") var longitude: Double,
    @SerializedName("latitude") var latitude: Double,
    @SerializedName("quality") var quality: Quality,
    var distance: Float?=null,
    var id:Int?=null
)

data class Quality (
    @SerializedName("category") var category: String,
    @SerializedName("index") var index: Int
)