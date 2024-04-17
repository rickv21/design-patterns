package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.SceneManager
import com.nhlstenden.morithij.budgettracker.controllers.popUps.CreatePopUp
import com.nhlstenden.morithij.budgettracker.controllers.popUps.DeletePopUp
import com.nhlstenden.morithij.budgettracker.controllers.popUps.UpdatePopUp
import com.nhlstenden.morithij.budgettracker.models.BudgetModel
import com.nhlstenden.morithij.budgettracker.models.ExpenseModel
import com.nhlstenden.morithij.budgettracker.models.UserInfoModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Modality
import javafx.stage.Stage
import java.time.LocalDateTime

class ExpenseController() : Controller() {

    @FXML
    lateinit var anchorPane : AnchorPane

    @FXML
    lateinit var button: Button

    private lateinit var userInfo : UserInfoModel

    @FXML
    fun initialize() {
        val thread = Thread {
            val dao = DAOFactory.getDAO(UserInfoModel::class.java) as DAO<UserInfoModel>
            userInfo = dao.get(1) as UserInfoModel
            Platform.runLater{
                anchorPane.setOnKeyPressed { event ->
                    if (event.code == KeyCode.C) {
                        CreatePopUp(userInfo)
                    }else if(event.code == KeyCode.U){
                        UpdatePopUp(userInfo)
                    }else if(event.code == KeyCode.D){
                        DeletePopUp(userInfo)
                    }
                }
            }
        }
        thread.start()





    }

}