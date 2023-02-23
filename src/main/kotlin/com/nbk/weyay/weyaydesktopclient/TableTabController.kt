package com.nbk.weyay.weyaydesktopclient

import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableView
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

class TableTabController() : CoroutineScope, Initializable {

    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx + job

    @FXML
    private lateinit var table: TableView<Destination>

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        table.selectionModel.selectionMode = SelectionMode.MULTIPLE
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