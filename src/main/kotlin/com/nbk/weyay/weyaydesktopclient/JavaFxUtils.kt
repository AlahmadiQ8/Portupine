package com.nbk.weyay.weyaydesktopclient

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.util.Callback
import org.kordamp.ikonli.javafx.FontIcon
import kotlin.reflect.KProperty

operator fun <T> ObservableValue<T>.getValue(thisRef: Any, property: KProperty<*>): T = value
operator fun <T> Property<T>.setValue(thisRef: Any, property: KProperty<*>, value: T?) = setValue(value)

class IconCell : TableCell<TableView<Destination>, Status>() {
    override fun updateItem(item: Status?, empty: Boolean) {
        super.updateItem(item, empty)
        graphic = if (!empty) item?.let { createIconContainer(it) } else null
    }

    private fun createIconContainer(item: Status) = HBox().apply {
        alignment = Pos.CENTER
        children.add(item.getIcon())
    }

    companion object {
        @JvmStatic
        fun forTableColumn(): Callback<TableColumn<TableView<Destination>, Status>, TableCell<TableView<Destination>, Status>> = Callback { IconCell() }
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

fun Status.getIcon() = when(this) {
    Status.LOADING -> FontIcon("fas-spinner").apply { iconColor = Color.ORANGE }
    Status.REACHABLE -> FontIcon("fas-check").apply { iconColor = Color.GREEN }
    Status.UNREACHABLE -> FontIcon("fas-times").apply { iconColor = Color.RED }
    else -> FontIcon("fas-question").apply { iconColor = Color.GRAY }
}

fun ObservableList<Destination>.addChangeListener(change: ListChangeListener.Change<out Destination>.() -> Unit): ObservableList<Destination> {
    this.addListener(ListChangeListener(change))
    return this
}