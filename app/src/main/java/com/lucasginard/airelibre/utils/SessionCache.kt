package com.lucasginard.airelibre.utils

import com.lucasginard.airelibre.modules.about.model.Contributor
import com.lucasginard.airelibre.modules.about.model.LinksDynamic

open class SessionCache {
    companion object{
        var listContributorsCache = ArrayList<Contributor>()
        var linksDynamicCache:LinksDynamic ?= null
        var lastUpdate:String = ""
    }
}