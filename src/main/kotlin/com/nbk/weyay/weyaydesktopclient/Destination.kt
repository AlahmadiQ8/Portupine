package com.nbk.weyay.weyaydesktopclient

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

class Destination(host: String, port: Int, description: String) {
    val hostProperty = SimpleStringProperty(host)
    var host by hostProperty

    val portProperty = SimpleIntegerProperty(port)
    var port by portProperty

    val descriptionProperty = SimpleStringProperty(description)
    val description by descriptionProperty

    val statusProperty = SimpleObjectProperty(Status.UNKNOWN)
    var status by statusProperty

    override fun toString(): String {
        return "[host: $host, port: $port, description: $description, status: $status]"
    }
}

enum class Status(val text: String) {
    REACHABLE("Reachable"),
    UNREACHABLE("Unreachable"),
    UNKNOWN("Unknown"),
    LOADING("Checking")
}