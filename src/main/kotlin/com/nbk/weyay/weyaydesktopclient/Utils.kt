package com.nbk.weyay.weyaydesktopclient

import kotlinx.coroutines.delay
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

const val TIMEOUT_MS = 3000

suspend fun isReachable(destination: Destination): Boolean {
    delay(2000)
    Socket().apply {
        return try {
            connect(InetSocketAddress(destination.host, destination.port), TIMEOUT_MS)
            true
        } catch (e: IOException) {
            false
        }
    }
}