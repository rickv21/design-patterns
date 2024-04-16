package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.models.UserInfoModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextField


class SettingsController() : Controller() {

    private lateinit var userInfo: UserInfoModel

    @FXML
    lateinit var expenseLimit: TextField

    @FXML
    lateinit var saveButton: Button

    @FXML
    fun initialize() {
        val thread = Thread {
            val dao = DAOFactory.getDAO(UserInfoModel::class.java) as DAO<UserInfoModel>
            userInfo = dao.get(1)!!;
            println("Limt: " + userInfo.expenseLimit)
            Platform.runLater {
                expenseLimit.text = userInfo.expenseLimit.toString()

                expenseLimit.textProperty().addListener { _, _, newValue ->
                    if (!newValue.matches("\\d*".toRegex())) {
                        expenseLimit.text = newValue.replace("\\D".toRegex(), "")
                    }
                }

                saveButton.setOnAction {
                    userInfo.expenseLimit = expenseLimit.text.toDouble()
                    val thread = Thread {
                        val dao = DAOFactory.getDAO(UserInfoModel::class.java) as DAO<UserInfoModel>
                        dao.update(userInfo)
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