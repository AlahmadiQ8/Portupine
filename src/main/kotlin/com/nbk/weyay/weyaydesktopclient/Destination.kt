package com.nbk.weyay.weyaydesktopclient

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

class Destination(host: String, port: Int, description: String) {

    @get:JvmName("hostProperty")
    val hostProperty = SimpleStringProperty(host)
    var host by hostProperty

    @get:JvmName("portProperty")
    val portProperty = SimpleIntegerProperty(port)
    var port by portProperty

    @get:JvmName("descriptionProperty")
    val descriptionProperty = SimpleStringProperty(description)
    val description by descriptionProperty

    @get:JvmName("statusProperty")
    val statusProperty = SimpleObjectProperty(Status.UNKNOWN)
    var status by statusProperty

    override fun toString(): String {
        return "[host: $host, port: $port, description: $description, status: $status]"
    }
}

enum class Status {
    REACHABLE,
    UNREACHABLE,
    UNKNOWN,
    LOADING
}