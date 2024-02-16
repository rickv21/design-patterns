package com.nhlstenden.morithij.budgettracker

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label

class Controller {
    lateinit var model: Model

    @FXML
    lateinit var label: Label

    @FXML
    fun handleButtonAction(event: ActionEvent) {
        model.increment()
        label.text = "You clicked me ${model["counter"]} times!"
    }
}