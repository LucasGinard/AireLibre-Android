package com.lucasginard.airelibre.modules.data

import com.lucasginard.airelibre.modules.home.model.CityResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.collections.ArrayList

interface APIHome {

    @GET("api/v1/aqi")
    fun getList(@Query("start") date:String): Call<ArrayList<CityResponse>>

}