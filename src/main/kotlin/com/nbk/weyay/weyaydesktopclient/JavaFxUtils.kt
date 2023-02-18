package com.nbk.weyay.weyaydesktopclient

import javafx.scene.control.TableColumn
import javafx.scene.control.cell.PropertyValueFactory

fun <T> createColumn(text: String, propertyName: String): TableColumn<T, String> {
    return TableColumn<T, String>().apply {
        this.text = text
        cellValueFactory = PropertyValueFactory<T, String>(propertyName)
    }
}