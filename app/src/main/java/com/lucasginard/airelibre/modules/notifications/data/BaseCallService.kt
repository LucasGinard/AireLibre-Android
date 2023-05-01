package com.lucasginard.airelibre.modules.notifications.data

import com.lucasginard.airelibre.BuildConfig.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BaseCallService {
    fun serviceSensor(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}