package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.SceneManager
import com.nhlstenden.morithij.budgettracker.models.TestModel
import javafx.fxml.FXML
import javafx.scene.control.Label

class HeaderController() : Controller() {

    @FXML
    lateinit var expenseLabel: Label

    @FXML
    lateinit var budgetLabel: Label

    @FXML
    lateinit var planningLabel: Label

    @FXML
    lateinit var settingsLabel: Label

    @FXML
    fun initialize() {
        expenseLabel.setOnMouseClicked {
            SceneManager.switchScene("expense", TestModel())
        }

        budgetLabel.setOnMouseClicked {
            SceneManager.switchScene("overview", TestModel())
        }

        planningLabel.setOnMouseClicked {
            SceneManager.switchScene("planner", TestModel())
        }

        settingsLabel.setOnMouseClicked {
            SceneManager.switchScene("settings")
        }
    }

    override fun setModels(vararg models: Any) {
        //
    }
}