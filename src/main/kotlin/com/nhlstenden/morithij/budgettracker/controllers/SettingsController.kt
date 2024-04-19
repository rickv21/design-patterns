package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.models.UserInfoModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.TextField


class SettingsController() : Controller() {

    private lateinit var userInfo: UserInfoModel

    @FXML
    lateinit var expenseLimit: TextField

    @FXML
    lateinit var saveButton: Button

    override val title = super.title + " - Settings"

    @FXML
    fun initialize() {
        val thread = Thread {
            val dao = DAOFactory.getDAO(UserInfoModel::class.java) as DAO<UserInfoModel>
            userInfo = dao.get(1)!!
            Platform.runLater {
                expenseLimit.text = userInfo.expenseLimit.toString()

                saveButton.setOnAction {
                    if(expenseLimit.text.toDoubleOrNull() == null){
                        val errorAlert = Alert(Alert.AlertType.ERROR)
                        errorAlert.title = "Error"
                        errorAlert.headerText = "Please enter a valid number!"
                        errorAlert.showAndWait()
                        return@setOnAction
                    }
                    userInfo.expenseLimit = expenseLimit.text.toDouble()
                    val thread = Thread {
                        val dao = DAOFactory.getDAO(UserInfoModel::class.java) as DAO<UserInfoModel>
                        dao.update(userInfo)
                        Platform.runLater {
                            val successAlert = Alert(Alert.AlertType.INFORMATION)
                            successAlert.title = "Success"
                            successAlert.headerText = "Updated settings!"
                            successAlert.showAndWait()
                        }
                    }
                    thread.start()
                }
            }
        }
        thread.start()



    }

    override fun setModels(vararg models: Any) {
        models.forEach {
            if (it is UserInfoModel) {
                userInfo = it
            }
        }
    }
}