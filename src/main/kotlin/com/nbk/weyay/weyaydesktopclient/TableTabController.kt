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
        table.selectionModel.selectedItems.checkReachability()
    }

    @FXML
    private fun checkAllRows() {
        table.items.checkReachability()
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

    private fun ObservableList<Destination>.checkReachability() {
        forEach {
            launch {
                it.status = Status.LOADING
                it.status = if (it.isReachable()) Status.REACHABLE else Status.UNREACHABLE
            }
        }
    }

}