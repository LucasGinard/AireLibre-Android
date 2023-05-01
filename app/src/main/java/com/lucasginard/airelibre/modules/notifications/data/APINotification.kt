package com.lucasginard.airelibre.modules.notifications.data

import com.lucasginard.airelibre.modules.home.model.SensorResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APINotification {

    @GET("api/v1/aqi")
    suspend fun getSensor(@Query("start") date:String,@Query("source") source:String): Response<ArrayList<SensorResponse>>

}