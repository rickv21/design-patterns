package com.nhlstenden.morithij.budgettracker.controllers.popUps

import com.nhlstenden.morithij.budgettracker.controllers.Observer
import com.nhlstenden.morithij.budgettracker.models.BudgetModel
import com.nhlstenden.morithij.budgettracker.models.ExpenseModel
import com.nhlstenden.morithij.budgettracker.models.UserInfoModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.HBox

class DeletePopUp (userInfo: UserInfoModel, budgetModel : BudgetModel, expenseModel: ExpenseModel, observer: Observer) : PopUp(userInfo, observer){
    init {
        stage.title = "Delete Expense"
        val okButton = Button("Delete")
        okButton.setOnAction {
            val thread = Thread {
                val dao = DAOFactory.getDAO(ExpenseModel::class.java) as DAO<ExpenseModel>
                dao.addObserver(observer)
                dao.delete(expenseModel.id)
                Platform.runLater {
                    val successAlert = Alert(Alert.AlertType.INFORMATION)
                    successAlert.title = "Success"
                    successAlert.headerText = "Deleted expense!"
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

        val scene = Scene(layout, 300.0, 200.0)
        stage.scene = scene
        stage.showAndWait()
    }
}