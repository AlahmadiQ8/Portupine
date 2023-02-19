package com.nbk.weyay.weyaydesktopclient

import kotlinx.coroutines.delay
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

const val TIMEOUT_MS = 3000

suspend fun isReachable(destination: Destination): Boolean {
    delay(2000)
    Socket().use {
        return try {
            it.connect(InetSocketAddress(destination.host, destination.port), TIMEOUT_MS)
            true
        } catch (e: IOException) {
            false
        }
    }
}

val File.nameWithoutExtension: String
    get() = if (this.name.indexOf(".") > 0) {
        this.name.substring(0, this.name.lastIndexOf("."))
    } else {
        this.name
    }
