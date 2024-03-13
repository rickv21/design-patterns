package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.SceneManager
import com.nhlstenden.morithij.budgettracker.models.Model
import com.nhlstenden.morithij.budgettracker.models.TestModel
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

/**
 * Test controller class for experimenting with MVC.
 */
class TestController() : Controller() {
    lateinit var testModel: TestModel

    @FXML
    lateinit var pane: AnchorPane

    @FXML
    lateinit var expensesLabel: Label


    @FXML
    fun initialize() {
        pane.setOnKeyPressed { event: KeyEvent ->
            if (event.code == KeyCode.F5) {
                SceneManager.switchScene("sqltest")
            }
        }
    }

    override fun setModels(vararg models: Any) {
        testModel = models[0] as TestModel
    }
}