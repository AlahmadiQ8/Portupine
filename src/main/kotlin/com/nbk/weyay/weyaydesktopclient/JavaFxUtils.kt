package com.nbk.weyay.weyaydesktopclient

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.TableCell
import javafx.scene.control.TableView
import javafx.scene.layout.HBox
import org.kordamp.ikonli.javafx.FontIcon
import kotlin.reflect.KProperty

operator fun <T> ObservableValue<T>.getValue(thisRef: Any, property: KProperty<*>): T = value
operator fun <T> Property<T>.setValue(thisRef: Any, property: KProperty<*>, value: T?) = setValue(value)

class IconCell : TableCell<TableView<Destination>, Status>() {
    override fun updateItem(item: Status?, empty: Boolean) {
        super.updateItem(item, empty)
        graphic = if (!empty) {
            when (item) {
                Status.UNREACHABLE -> createPriorityGraphic("fas-step-forward")
                else  -> createPriorityGraphic("fas-fast-forward")
            }
        } else {
            null
        }
    }

    private fun createPriorityGraphic(code: String): Node {
        val graphicContainer = HBox()
        graphicContainer.alignment = Pos.CENTER
        val imageView = FontIcon(code)
        graphicContainer.children.add(imageView)
        return graphicContainer
    }
}

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