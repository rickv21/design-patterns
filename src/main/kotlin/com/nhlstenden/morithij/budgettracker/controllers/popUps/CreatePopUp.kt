package com.nhlstenden.morithij.budgettracker.controllers.popUps;

import com.nhlstenden.morithij.budgettracker.models.ExpenseModel
import com.nhlstenden.morithij.budgettracker.models.UserInfoModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*;
import javafx.scene.layout.HBox

class CreatePopUp (userInfo: UserInfoModel) : PopUp(userInfo) {
    init {
        stage.title = "Create Expense"
        val label1 = Label("Money:")
        val textField1 = TextField()
        layout.add(label1, 0, 0)
        layout.add(textField1, 1, 0)

        val label2 = Label("Date:")
        val textField2 = DatePicker()
        layout.add(label2, 0, 1)
        layout.add(textField2, 1, 1)

        val label3 = Label("Description:")
        val textField3 = TextField()
        layout.add(label3, 0, 2)
        layout.add(textField3, 1, 2)

        val okButton = Button("Add")
        okButton.setOnAction {

            if (textField1.text.isEmpty() || textField2.value == null || textField3.text.isEmpty()) {
                val errorAlert = Alert(Alert.AlertType.ERROR)
                errorAlert.title = "Error"
                errorAlert.headerText = "Please fill in all fields!"
                errorAlert.showAndWait()
                return@setOnAction
            }

//            if (textField1.text.toDouble() > userInfo.expenseLimit) {
//                if (userInfo.expenseLimit == 0.0) {
//                    return@setOnAction
//                }
//                val errorAlert = Alert(Alert.AlertType.ERROR)
//                errorAlert.title = "Error"
//                errorAlert.headerText = "The expense you entered is more than your set limit!"
//                errorAlert.showAndWait()
//                return@setOnAction
//            }

            val thread = Thread {
                val dao = DAOFactory.getDAO(ExpenseModel::class.java) as DAO<ExpenseModel>
                dao.create(ExpenseModel(1, textField1.text.toDouble(), textField2.value, textField3.text))
                Platform.runLater {
                    val successAlert = Alert(Alert.AlertType.INFORMATION)
                    successAlert.title = "Success"
                    successAlert.headerText = "Added expense!"
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
