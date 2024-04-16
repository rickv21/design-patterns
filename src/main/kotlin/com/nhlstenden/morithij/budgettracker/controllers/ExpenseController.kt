package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.SceneManager
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
                    if (event.code == KeyCode.INSERT) {
                        val popup = Stage()
                        popup.initModality(Modality.APPLICATION_MODAL)
                        popup.title = "Add Expense"
                        popup.isResizable = false
                        popup.minWidth = 400.0
                        popup.maxWidth = 400.0
                        popup.minHeight = 200.0
                        popup.maxHeight = 200.0

                        val layout = GridPane()
                        layout.alignment = Pos.CENTER
                        layout.hgap = 10.0
                        layout.vgap = 10.0
                        layout.padding = Insets(25.0, 25.0, 25.0, 25.0)

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

                            if(textField1.text.isEmpty() || textField2.value == null || textField3.text.isEmpty()){
                                val errorAlert = Alert(Alert.AlertType.ERROR)
                                errorAlert.title = "Error"
                                errorAlert.headerText = "Please fill in all fields!"
                                errorAlert.showAndWait()
                                return@setOnAction
                            }

                            if(textField1.text.toDouble() > userInfo.expenseLimit){
                                val errorAlert = Alert(Alert.AlertType.ERROR)
                                errorAlert.title = "Error"
                                errorAlert.headerText = "The expense you entered is more then your set limit!"
                                errorAlert.showAndWait()
                                return@setOnAction
                            }

                            val thread = Thread {
                                val dao = DAOFactory.getDAO(ExpenseModel::class.java) as DAO<ExpenseModel>
                                dao.create(ExpenseModel(1, textField1.text.toDouble(), textField2.value, textField3.text))
                                Platform.runLater{
                                    val successAlert = Alert(Alert.AlertType.INFORMATION)
                                    successAlert.title = "Success"
                                    successAlert.headerText = "Added expense!"
                                    successAlert.showAndWait()
                                    popup.close()
                                }
                            }
                            thread.start()


                            popup.close()
                        }

                        val cancelButton = Button("Cancel")
                        cancelButton.setOnAction {
                            popup.close()
                        }

                        val buttonBox = HBox(10.0)
                        buttonBox.alignment = Pos.CENTER
                        buttonBox.children.addAll(okButton, cancelButton)
                        layout.add(buttonBox, 0, 3, 2, 1)

                        val scene = Scene(layout, 300.0, 200.0)
                        popup.scene = scene


                        popup.showAndWait()
                    }
                }
            }
        }
        thread.start()





    }

}