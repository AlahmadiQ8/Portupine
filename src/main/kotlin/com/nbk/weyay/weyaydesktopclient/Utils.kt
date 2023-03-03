package com.nbk.weyay.weyaydesktopclient

import kotlinx.coroutines.delay
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Random

const val TIMEOUT_MS = 3000

suspend fun Destination.isReachable(): Boolean {
    val randomGenerator = Random()
    val duration = randomGenerator.nextInt(1000, 3000)
    delay(duration.toLong())
    Socket().use {
        try {
            it.connect(InetSocketAddress(host, port.toInt()), TIMEOUT_MS)
            return true
        } catch (e: IOException) {
            return false
        }
    }
}

val File.nameWithoutExtension: String
    get() = if (this.name.indexOf(".") > 0) {
        this.name.substring(0, this.name.lastIndexOf("."))
    } else {
        this.name
    }
