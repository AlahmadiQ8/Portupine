@file:Suppress("UNCHECKED_CAST")

package com.nbk.weyay.weyaydesktopclient

import javafx.collections.FXCollections
import javafx.collections.ObservableList
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
import java.nio.file.Path
import java.util.*
import kotlin.coroutines.CoroutineContext

const val VERSION = "v0.0.1"

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

    @FXML
    private lateinit var saveButton: MenuItem

    @FXML
    private lateinit var saveAsButton: MenuItem

    @FXML
    private lateinit var recentFilesContainer: VBox

    private lateinit var recentFilesFactory: NodeFactory<String>

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        statusLabel.text = VERSION
        tabsPane.selectionModel.selectedItemProperty().addListener { _, _, newTab ->
            saveButton.isDisable = newTab?.userData == null
            saveAsButton.isDisable = newTab?.userData == null
        }
        recentFilesFactory = NodeFactory(recentFilesContainer.children, RecentFiles.recentItems) { path ->
            val pathParsed = Path.of(path)
            Hyperlink(pathParsed.fileName.toString()).apply {
                setOnAction {
                    openFile(Path.of(path).toFile())
                }
            }
        }
    }

    @FXML
    private fun openFile() {
        val fileChooser = FileChooser()
        fileChooser.title = "Select File"
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("CSV Files", "*.csv"))
        val file: File? = fileChooser.showOpenDialog(null)
        openFile(file)
    }

    private fun openFile(file: File?) {
        val errorHandler = CoroutineExceptionHandler { context, exception ->
            println(exception.message)
            println(context.toString())
        }
        launch(errorHandler) {
            val (newTab, newTabController) = loadNewTableTab()
            file?.run {
                RecentFiles.addRecentFile(file.absolutePath)
                newTabController.currentFile = this
                val data = FileReader(this)
                    .readCsv(setOf("host", "port", "description"))
                    .toObservableList()
                    .addChangeListener {
                        while (next()) {
                            newTab.text = file.nameWithoutExtension + "*"
                        }
                    }

                newTab.apply {
                    text = file.nameWithoutExtension
                    onClosed = onClosedTabHandler()
                    userData = newTabController
                }
                tabsPane.tabs.add(newTab)
                welcomePane.isVisible = false
                tabsPane.selectionModel.select(newTab)
                newTabController.addTableData(data)
            }
        }
    }

    @FXML
    private fun new() {
        saveAs(emptyList())
    }

    @FXML
    private fun save() {
        val currentTabController = tabsPane.selectionModel.selectedItem.userData as TableTabController
        launch {
            if (currentTabController.saveFile()) {
                tabsPane.selectionModel.selectedItem.text = currentTabController.currentFile!!.nameWithoutExtension
            }
        }
    }

    @FXML
    private fun saveAs() {
        saveAs((tabsPane.selectionModel.selectedItem.userData as TableTabController).items)
    }

    private fun saveAs(items: Collection<Destination>) {
        println("saveAs")
        val fileChooser = FileChooser()
        fileChooser.title = "Save"
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("CSV Files", "*.csv"))
        val file: File? = fileChooser.showSaveDialog(null)

        launch {
            val (newTab, newTabController) = loadNewTableTab()
            file?.run {
                newTabController.currentFile = this
                val data = FXCollections.observableArrayList<Destination> { d ->
                    arrayOf(
                        d.hostProperty,
                        d.portProperty,
                        d.descriptionProperty,
                    )
                }.apply {
                    addAll(items)
                    addChangeListener {
                        while (next()) {
                            newTab.text = file.nameWithoutExtension + "*"
                        }
                    }
                }

                newTab.apply {
                    text = file.nameWithoutExtension
                    onClosed = onClosedTabHandler()
                    userData = newTabController
                }
                tabsPane.tabs.add(newTab)
                welcomePane.isVisible = false
                tabsPane.selectionModel.select(newTab)
                newTabController.addTableData(data)
                if (!newTabController.saveFile()) {
                    error("Unexpected error saving file")
                }
            }
        }
    }

    @FXML
    private fun loadExample() {
        val (newTab, newTabController) = loadNewTableTab()
        val data = MainController::class.java.getResourceAsStream("example.csv")
            ?.reader()
            ?.readCsv(setOf("host", "port", "description"))
            ?.toObservableList()
            ?.addChangeListener {
                while (next()) {
                    newTab.text = "example.csv*"
                }
            }
            ?: error("Cannot open/find the file")

        launch {
            newTab.apply {
                text = "example.csv"
                onClosed = onClosedTabHandler()
                userData = newTabController
            }
            tabsPane.tabs.add(newTab)
            welcomePane.isVisible = false
            tabsPane.selectionModel.select(newTab)
            newTabController.addTableData(data)
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

    private fun List<HashMap<String, String>>.toObservableList(): ObservableList<Destination> {
        val tableData = FXCollections.observableArrayList<Destination> { d ->
            arrayOf(
                d.hostProperty,
                d.portProperty,
                d.descriptionProperty,
            )
        }
        forEach {
            val destination = Destination(
                it.getValue("host"),
                it.getValue("port").toInt(),
                it.getValue("description")
            )
            tableData.add(destination)
        }
        return tableData
    }
}