package com.nhlstenden.morithij.budgettracker

import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage

// The entry point for the application.
fun main(args: Array<String>) {
    Application.launch(BudgetTracker::class.java, *args)
}

/**
 * The main class for the BudgetTracker application.
 */
class BudgetTracker : Application() {

    override fun start(primaryStage: Stage) {
        primaryStage.icons.add(Image(javaClass.getResourceAsStream("/budget.png")));
        SceneManager.primaryStage = primaryStage
        SceneManager.switchScene("overview")
    }
}