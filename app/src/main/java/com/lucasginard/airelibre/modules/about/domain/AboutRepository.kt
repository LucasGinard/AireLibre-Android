package com.lucasginard.airelibre.modules.about.domain

import com.lucasginard.airelibre.modules.data.APIGitHub
import javax.inject.Inject

class AboutRepository @Inject constructor(private val gitHubService: APIGitHub) {

    suspend fun getContributors(user:String, repo:String) = gitHubService.getContributors(user,repo)
}