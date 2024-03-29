package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.SceneManager
import com.nhlstenden.morithij.budgettracker.models.TestModel
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent


class HeaderController() : Controller() {

    @FXML
    lateinit var expenseLabel: Label

    @FXML
    lateinit var budgetLabel: Label

    @FXML
    lateinit var planningLabel: Label

    @FXML
    lateinit var accountLabel: Label



    @FXML
    fun initialize() {
        expenseLabel.setOnMouseClicked { event: MouseEvent ->
            SceneManager.switchScene("expense", TestModel())
        }

        budgetLabel.setOnMouseClicked { event: MouseEvent ->
            SceneManager.switchScene("overview", TestModel())
        }

        planningLabel.setOnMouseClicked { event: MouseEvent ->
            SceneManager.switchScene("planner", TestModel())
        }

        accountLabel.setOnMouseClicked { event: MouseEvent ->
            SceneManager.switchScene("account", TestModel())
        }
    }

    override fun setModels(vararg models: Any) {
        //
    }
}