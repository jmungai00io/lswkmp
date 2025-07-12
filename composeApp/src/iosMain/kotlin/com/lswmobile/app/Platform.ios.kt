package com.lswmobile.app

actual fun getPlatform(): Platform = IOSPlatform()

class IOSPlatform : Platform {
    override val name: String = "iOS"
}
