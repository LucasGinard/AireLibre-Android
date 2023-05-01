package com.lucasginard.airelibre.modules.notifications.data

import com.lucasginard.airelibre.modules.home.model.SensorResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APINotification {

    @GET("api/v1/aqi")
    fun getSensor(@Query("start") date:String,@Query("source") source:String): Call<ArrayList<SensorResponse>>

}