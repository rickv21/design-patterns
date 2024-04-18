package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.SceneManager
import com.nhlstenden.morithij.budgettracker.models.BudgetModel
import com.nhlstenden.morithij.budgettracker.models.ExpenseModel
import com.nhlstenden.morithij.budgettracker.models.Model
import com.nhlstenden.morithij.budgettracker.models.TestModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import com.nhlstenden.morithij.budgettracker.models.dao.ExpenseDAO
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.util.Callback

class ViewBudgetController : Controller() {
    private lateinit var budgetModel: BudgetModel

    @FXML
    private lateinit var goBackButton: Button

    @FXML
    private lateinit var overviewExpenseRecords: TableView<ExpenseModel>


    @FXML
    fun initialize() {
        overviewExpensesTable()

        goBackButton.setOnAction {
            SceneManager.switchScene("overview", TestModel())
        }
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
        expenseColumn.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.money.toString()) }
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
        descriptionColumn.prefWidth = 385.0

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
                        //edit pop up
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


}