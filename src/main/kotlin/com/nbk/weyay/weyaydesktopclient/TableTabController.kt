package com.nbk.weyay.weyaydesktopclient

import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.converter.IntegerStringConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import java.net.URL
import java.util.*
import kotlin.coroutines.CoroutineContext

class TableTabController : CoroutineScope, Initializable {

    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx + job

    @FXML
    private lateinit var table: TableView<Destination>

    @FXML
    private lateinit var portTableColumn: TableColumn<TableView<Destination>, Int>

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        table.selectionModel.selectionMode = SelectionMode.MULTIPLE
        portTableColumn.cellFactory = TextFieldTableCell.forTableColumn(IntegerStringConverter())
    }

    fun addTableData(items: ObservableList<Destination>) {
        table.items = items
    }

    @FXML
    private fun onCheckSelected(event: ActionEvent) {
        table.checkSelectedRow()
    }

    @FXML
    private fun checkAllRows() {
        val channel = Channel<Destination>()
        table.items.forEach {
            launch {
                it.status = Status.LOADING
                table.items[table.items.indexOf(it)] = it
                channel.send(checkReachability(it))
            }
        }
        launch {
            channel.consumeEach {
                table.items[table.items.indexOf(it)] = it
            }
        }
    }

    @FXML
    private fun addNewRow() {
        val newDestination = Destination("", 80, "")

        table.apply {
            requestFocus()
            items.add(newDestination)
            val rowIndex = items.indexOf(newDestination)
            selectionModel.apply {
                clearSelection()
                select(newDestination)
            }
            focusModel.focus(rowIndex)
            scrollTo(newDestination)
            edit(rowIndex, table.columns[1])
        }
    }

    @FXML
    private fun removeRows(event: ActionEvent) {
        table.items.removeAll(table.selectionModel.selectedItems)
    }

    private fun TableView<Destination>.checkSelectedRow() {
        val channel = Channel<Destination>()
        selectionModel.selectedItems.forEach {
            launch {
                it.status = Status.LOADING
                items[items.indexOf(it)] = it
                channel.send(checkReachability(it))
            }
        }
        launch {
            channel.consumeEach {
                items[items.indexOf(it)] = it
            }
        }
    }

}