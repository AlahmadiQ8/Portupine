package com.nbk.weyay.weyaydesktopclient

import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

const val TIMEOUT_MS = 3000

fun Destination.isReachable(): Boolean {
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
