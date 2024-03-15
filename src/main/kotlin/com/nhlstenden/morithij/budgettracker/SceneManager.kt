package com.nhlstenden.morithij.budgettracker

import com.nhlstenden.morithij.budgettracker.controllers.Controller
import com.nhlstenden.morithij.budgettracker.models.Model
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.awt.Color

object SceneManager {

    lateinit var primaryStage: Stage

    private val WINDOW_WIDTH = 1000.0
    private val WINDOW_HEIGHT = 700.0

    fun switchScene(key: String, vararg models: Model)  {
        val createdScene = createScene(key, *models)
        primaryStage.title = createdScene.first.title
        primaryStage.minWidth = WINDOW_WIDTH
        primaryStage.minHeight = WINDOW_HEIGHT

        val root = createdScene.second.getRoot() as Parent

        // Update the scene with the new view
        val scene = Scene(root)

        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun createScene(key: String, vararg models: Model) : Pair<Controller, FXMLLoader> {
        val viewName : String
        when (key) {
            "overview" -> {
                viewName = "overview"
            }
            "expense" -> {
                viewName = "expense"
            }
            "planner" -> {
                viewName = "planner"
            }
            "account" -> {
                viewName = "account"
            }
            else -> {
                throw IllegalArgumentException("No such key: $key")
            }
        }
        val fxmlLoader : FXMLLoader
        try {
            fxmlLoader = FXMLLoader(javaClass.getResource("/views/${viewName}.fxml"))
            fxmlLoader.load()
        } catch (e: NullPointerException) {
            throw IllegalArgumentException("No such view: $viewName")
        }

        val controller = fxmlLoader.getController<Controller>()
        controller.setModels(*models)

        return Pair(controller, fxmlLoader)
    }
}