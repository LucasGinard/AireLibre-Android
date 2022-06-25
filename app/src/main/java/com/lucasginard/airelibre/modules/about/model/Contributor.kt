package com.lucasginard.airelibre.modules.about.model

import com.google.gson.annotations.SerializedName

data class Contributor(
    @SerializedName("login") var nameContributor: String,
    @SerializedName("avatar_url") var profileImage: String,
    @SerializedName("html_url") var githubContributor: String,
)
