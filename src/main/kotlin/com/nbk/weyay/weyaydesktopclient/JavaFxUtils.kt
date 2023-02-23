package com.nbk.weyay.weyaydesktopclient

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.scene.Parent
import kotlin.reflect.KProperty

operator fun <T> ObservableValue<T>.getValue(thisRef: Any, property: KProperty<*>): T = value
operator fun <T> Property<T>.setValue(thisRef: Any, property: KProperty<*>, value: T?) = setValue(value)

@Suppress("unused")
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