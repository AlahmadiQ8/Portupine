@file:OptIn(ExperimentalCoroutinesApi::class)

package com.nbk.weyay.weyaydesktopclient

import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.stage.FileChooser
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.javafx.JavaFx
import java.io.File
import kotlin.coroutines.CoroutineContext


class MainController: CoroutineScope {

    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx + job

    @FXML
    private lateinit var welcomeText: Label

    @FXML
    private lateinit var statusLabel: Label

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