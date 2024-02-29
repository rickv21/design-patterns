package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.models.TestModel
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label

/**
 * Test controller class for experimenting with MVC.
 */
class TestController {
    lateinit var testModel: TestModel

    @FXML
    lateinit var label: Label

    @FXML
    fun handleButtonAction(event: ActionEvent) {
        testModel.increment()
        label.text = "You clicked me ${testModel["counter"]} times!"
    }
}