package com.nbk.weyay.weyaydesktopclient

import kotlinx.coroutines.delay
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Random

const val TIMEOUT_MS = 3000

suspend fun checkReachability(destination: Destination): Destination {
    val randomGenerator = Random()
    val duration = randomGenerator.nextInt(1000, 3000)
    delay(duration.toLong())
    Socket().use {
        try {
            it.connect(InetSocketAddress(destination.host, destination.port.toInt()), TIMEOUT_MS)
            destination.status = Status.REACHABLE
        } catch (e: IOException) {
            destination.status = Status.UNREACHABLE
        }
        return destination
    }
}

val File.nameWithoutExtension: String
    get() = if (this.name.indexOf(".") > 0) {
        this.name.substring(0, this.name.lastIndexOf("."))
    } else {
        this.name
    }
