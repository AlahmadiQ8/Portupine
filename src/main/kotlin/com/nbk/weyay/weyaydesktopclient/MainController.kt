@file:Suppress("UNCHECKED_CAST")

package com.nbk.weyay.weyaydesktopclient

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.FileChooser
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.javafx.JavaFx
import org.kordamp.ikonli.javafx.FontIcon
import java.io.File
import java.io.FileReader
import java.net.URL
import java.util.*
import kotlin.coroutines.CoroutineContext


@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("OPT_IN_USAGE")
class MainController : CoroutineScope, Initializable {

    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx + job

    @FXML
    private lateinit var welcomeText: Label

    @FXML
    private lateinit var statusLabel: Label

    @FXML
    private lateinit var tabsPane: TabPane

    @FXML
    private lateinit var welcomePane: AnchorPane

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        println("initialized")
    }

    @FXML
    private fun openFile() {
        val fileChooser = FileChooser()
        fileChooser.title = "Select File"
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("CSV Files", "*.csv"))
        val file: File? = fileChooser.showOpenDialog(null)
        exampleCoroutine()
        val errorHandler = CoroutineExceptionHandler { context, exception ->
            println(exception.message)
            println(context.toString())
        }
        launch(errorHandler) {
            file?.run {
                val data = FileReader(this).readCsv(setOf("host", "port", "description"))
                val tableData = FXCollections.observableArrayList<Destination>()
                // TODO: catch NumberFormatException for toInt
                data.forEach {
                    val destination = Destination(
                        it.getValue("host"),
                        it.getValue("port").toInt(),
                        it.getValue("description")
                    )
                    println(destination)
                    tableData.add(destination)
                }

                createTableTab(file.nameWithoutExtension, tableData)
            }
        }
    }

    @FXML
    private fun loadExample() {
        val data = MainController::class.java.getResourceAsStream("example.csv")
            ?.reader()
            ?.readCsv(setOf("host", "port", "description"))
            ?: error("Cannot open/find the file")
        val tableData = FXCollections.observableArrayList<Destination>()
        data.forEach {
            val destination = Destination(
                it.getValue("host"),
                it.getValue("port").toInt(),
                it.getValue("description")
            )
            tableData.add(destination)
        }

        createTableTab("example", tableData)
    }

    private fun onCheckSelected(): EventHandler<ActionEvent> {
        return EventHandler {
            val currentTab = tabsPane.selectionModel.selectedItem.content as AnchorPane
            val table = (currentTab as Parent).findNodeBFS { it is TableView<*> }!! as TableView<Destination>
            table.checkSelectedRow()
        }
    }

    private fun checkAllRows(): EventHandler<ActionEvent> {
        return EventHandler {
            val currentTab = tabsPane.selectionModel.selectedItem.content as AnchorPane
            val table = (currentTab as Parent).findNodeBFS { it is TableView<*> }!! as TableView<Destination>
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

    private fun <T> createTableTab(tabName: String, tableData: ObservableList<T>) {
        val tab = Tab(tabName).apply {
            onClosed = EventHandler {
                println(tabsPane)
                if (tabsPane.tabs.size == 0) {
                    launch {
                        welcomePane.isVisible = true
                    }
                }
            }
            AnchorPane().apply {
                VBox().apply {
                    spacing = 5.0
                    padding = Insets(5.0, 0.0, 0.0, 0.0)
                    AnchorPane.setTopAnchor(this, 0.0)
                    AnchorPane.setBottomAnchor(this, 0.0)
                    AnchorPane.setRightAnchor(this, 0.0)
                    AnchorPane.setLeftAnchor(this, 0.0)
                    GridPane().apply {
                        padding = Insets(0.0, 0.0, 0.0, 5.0)
                        val colConstraints = ColumnConstraints(10.0, 30.0, 35.0, Priority.SOMETIMES, null, true)
                        columnConstraints.setAll(colConstraints, colConstraints)
                        rowConstraints.setAll(RowConstraints(10.0, 30.0, 35.0, Priority.SOMETIMES, null, true))
                        add(Button().apply {
                            isMnemonicParsing = false
                            onAction = onCheckSelected()
                            graphic = FontIcon("fas-step-forward:15:GREEN")
                        }, 0, 0)
                        add(Button().apply {
                            isMnemonicParsing = false
                            onAction = checkAllRows()
                            graphic = FontIcon("fas-fast-forward:15:GREEN")
                        }, 1, 0)
                    }.also { children.add(it) }
                    TableView<T>().apply {
                        selectionModel.selectionMode = SelectionMode.MULTIPLE
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

        launch {
            tabsPane.tabs.add(tab)
            welcomePane.isVisible = false
            tabsPane.selectionModel.select(tab)
        }
    }

    private fun exampleCoroutine() {
        val destination = Destination("google.com", 443, "testing")
        launch {
            statusLabel.text = "Checking ${destination.host}:${destination.port}"
            destination.status = Status.LOADING
            val statusProducer = produce { send(checkReachability(destination)) }
            statusProducer.consumeEach {
                statusLabel.text = "${destination.host}:${destination.port} is $it"
//                destination.status = if (it) Status.REACHABLE else Status.UNREACHABLE
                println(destination.status.text + destination.description)
            }
        }
    }
}