package com.nhlstenden.morithij.budgettracker

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage


fun main(args: Array<String>) {
    Application.launch(BudgetTracker::class.java, *args)
}

class BudgetTracker : Application() {

    override fun start(primaryStage: Stage) {
        //The view and controllers should be created in a Factory and returned via Pair<>.
        val loader = FXMLLoader(javaClass.getResource("/view.fxml"))
        val root = loader.load<Parent>()
        val controller = loader.getController<Controller>()
        controller.model = Model()

        primaryStage.width = 500.0
        primaryStage.height = 375.0

        primaryStage.title = "Budget tracker MVC Test"
        primaryStage.scene = Scene(root)
        primaryStage.show()
    }
}