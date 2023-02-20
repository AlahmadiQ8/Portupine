package com.nbk.weyay.weyaydesktopclient

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableView

class TableTabController {

    @FXML
    private lateinit var table: TableView<Destination>

    init {
        table.selectionModel.selectionMode = SelectionMode.MULTIPLE
    }

    @FXML
    private fun onCheckSelected(event: ActionEvent) {
        println("REFERENCE TABLE VIEW TO PERFORM ACTION")
    }

    @FXML
    private fun onCheckAll(event: ActionEvent) {
        println("REFERENCE TABLE VIEW TO PERFORM ACTION")
    }
}