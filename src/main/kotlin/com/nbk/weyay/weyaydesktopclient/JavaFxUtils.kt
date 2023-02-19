package com.nbk.weyay.weyaydesktopclient

import javafx.scene.Parent
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.PropertyValueFactory

fun <T> createColumn(text: String, propertyName: String): TableColumn<T, String> {
    return TableColumn<T, String>().apply {
        this.text = text
        cellValueFactory = PropertyValueFactory<T, String>(propertyName)
    }
}

fun Parent.findNodeBFS(predicate: (c: Parent) -> Boolean): Parent? {
    val queue = ArrayDeque<Parent>()
    queue.add(this)

    var current: Parent?

    while (!queue.isEmpty()) {
        current = queue.removeFirst()

        if (predicate(current)) {
            return current
        } else {
            current.childrenUnmodifiable.forEach {
                queue.add(it as Parent)
            }
        }
    }

    return null
}