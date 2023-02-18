@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nbk.weyay.weyaydesktopclient

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.FileChooser
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.javafx.JavaFx
import java.io.File
import java.net.URL
import java.util.*
import kotlin.coroutines.CoroutineContext


class MainController: CoroutineScope, Initializable {

    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx + job

    @FXML
    private lateinit var welcomeText: Label

    @FXML
    private lateinit var statusLabel: Label

    @FXML
    private lateinit var tabPane: TabPane

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val tab = Tab("first").apply {
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
                        rowConstraints.setAll(RowConstraints(10.0, 30.0, -1.0,  Priority.SOMETIMES, null, true))
                        add(Button("Button").apply {
                            maxWidth = 200.0
                            isMnemonicParsing = false }, 0, 0)
                        add(Button("Button").apply {
                            maxWidth = 200.0
                            isMnemonicParsing = false}, 1, 0)
                    }.also { children.add(it) }
                    TableView<String>().apply {
                        VBox.setVgrow(this, Priority.ALWAYS)
                        columns.addAll(
                            TableColumn<String, String>().apply { text = "Column 1" },
                            TableColumn<String, String>().apply { text = "Column 2" }
                        )
                        prefHeight = 200.0
                        prefWidth = 200.0
                    }.also { children.add(it) }
                }.also { children.add(it) }
            }.also { content = it }
        }
        tabPane.tabs.add(tab)
        println("initialized")
    }

    @FXML
    private fun onHelloButtonClick() {
        val fileChooser = FileChooser()
        fileChooser.title = "Select File"
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("CSV Files", "*.csv"))
        val file: File? = fileChooser.showOpenDialog(null)
        exampleCoroutine()
        file?.run {
            println(file.absolutePath)
        }
    }

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