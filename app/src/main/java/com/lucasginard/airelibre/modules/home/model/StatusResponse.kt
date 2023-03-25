package com.lucasginard.airelibre.modules.home.model

import com.google.gson.annotations.SerializedName
import com.lucasginard.airelibre.utils.Constants

data class StatusResponse(
    @SerializedName("service") var service: String,
    @SerializedName("database") var database: String,

    val getDataBaseIsUp:() -> Boolean ={
        database == Constants.IS_UP
    },

    val getServiceIsUp:() -> Boolean ={
        service == Constants.IS_UP
    }
)
