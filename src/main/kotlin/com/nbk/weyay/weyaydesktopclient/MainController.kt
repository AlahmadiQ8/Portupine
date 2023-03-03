@file:Suppress("UNCHECKED_CAST")

package com.nbk.weyay.weyaydesktopclient

import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import java.awt.Desktop
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

                val (newTab, newTabController) = loadNewTableTab()

                launch {
                    newTab.apply {
                        text = file.nameWithoutExtension
                        onClosed = onClosedTabHandler()
                    }
                    tabsPane.tabs.add(newTab)
                    welcomePane.isVisible = false
                    tabsPane.selectionModel.select(newTab)
                    newTabController.addTableData(tableData)
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

        val (newTab, newTabController) = loadNewTableTab()

        launch {
            newTab.apply {
                text = "example.csv"
                onClosed = onClosedTabHandler()
            }
            tabsPane.tabs.add(newTab)
            welcomePane.isVisible = false
            tabsPane.selectionModel.select(newTab)
            newTabController.addTableData(tableData)
        }
    }

    @FXML
    fun goToProjectHomepage() {
        Desktop.getDesktop().browse(URL("https://github.com/AlahmadiQ8/Portupine").toURI())
    }

    @FXML
    fun closeApplication(event: ActionEvent) {
        ((event.source as MenuItem).parentPopup.ownerWindow.scene.window as Stage).close()
    }

    private fun loadNewTableTab(): Pair<Tab, TableTabController> {
        val newTabLoader = FXMLLoader(PortupineApp::class.java.getResource("TableTab.fxml"))
        val newTab = newTabLoader.load<Tab>()
        val newTabController = newTabLoader.getController<TableTabController>()
        return Pair(newTab, newTabController)
    }

    private fun onClosedTabHandler() = EventHandler<Event> {
        if (tabsPane.tabs.size == 0) {
            launch {
                welcomePane.isVisible = true
            }
        }
    }

}