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
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.util.Callback
import java.time.LocalDate
import java.util.regex.Pattern

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
        val popup = Stage()
        popup.initModality(Modality.APPLICATION_MODAL)
        popup.title = "Edit Expense"
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

        val label1 = Label("Expense:")
        val textFieldExpense = TextField(expense.money.toString())
        layout.add(label1, 0, 0)
        layout.add(textFieldExpense, 1, 0)

        val label2 = Label("Date:")
        val textFieldDate = TextField(expense.recordDate.toString())
        layout.add(label2, 0, 1)
        layout.add(textFieldDate, 1, 1)

        val label3 = Label("Description:")
        val textFieldDescription = TextField(expense.description)
        layout.add(label3, 0, 2)
        layout.add(textFieldDescription, 1, 2)

        val updateButton = Button("Save")
        updateButton.setOnAction {
            val thread = Thread {
                val newMoney = textFieldExpense.text.toDouble()
                val newRecordDate = textFieldDate.text
                val newDescription = textFieldDescription.text

                if (!isDateFormatValid(newRecordDate)) {
                    Platform.runLater {
                        val errorAlert = Alert(Alert.AlertType.ERROR)
                        errorAlert.title = "Error"
                        errorAlert.headerText = "Failed to update expense, fill date in as YYYY-mm-DD"
                        errorAlert.showAndWait()
                    }
                    return@Thread
                }

                val parsedRecordDate = LocalDate.parse(textFieldDate.text)

                // update
                val updatedExpense = ExpenseModel(
                        expense.budgetID,
                        newMoney,
                        parsedRecordDate,
                        newDescription,
                        expense.id
                )

                val dao = DAOFactory.getDAO(ExpenseModel::class.java) as DAO<ExpenseModel>

                try {
                    dao.update(updatedExpense)

                    Platform.runLater {
                        val successAlert = Alert(Alert.AlertType.INFORMATION)
                        successAlert.title = "Success"
                        successAlert.headerText = "Expense updated successfully"
                        successAlert.showAndWait()
                        popup.close()
                    }
                } catch (e: Exception) {
                    Platform.runLater {
                        val errorAlert = Alert(Alert.AlertType.ERROR)
                        errorAlert.title = "Error"
                        errorAlert.headerText = "Failed to update expense: ${e.message}"
                        errorAlert.showAndWait()
                    }
                }
            }
            thread.start()
        }

        val cancelButton = Button("Cancel")
        cancelButton.setOnAction {
            popup.close()
        }

        val buttonBox = HBox(10.0)
        buttonBox.alignment = Pos.CENTER
        buttonBox.children.addAll(updateButton, cancelButton)
        layout.add(buttonBox, 0, 3, 2, 1)

        val scene = Scene(layout, 300.0, 200.0)
        popup.scene = scene

        popup.showAndWait()
    }

    fun isDateFormatValid(dateString: String): Boolean {
        return try {
            LocalDate.parse(dateString)
            true // Datum formaat is geldig
        } catch (e: Exception) {
            false // Datum formaat is ongeldig
        }
    }
}