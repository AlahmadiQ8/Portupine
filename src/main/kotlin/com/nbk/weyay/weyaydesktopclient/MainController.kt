package com.nbk.weyay.weyaydesktopclient

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.FileChooser
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.javafx.JavaFx
import java.io.File
import java.io.FileReader
import java.net.URL
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.coroutines.CoroutineContext


class MainController : CoroutineScope, Initializable {

    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx + job

    @FXML
    private lateinit var welcomeText: Label

    @FXML
    private lateinit var statusLabel: Label

    @FXML
    private lateinit var tabPane: TabPane

    private val tabsData = mutableListOf<ObservableList<Destination>>()

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

                tabsData.add(tableData)
                val tab = createTableTab(file.nameWithoutExtension, tabsData.last(), onCheckSingle())
                tabPane.tabs.run {
                    if (size == 1) {
                        this[0] = tab
                    } else {
                        add(tab)
                    }
                }
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
        tabsData.add(tableData)
        val tab = createTableTab("example", tabsData.first(), onCheckSingle())
        tabPane.tabs.add(tab)
        tabPane.selectionModel.select(tab)
    }

    @Suppress("UNCHECKED_CAST")
    private fun onCheckSingle(): EventHandler<ActionEvent> {
        return EventHandler {
            val currentTab = tabPane.selectionModel.selectedItem.content as AnchorPane
            val table = findNodeBFS(currentTab as Parent) { it is TableView<*>}!! as TableView<Destination>
            table.selectionModel.selectedItem?.run {
                launch {
                    port = 122344
                    table.items[table.items.indexOf(this@run)] = this@run
                }
            }
        }
    }

    private fun findNodeBFS(start: Parent, predicate: (c: Parent) -> Boolean): Parent? {
        val queue = ArrayDeque<Parent>()
        queue.add(start)

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



    @Suppress("OPT_IN_USAGE")
    private fun exampleCoroutine() {
        val destination = Destination("google.com", 443, "testing")
        launch {
            statusLabel.text = "Checking ${destination.host}:${destination.port}"
            destination.status = Status.LOADING
            val statusProducer = produce { send(isReachable(destination)) }
            statusProducer.consumeEach {
                statusLabel.text = "${destination.host}:${destination.port} is $it"
                destination.status = if (it) Status.REACHABLE else Status.UNREACHABLE
                println(destination.status.text + destination.description)
            }
        }
    }
}