package com.lucasginard.airelibre.utils

import com.lucasginard.airelibre.modules.about.model.Contributor

open class SessionCache {
    companion object{
        var listContributorsCache = ArrayList<Contributor>()
        var lastUpdate:String = ""
    }
}