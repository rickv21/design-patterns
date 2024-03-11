package com.nhlstenden.morithij.budgettracker

import com.nhlstenden.morithij.budgettracker.controllers.Controller
import com.nhlstenden.morithij.budgettracker.models.Model
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

object SceneManager {

    lateinit var primaryStage: Stage

    private val WINDOW_WIDTH = 900.0
    private val WINDOW_HEIGHT = 640.0

    fun switchScene(key: String, vararg models: Model)  {
        val createdScene = createScene(key, *models)
        primaryStage.title = createdScene.first.title
        primaryStage.width = WINDOW_WIDTH
        primaryStage.height = WINDOW_HEIGHT
        primaryStage.isResizable = false

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
            "sqltest" -> {
                viewName = "sqltestview"
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