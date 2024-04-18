package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.SceneManager
import com.nhlstenden.morithij.budgettracker.models.TestModel
import javafx.fxml.FXML
import javafx.scene.control.Label

class HeaderController() : Controller() {

    @FXML
    lateinit var homeLabel : Label

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
        homeLabel.setOnMouseClicked {
            SceneManager.switchScene("overview")
        }

        expenseLabel.setOnMouseClicked {
            SceneManager.switchScene("expense")
        }

        budgetLabel.setOnMouseClicked {
            SceneManager.switchScene("overview")
        }

        planningLabel.setOnMouseClicked {
            SceneManager.switchScene("planner")
        }

        settingsLabel.setOnMouseClicked {
            SceneManager.switchScene("settings")
        }
    }
}