package com.nbk.weyay.weyaydesktopclient

import java.io.*

fun Reader.readCsv(requiredHeaders: Set<String>): MutableList<HashMap<String, String>> {
    use {reader ->
        val result = mutableListOf<HashMap<String, String>>()
        val colHeaders: LinkedHashMap<Int, String> = LinkedHashMap()
        var firstLine = true
        reader.forEachLine { line ->
            val data = line.split(",")
            if (firstLine) {
                data.map { header ->
                    header.trim().lowercase().also {
                        if (!requiredHeaders.contains(it)) {
                            throw IllegalArgumentException("CSV file does not contain column $it")
                        }
                    }
                }.forEachIndexed { index, key ->
                    colHeaders[index] = key
                }
                firstLine = false
            } else {
                val map = HashMap<String, String>()
                line.split(",").forEachIndexed { colIndex, value ->
                    colHeaders[colIndex]?.run {
                        map[this] = value
                    }
                }
                result.add(map)
            }
        }

        return result
    }
}