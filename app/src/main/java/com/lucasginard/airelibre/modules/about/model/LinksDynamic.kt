package com.lucasginard.airelibre.modules.about.model

data class LinksDynamic(
    var linkTwitter:String ?= null,
    var linkWeb:String ?= null,
    var linkGitHub:String ?= null,
    var linkLicense:String ?= null,
    var linkAppAndroid:String ?= null,
    var linkIcon:String ?= null,
    var linkMastodon:String ?= null
)
