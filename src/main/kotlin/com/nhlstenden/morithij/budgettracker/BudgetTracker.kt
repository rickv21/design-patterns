package com.nhlstenden.morithij.budgettracker

import javafx.application.Application
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.stage.Stage
import java.time.format.DateTimeParseException

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
            primaryStage.icons.add(Image(javaClass.getResourceAsStream("/budget.png")))
            SceneManager.primaryStage = primaryStage
            SceneManager.switchScene("overview")

            //Error handler.
            Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
                throwable.printStackTrace()

                //Handle datetime issues here since they get redirected here.
                if(throwable is DateTimeParseException){
                    val errorAlert = Alert(Alert.AlertType.ERROR)
                    errorAlert.title = "Date error"
                    errorAlert.headerText = "The date value is incorrect!"
                    errorAlert.showAndWait()
                    return@setDefaultUncaughtExceptionHandler
                }

                val alert = Alert(Alert.AlertType.ERROR)
                alert.title = "Error Dialog"
                var ex = throwable

                while (ex.cause != null) {
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