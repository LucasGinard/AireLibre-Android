package com.lucasginard.airelibre.modules.data

import com.lucasginard.airelibre.modules.home.model.CityResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.collections.ArrayList

interface APIService {

    @GET("api/v1/aqi")
    fun getList(@Query("start") date:String): Call<ArrayList<CityResponse>>

    companion object {
        var retrofitService: APIService? = null
        fun getInstance() : APIService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://rald-dev.greenbeep.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(APIService::class.java)
            }
            return retrofitService!!
        }
    }
}