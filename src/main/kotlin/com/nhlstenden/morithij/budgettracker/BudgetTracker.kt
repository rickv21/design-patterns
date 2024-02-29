package com.nhlstenden.morithij.budgettracker

import com.nhlstenden.morithij.budgettracker.controllers.SQLTestController
import com.nhlstenden.morithij.budgettracker.controllers.TestController
import com.nhlstenden.morithij.budgettracker.models.TestModel
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.stage.Stage


fun main(args: Array<String>) {
    Application.launch(BudgetTracker::class.java, *args)
}

class BudgetTracker : Application() {

    override fun start(primaryStage: Stage) {
        //The view and controllers should be created in a Factory and returned via Pair<>.
        val loader = FXMLLoader(javaClass.getResource("/views/testview.fxml"))
        val root = loader.load<Parent>()
        val controller = loader.getController<TestController>()
        controller.testModel = TestModel()

        primaryStage.width = 500.0
        primaryStage.height = 375.0

        primaryStage.title = "Budget tracker MVC Test"

        val scene = Scene(root)
        primaryStage.scene = scene
        primaryStage.show()

        // Temporary code to switch between test views
        // Pressing F5 will open the SQL Test view.
        scene.setOnKeyPressed { event ->
            if (event.code == KeyCode.F5) {
                // Load the new view and controller when F5 is pressed
                val newLoader = FXMLLoader(javaClass.getResource("/views/sqltestview.fxml"))
                val newRoot = newLoader.load<Parent>()
                val newController = newLoader.getController<SQLTestController>()
                primaryStage.width = 800.0
                primaryStage.title = "Budget tracker SQL Test"
                // Update the scene with the new view
                scene.root = newRoot
            }
        }
    }
}