package com.nbk.weyay.weyaydesktopclient

import java.io.BufferedReader
import java.io.Closeable
import java.io.File
import java.io.FileReader

class CsvReader(file: File, requiredHeaders: Set<String>): Closeable {

    private val fileReader = FileReader(file)
    private var bufferedReader: BufferedReader = BufferedReader(fileReader)
    private val colHeaders: LinkedHashMap<Int, String> = LinkedHashMap()

    init {
        // read first line
        bufferedReader.readLine().split(",").map { header ->
            header.trim().lowercase().also {
                if (!requiredHeaders.contains(it)) {
                    throw IllegalArgumentException("CSV file does not contain column $it")
                }
            }
        }.forEachIndexed { index, key ->
            colHeaders[index] = key
        }
    }

    fun readCsv(): List<HashMap<String, String>> {
        val result = mutableListOf<HashMap<String, String>>()
        bufferedReader.forEachLine {line ->
            val map = HashMap<String, String>()
            line.split(",").forEachIndexed { colIndex, value ->
                colHeaders[colIndex]?.run {
                    map[this] = value
                }
                result.add(map)
            }
        }
        return result
    }

    override fun close() {
        fileReader.close()
        bufferedReader.close()
    }
}