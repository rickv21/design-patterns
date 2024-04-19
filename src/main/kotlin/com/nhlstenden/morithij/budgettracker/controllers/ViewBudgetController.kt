package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.SceneManager
import com.nhlstenden.morithij.budgettracker.controllers.popUps.CreatePopUp
import com.nhlstenden.morithij.budgettracker.controllers.popUps.UpdatePopUp
import com.nhlstenden.morithij.budgettracker.models.BudgetModel
import com.nhlstenden.morithij.budgettracker.models.ExpenseModel
import com.nhlstenden.morithij.budgettracker.models.TestModel
import com.nhlstenden.morithij.budgettracker.models.UserInfoModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import com.nhlstenden.morithij.budgettracker.models.dao.ExpenseDAO
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
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
import javafx.util.Callback
import java.text.DecimalFormat
import java.time.LocalDate
import java.util.*

class ViewBudgetController : Controller() {
    private lateinit var budgetModel: BudgetModel

    @FXML
    private lateinit var goBackButton: Button

    @FXML
    private lateinit var overviewExpenseRecords: TableView<ExpenseModel>

    @FXML
    private lateinit var anchorPane: AnchorPane

    private lateinit var userInfo : UserInfoModel

    @FXML
    fun initialize() {
        val thread = Thread {
            val dao = DAOFactory.getDAO(UserInfoModel::class.java) as DAO<UserInfoModel>
            userInfo = dao.get(1) as UserInfoModel

            Platform.runLater {
                anchorPane.setOnKeyPressed { event ->
                    if (event.code == KeyCode.C || event.code == KeyCode.INSERT) {
                        CreatePopUp(userInfo, budgetModel)
                    }
                }
                goBackButton.setOnAction {
                    SceneManager.switchScene("overview", TestModel())
                }
            }
        }
        thread.start()
    }

    override fun setModels(vararg models: Any) {
        super.setModels(*models)
        budgetModel = models.firstOrNull() as BudgetModel
        if (budgetModel != null) {
            overviewExpensesTable()
        }
    }

    fun overviewExpensesTable() {
        // Get expense records for the given budget ID
        val thread = Thread {
            val expenseDAO = DAOFactory.getDAO(ExpenseModel::class.java) as ExpenseDAO
            val expenseRecords = expenseDAO.getAllByBudgetID(budgetModel.id)
            Platform.runLater {
                overviewExpenseRecords.items = FXCollections.observableArrayList(expenseRecords)
            }
        }

        // Expense column
        val expenseColumn = TableColumn<ExpenseModel, String>("Expense")
        expenseColumn.setCellValueFactory { cellData ->
            run {
                val currency = Currency.getInstance(budgetModel.currency).symbol
                SimpleStringProperty(currency + DecimalFormat("#,##0.00").format(cellData.value.money))
            }
        }

        expenseColumn.isResizable = false
        expenseColumn.prefWidth = 100.0

        // Date column
        val dateColumn = TableColumn<ExpenseModel, String>("Date")
        dateColumn.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.recordDate.toString()) }
        dateColumn.isResizable = false
        dateColumn.prefWidth = 150.0

        // Description column
        val descriptionColumn = TableColumn<ExpenseModel, String>("Description")
        descriptionColumn.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.description) }
        descriptionColumn.isResizable = false
        descriptionColumn.prefWidth = 383.0

        // Edit column
        val editColumn = TableColumn<ExpenseModel, ExpenseModel>("Edit")
        editColumn.isResizable = false
        editColumn.prefWidth = 75.0
        editColumn.cellFactory = Callback { _ ->
            object : TableCell<ExpenseModel, ExpenseModel>() {
                private val editButton = Button("Edit")
                private val buttonBox = HBox(editButton)

                init {
                    editButton.setOnAction {
                        val expense = tableView.items[index]
                        setupEditExpenseButtonAction(expense)
                    }
                    buttonBox.alignment = Pos.CENTER
                }

                override fun updateItem(item: ExpenseModel?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) {
                        graphic = null
                    } else {
                        graphic = buttonBox
                    }
                }
            }
        }

        overviewExpenseRecords.columns.setAll(expenseColumn, dateColumn, descriptionColumn, editColumn)

        thread.start()
    }

    private fun setupEditExpenseButtonAction(expense: ExpenseModel) {
        UpdatePopUp(userInfo, budgetModel, expense)
    }

    fun isDateFormatValid(dateString: String): Boolean {
        return try {
            LocalDate.parse(dateString)
            true // valid
        } catch (e: Exception) {
            false // invalid
        }
    }
}