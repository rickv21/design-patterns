package com.nhlstenden.morithij.budgettracker

import com.nhlstenden.morithij.budgettracker.models.*
import javafx.application.Application
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
        SceneManager.primaryStage = primaryStage
        SceneManager.switchScene("overview")
    }
}