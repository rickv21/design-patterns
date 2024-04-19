package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.SceneManager
import com.nhlstenden.morithij.budgettracker.controllers.popUps.CreatePopUp
import com.nhlstenden.morithij.budgettracker.controllers.popUps.DeletePopUp
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
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.util.Callback
import java.text.DecimalFormat
import java.time.LocalDate
import java.util.*

class ViewBudgetController : Controller(), Observer {
    private lateinit var budgetModel: BudgetModel

    @FXML
    private lateinit var goBackButton: Button

    @FXML
    private lateinit var overviewExpenseRecords: TableView<ExpenseModel>

    @FXML
    private lateinit var anchorPane: AnchorPane

    @FXML
    private lateinit var searchTerm : TextField

    @FXML
    private lateinit var totalBudgetLabel: Label

    @FXML
    private lateinit var currentBudgetLabel: Label

    @FXML
    private lateinit var titleLabel: Label

    private lateinit var userInfo : UserInfoModel

    private lateinit var expenseRecords : List<ExpenseModel>

    @FXML
    fun initialize() {
        val thread = Thread {
            val dao = DAOFactory.getDAO(UserInfoModel::class.java) as DAO<UserInfoModel>
            userInfo = dao.get(1) as UserInfoModel

            Platform.runLater {
                anchorPane.setOnKeyPressed { event ->
                    if (event.code == KeyCode.C || event.code == KeyCode.INSERT) {
                        CreatePopUp(userInfo, budgetModel, this)
                    }
                }
                goBackButton.setOnAction {
                    SceneManager.switchScene("overview", TestModel())
                }
            }
        }
        thread.start()
    }



    private fun setRecords(){
        val expensesThread = Thread {
            val expenseDAO = DAOFactory.getDAO(ExpenseModel::class.java) as ExpenseDAO
            expenseRecords = expenseDAO.getAllByBudgetID(budgetModel.id)
            updatePage(expenseRecords)
        }
        expensesThread.start()
    }
    override fun setModels(vararg models: Any) {
        super.setModels(*models)
        budgetModel = models.firstOrNull() as BudgetModel
        setRecords()
        titleLabel.text = "View ${budgetModel.description}"
    }

    private fun updatePage(records: List<ExpenseModel>) {
        // Get expense records for the given budget ID
        Platform.runLater {
            overviewExpenseRecords.items = FXCollections.observableArrayList(records)
            totalBudgetLabel.text = budgetModel.totalBudget.toString()
            currentBudgetLabel.text = budgetModel.currentBudget.toString()
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
        descriptionColumn.prefWidth = 300.0

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

        // Delete column
        val deleteColumn = TableColumn<ExpenseModel, ExpenseModel>("Delete")
        deleteColumn.isResizable = false
        deleteColumn.prefWidth = 75.0
        deleteColumn.cellFactory = Callback { _ ->
            object : TableCell<ExpenseModel, ExpenseModel>() {
                private val deleteButton = Button("Delete")
                private val buttonBox = HBox(deleteButton)

                init {
                    deleteButton.setOnAction {
                        val expense = tableView.items[index]
                        setupDeleteExpenseButtonAction(expense)
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

        Platform.runLater {
            overviewExpenseRecords.columns.setAll(expenseColumn, dateColumn, descriptionColumn, editColumn, deleteColumn)
        }
    }

    private fun setupEditExpenseButtonAction(expense: ExpenseModel) {
        UpdatePopUp(userInfo, budgetModel, expense, this)
    }

    private fun setupDeleteExpenseButtonAction(expense: ExpenseModel) {
        DeletePopUp(userInfo, budgetModel, expense, this)
    }

    fun search(actionEvent: ActionEvent){
        if(searchTerm.text.isEmpty()){
            updatePage(expenseRecords)
        }
        val result = mutableListOf<ExpenseModel>()
        expenseRecords.forEach{budget ->
            if(budget.description.contains(searchTerm.text)){
                result.add(budget)
            }
        }
        updatePage(result)
    }

    fun addExpense(actionEvent: ActionEvent){
        CreatePopUp(userInfo, budgetModel, this)
    }

    override fun update(obj: Any) {
        // When the notice from the publishers consists of a budgetID and a money value, the current budget should be updated
        if(obj is Pair<*, *>){
            val pair = obj as Pair<Int, Double>
            val currentBudgetThread = Thread{
                val dao = DAOFactory.getDAO(BudgetModel::class.java) as DAO<BudgetModel>
                val oldBudget = dao.get(pair.first)
                if(oldBudget != null){
                    val newBudget = BudgetModel(oldBudget.totalBudget, (oldBudget.currentBudget + pair.second), oldBudget.description, oldBudget.currency, pair.first)
                    dao.update(newBudget)
                    budgetModel = newBudget
                }
            }
            currentBudgetThread.start()

        }
        setRecords()
        updatePage(expenseRecords)
    }
}