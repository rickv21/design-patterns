package com.nhlstenden.morithij.budgettracker

import com.nhlstenden.morithij.budgettracker.models.TestModel
import javafx.application.Application
import javafx.scene.control.Alert
import javafx.stage.Stage
import java.lang.reflect.InvocationTargetException

// The entry point for the application.
fun main(args: Array<String>) {
    Application.launch(BudgetTracker::class.java, *args)
}

/**
 * The main class for the BudgetTracker application.
 */
class BudgetTracker : Application() {

    override fun start(primaryStage: Stage) {
        try {
            SceneManager.primaryStage = primaryStage
            SceneManager.switchScene("test", TestModel())

            //Error handler.
            Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
                throwable.printStackTrace()
                val alert = Alert(Alert.AlertType.ERROR)
                alert.title = "Error Dialog"
                var ex = throwable

                while (ex.cause != null){
                    ex = ex.cause!!
                }
                val message: String = ex.message ?: "Unknown error"
                alert.headerText = ex::class.simpleName//"An error occurred:"
                alert.contentText = message
                alert.contentText = ex.message

                alert.showAndWait()

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}