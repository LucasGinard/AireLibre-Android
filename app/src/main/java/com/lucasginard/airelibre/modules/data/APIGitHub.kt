package com.lucasginard.airelibre.modules.data

import com.lucasginard.airelibre.modules.about.model.Contributor
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface APIGitHub {

    @GET("{user}/{repo}/contributors")
    suspend fun getContributors(@Path("user") user:String,@Path("repo") repo:String): Response<ArrayList<Contributor>>
}