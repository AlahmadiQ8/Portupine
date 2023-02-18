package com.nbk.weyay.weyaydesktopclient

import java.io.BufferedReader
import java.io.Closeable
import java.io.File
import java.io.FileReader

class CsvReader(file: File, requiredHeaders: Set<String>): Closeable {

    private val fileReader = FileReader(file)
    private var bufferedReader: BufferedReader = BufferedReader(fileReader)
    private val colHeaders: LinkedHashMap<String, Int> = LinkedHashMap()

    init {
        // read first line
        bufferedReader.readLine().split(",").map { header ->
            header.trim().lowercase().also {
                if (!requiredHeaders.contains(it)) {
                    throw IllegalArgumentException("CSV file does not contain column $it")
                }
            }
        }.forEachIndexed { index, key ->
            colHeaders[key] = index
        }
    }

    fun readCsv(): List<HashMap<String, String>> {
        TODO("Not Implemented")
    }

    override fun close() {
        fileReader.close()
        bufferedReader.close()
    }
}