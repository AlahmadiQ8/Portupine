package com.nbk.weyay.weyaydesktopclient

import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Tab
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.*

fun <T> createColumn(text: String, propertyName: String): TableColumn<T, String> {
    return TableColumn<T, String>().apply {
        this.text = text
        cellValueFactory = PropertyValueFactory<T, String>(propertyName)
    }
}

fun <T> createTableTab(tabName: String, tableData: ObservableList<T>) =
    Tab(tabName).apply {
        AnchorPane().apply {
            VBox().apply {
                spacing = 5.0
                padding = Insets(5.0, 0.0, 0.0, 0.0)
                AnchorPane.setTopAnchor(this, 0.0)
                AnchorPane.setBottomAnchor(this, 0.0)
                AnchorPane.setRightAnchor(this, 0.0)
                AnchorPane.setLeftAnchor(this, 0.0)
                GridPane().apply {
                    val colConstraints = ColumnConstraints(10.0, 60.0, 60.0, Priority.SOMETIMES, null, true)
                    columnConstraints.setAll(colConstraints, colConstraints)
                    rowConstraints.setAll(RowConstraints(10.0, 30.0, -1.0, Priority.SOMETIMES, null, true))
                    add(Button("Button").apply {
                        maxWidth = 200.0
                        isMnemonicParsing = false
                    }, 0, 0)
                    add(Button("Button").apply {
                        maxWidth = 200.0
                        isMnemonicParsing = false
                    }, 1, 0)
                }.also { children.add(it) }
                TableView<T>().apply {
                    AnchorPane.setRightAnchor(this, 0.0)
                    VBox.setVgrow(this, Priority.ALWAYS)
                    items = tableData
                    columns.addAll(
                        createColumn("Status", "status"),
                        createColumn("Host", "host"),
                        createColumn("Port", "port"),
                        createColumn("Description", "description")
                    )
                    prefHeight = 200.0
                    prefWidth = 200.0
                }.also { children.add(it) }
            }.also { children.add(it) }
        }.also { content = it }
    }