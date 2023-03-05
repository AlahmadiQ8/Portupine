package com.nbk.weyay.weyaydesktopclient

import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DataFormat
import javafx.scene.input.TransferMode
import javafx.util.converter.IntegerStringConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL
import java.util.*
import kotlin.coroutines.CoroutineContext

val SERIALIZED_MIME_TYPE = DataFormat("application/x-java-serialized-object")

class TableTabController : CoroutineScope, Initializable {

    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx + job

    @FXML
    private lateinit var table: TableView<Destination>

    @FXML
    private lateinit var portTableColumn: TableColumn<TableView<Destination>, Int>

    private val draggedItems: MutableList<Destination> = mutableListOf()

    var currentFile: File? = null

    lateinit var items: ObservableList<Destination>

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        table.apply tableView@ {
            selectionModel.selectionMode = SelectionMode.MULTIPLE

            // https://stackoverflow.com/a/52437193/5431968
            // I copied the whole answer and have no idea what's going on, but it works
            setRowFactory {
                TableRow<Destination?>().apply row@ {
                    setOnDragDetected { mouseEvent ->
                        if (!isEmpty) {
                            draggedItems.clear()
                            draggedItems.addAll(this@tableView.selectionModel.selectedItems)
                            val dragAndDropStart = startDragAndDrop(TransferMode.MOVE).apply {
                                dragView = this@row.snapshot(null, null)
                            }
                            ClipboardContent().apply {
                                put(SERIALIZED_MIME_TYPE, this@row.index)
                                dragAndDropStart.setContent(this)
                            }
                            mouseEvent.consume()
                        }
                    }

                    setOnDragOver { dragEvent ->
                        if (dragEvent.dragboard.hasContent(SERIALIZED_MIME_TYPE)) {
                            if (index != dragEvent.dragboard.getContent(SERIALIZED_MIME_TYPE) as Int) {
                                dragEvent.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
                                dragEvent.consume()
                            }
                        }
                    }

                    setOnDragDropped { dragEvent ->
                        if (dragEvent.dragboard.hasContent(SERIALIZED_MIME_TYPE)) {
                            var destination: Destination? = null
                            var delta = 0
                            var dropIndex = if (this@row.isEmpty) {
                                it.items.size
                            } else {
                                destination = it.items[this@row.index]
                                this@row.index
                            }
                            if (destination != null) {
                                while (draggedItems.contains(destination)) {
                                    delta = 1
                                    --dropIndex
                                    if (dropIndex < 0) {
                                        destination = null
                                        dropIndex = 0
                                        break
                                    }
                                    destination = it.items[dropIndex]
                                }
                            }
                            it.items.removeAll(draggedItems)
                            if (destination != null) {
                                dropIndex = it.items.indexOf(destination) + delta
                            } else if (dropIndex != 0) {
                                dropIndex = it.items.size
                            }
                            it.selectionModel.clearSelection()
                            for (d in draggedItems) {
                                it.items.add(dropIndex, d)
                                it.selectionModel.select(dropIndex++)
                            }
                            dragEvent.isDropCompleted = true
                            draggedItems.clear()
                            dragEvent.consume()
                        }
                    }
                }
            }
        }
        portTableColumn.cellFactory = TextFieldTableCell.forTableColumn(IntegerStringConverter())
    }

    fun addTableData(items: ObservableList<Destination>) {
        this.items = items
        table.items = items
    }

    fun saveFile(): Boolean {
        return currentFile?.let {
            it.printWriter().use { out ->
                out.println("host,port,description")
                table.items.forEach {
                    out.println("${it.host},${it.port},${it.description}")
                }
            }
            true
        } ?: false
    }

    @FXML
    private fun onCheckSelected() {
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
    private fun removeRows() {
        table.items.removeAll(table.selectionModel.selectedItems)
    }

    private fun ObservableList<Destination>.checkReachability() {
        forEach {
            launch(Dispatchers.Default) {
                it.status = Status.LOADING
                it.status = if (it.isReachable()) Status.REACHABLE else Status.UNREACHABLE
            }
        }
    }

}