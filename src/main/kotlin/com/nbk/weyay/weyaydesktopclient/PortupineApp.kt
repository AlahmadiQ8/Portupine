package com.nbk.weyay.weyaydesktopclient

import javafx.application.Application
import javafx.application.Application.launch
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.SceneAntialiasing
import javafx.stage.Stage

class PortupineApp : Application() {

    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(PortupineApp::class.java.getResource("main-view.fxml"))
        Scene(fxmlLoader.load(), -1.0, -1.0, true, SceneAntialiasing.BALANCED)
            .run {
                stylesheets.add(PortupineApp::class.java.getResource("stylesheet.css")?.toExternalForm()!!)
                stage.scene = this
            }
        stage.title = "Portupine"
        stage.show()
    }
}

fun main() {
    launch(PortupineApp::class.java)
}