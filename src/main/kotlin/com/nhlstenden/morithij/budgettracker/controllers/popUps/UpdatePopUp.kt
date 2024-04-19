package com.nhlstenden.morithij.budgettracker.controllers.popUps

import com.nhlstenden.morithij.budgettracker.controllers.Observer
import com.nhlstenden.morithij.budgettracker.models.BudgetModel
import com.nhlstenden.morithij.budgettracker.models.ExpenseModel
import com.nhlstenden.morithij.budgettracker.models.UserInfoModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.HBox

class UpdatePopUp (userInfo: UserInfoModel, budgetModel : BudgetModel, expenseModel: ExpenseModel, observer: Observer) : PopUp(userInfo, observer){
    init {
        stage.title = "Update Expense"
        val label1 = Label("Money:")
        val textField1 = TextField()
        textField1.text = expenseModel.money.toString()
        layout.add(label1, 0, 0)
        layout.add(textField1, 1, 0)

        val label2 = Label("Date:")
        val textField2 = DatePicker()
        textField2.value = expenseModel.recordDate
        layout.add(label2, 0, 1)
        layout.add(textField2, 1, 1)

        val label3 = Label("Description:")
        val textField3 = TextField()
        textField3.text = expenseModel.description
        layout.add(label3, 0, 2)
        layout.add(textField3, 1, 2)

        val intervalLabel = Label("Interval:")
        val interval = ComboBox<String>()

        // Add items to the ComboBox
        interval.items = FXCollections.observableArrayList("Weekly", "Monthly", "Annually")


        // Handle selection change
        interval.setOnAction {
            val selectedOption = interval.value
        }

        val endDateLabel = Label("End date:")
        val endDate = DatePicker()
        var scene = Scene(layout, 300.0, 200.0)
        val thread = Thread {
            val dao = DAOFactory.getDAO(ExpenseModel::class.java) as DAO<ExpenseModel>
            val old = dao.get(1)
            if(old?.interval != null) {
                // Set default selection
                interval.selectionModel.select(old.interval)
                endDate.value = old.endDate
                layout.add(intervalLabel, 0, 7)
                layout.add(interval, 1, 7)
                layout.add(endDateLabel, 0, 8)
                layout.add(endDate, 1, 8)
                stage.minHeight = 300.0
                stage.maxHeight = 300.0
            }
            textField1.text = old?.money.toString()
            textField2.value = old?.recordDate
            textField3.text = old?.description.toString()
        }
        thread.start()

        val okButton = Button("Update")
        okButton.setOnAction {

            if (textField1.text.isEmpty() || textField2.value == null || textField3.text.isEmpty()) {
                val errorAlert = Alert(Alert.AlertType.ERROR)
                errorAlert.title = "Error"
                errorAlert.headerText = "Please fill in all fields!"
                errorAlert.showAndWait()
                return@setOnAction
            }

            if(textField1.text.toDoubleOrNull() == null){
                val errorAlert = Alert(Alert.AlertType.ERROR)
                errorAlert.title = "Error"
                errorAlert.headerText = "Please enter a valid number!"
                errorAlert.showAndWait()
                return@setOnAction
            }

            if (textField1.text.toDouble() > userInfo.expenseLimit) {
                if (userInfo.expenseLimit != 0.0) {
                    val errorAlert = Alert(Alert.AlertType.ERROR)
                    errorAlert.title = "Error"
                    errorAlert.headerText = "The expense you entered is more than your set limit!"
                    errorAlert.showAndWait()
                    return@setOnAction
                }
            }

            if(endDate.value != null && endDate.value.isBefore(textField2.value)){
                val errorAlert = Alert(Alert.AlertType.ERROR)
                errorAlert.title = "Error"
                errorAlert.headerText = "End date must be after the Date"
                errorAlert.showAndWait()
                return@setOnAction
            }

            val thread = Thread {
                val dao = DAOFactory.getDAO(ExpenseModel::class.java) as DAO<ExpenseModel>
                dao.update(ExpenseModel(budgetModel.id, textField1.text.toDouble(), textField2.value, textField3.text, interval.value, endDate.value, expenseModel.id))
                Platform.runLater {
                    val successAlert = Alert(Alert.AlertType.INFORMATION)
                    successAlert.title = "Success"
                    successAlert.headerText = "Updated expense!"
                    successAlert.showAndWait()
                    stage.close()
                }
            }
            thread.start()
            stage.close()
        }

        val cancelButton = Button("Cancel")
        cancelButton.setOnAction {
            stage.close()
        }

        val buttonBox = HBox(10.0)
        buttonBox.alignment = Pos.CENTER
        buttonBox.children.addAll(okButton, cancelButton)
        layout.add(buttonBox, 0, 3, 2, 1)

        stage.scene = scene
        stage.showAndWait()
    }
}