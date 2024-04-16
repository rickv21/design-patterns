package com.nhlstenden.morithij.budgettracker.controllers

import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.WindowEvent

class ExpenseController() : Controller() {


    @FXML
    lateinit var button: Button

    @FXML
    fun initialize() {
        button.setOnAction {
            val popup = Stage()

            popup.initModality(Modality.APPLICATION_MODAL)
            popup.title = "Popup Window"

            val layout = GridPane()
            layout.alignment = Pos.CENTER
            layout.hgap = 10.0
            layout.vgap = 10.0
            layout.padding = Insets(25.0, 25.0, 25.0, 25.0)

            val label1 = Label("Input 1:")
            val textField1 = TextField()
            layout.add(label1, 0, 0)
            layout.add(textField1, 1, 0)

            val label2 = Label("Input 2:")
            val textField2 = TextField()
            layout.add(label2, 0, 1)
            layout.add(textField2, 1, 1)

            val label3 = Label("Input 3:")
            val textField3 = TextField()
            layout.add(label3, 0, 2)
            layout.add(textField3, 1, 2)

            val okButton = Button("OK")
            okButton.setOnAction {
                println("OK button pressed")
                popup.close()
            }
            layout.add(okButton, 0, 3)

            val cancelButton = Button("Cancel")
            cancelButton.setOnAction {
                println("Cancel button pressed")
                popup.close()
            }
            layout.add(cancelButton, 1, 3)

            val scene = Scene(layout, 300.0, 200.0)
            popup.scene = scene

            popup.setOnCloseRequest { event: WindowEvent ->
                println("Popup closed without pressing OK or Cancel")
            }

            popup.showAndWait()
        }

    }

}