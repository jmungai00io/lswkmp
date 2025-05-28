package com.lswmobile.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform