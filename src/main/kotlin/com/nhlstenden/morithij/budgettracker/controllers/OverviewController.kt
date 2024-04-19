package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.SceneManager
import com.nhlstenden.morithij.budgettracker.models.*
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import com.nhlstenden.morithij.budgettracker.models.dao.BudgetDAO
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.util.Callback
import java.time.LocalDate
import java.util.ArrayList


class OverviewController : Controller(), Observer {
    lateinit var userInfo: UserInfoModel

    private val tagNamesMap = mutableMapOf<Int?, String?>()

    @FXML
    private lateinit var overviewBudgetRecords: TableView<BudgetModel>

    @FXML
    lateinit var addBudgetButton: Button

    @FXML
    lateinit var totalMoneyLabel: Label

    @FXML
    lateinit var searchTerm: TextField

    private lateinit var allRecords : List<BudgetModel>

    @FXML
    private lateinit var reminderBanner : AnchorPane

    @FXML
    private lateinit var reminderLabel : Label

    fun initialize() {
        // setTotalAmount()


        val thread = Thread {
            val moneyRecordDAO = DAOFactory.getDAO(BudgetModel::class.java) as DAO<BudgetModel>
            val allRecords = moneyRecordDAO.getAll()
            this.allRecords = allRecords

            val reminderDAO = DAOFactory.getDAO(ReminderModel::class.java) as DAO<ReminderModel>
            val allReminders = reminderDAO.getAll()
            val today = LocalDate.now()
            val reminders = ArrayList<ReminderModel>()
            for(reminder in allReminders){
                if(reminder.remindDate.isEqual(today)){
                    reminders.add(reminder)
                }
            }
            setupTableView(allRecords)
            setupAddBudgetButtonAction()

            if(reminders.isNotEmpty()){
                Platform.runLater {
                    reminderBanner.isVisible = true
                    if(reminders.count() > 1){
                        reminderLabel.text = "You have ${reminders.count()} reminders today: "
                    } else {
                        reminderLabel.text = "You have a reminder today: "
                    }
                    for((count, reminder) in reminders.withIndex()){
                        if(count == 0){
                            reminderLabel.text += reminder.description
                        } else {
                            reminderLabel.text = reminderLabel.text + ", " + reminder.description
                        }
                    }
                    reminderLabel.text += "."

                }
            }
        }
        thread.start()
    }

    private fun setupTableView(allRecords: List<BudgetModel>) {
        // get budget money records
        val thread = Thread {
            Platform.runLater {
                overviewBudgetRecords.items = FXCollections.observableArrayList(allRecords)
            }
        }

        // Get money value for budget column
        val budgetColumn = TableColumn<BudgetModel, String>("Budget")
        budgetColumn.setCellValueFactory { cellData -> SimpleStringProperty(formatMoney(cellData.value.totalBudget)) }
        budgetColumn.isResizable = false
        budgetColumn.prefWidth = 100.0

        // Get record description value for description column
        val descriptionColumn = TableColumn<BudgetModel, String>("Description")
        descriptionColumn.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.description) }
        descriptionColumn.isResizable = false
        descriptionColumn.prefWidth = 220.0

        // Action column
        val actionColumn = TableColumn<BudgetModel, BudgetModel>("Action")
        actionColumn.isResizable = false
        actionColumn.prefWidth = 100.0

        actionColumn.cellFactory = Callback { _ ->
            object : TableCell<BudgetModel, BudgetModel>() {
                private val viewButton = Button("View")
                private val buttonBox = HBox(viewButton)

                init {
                    viewButton.setOnAction {
                        val budget = tableView.items[index]
                        SceneManager.switchScene("viewbudget", budget)
                    }
                    buttonBox.alignment = Pos.CENTER
                }

                override fun updateItem(item: BudgetModel?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) {
                        graphic = null
                    } else {
                        graphic = buttonBox
                    }
                }
            }
        }
        val deleteColumn = TableColumn<BudgetModel, BudgetModel>("Delete")
        deleteColumn.cellFactory = Callback { param ->
            object : TableCell<BudgetModel, BudgetModel>() {
                private val button = Button("Delete")

                init {
                    button.setOnAction {
                        val budgetModel = tableView.items[index]
                        val thread = Thread {
                            val dao = DAOFactory.getDAO(BudgetModel::class.java) as DAO<BudgetModel>
                            dao.addObserver(this@OverviewController)
                            dao.delete(budgetModel.id)
                        }
                        thread.start()
                    }
                    alignment = Pos.CENTER
                }

                override fun updateItem(item: BudgetModel?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) {
                        graphic = null
                    } else {
                        graphic = button
                    }
                }
            }
        }
        overviewBudgetRecords.columns.setAll(budgetColumn, descriptionColumn, actionColumn, deleteColumn)

        thread.start()
    }


    private fun getTagName(tagId: Int?): String? {
        // Check if tag name already exists in the map, this to prevent continuous calls because of setCellValueFactory
        if (tagNamesMap.containsKey(tagId)) {
            return tagNamesMap[tagId]
        }

        var tagName: String? = null
        val dao = DAOFactory.getDAO(TagModel::class.java) as DAO<TagModel>
        val tag = dao.get(tagId ?: return null)
        tagName = tag?.tag_name

        // if not exist save
        tagNamesMap[tagId] = tagName

        return tagName
    }

    override fun update(obj: Any) {
        if (obj is UserInfoModel) {
            val thread = Thread {
                val dao = DAOFactory.getDAO(UserInfoModel::class.java) as DAO<UserInfoModel>
                dao.addObserver(this)
                dao.update(userInfo)
                Platform.runLater {
                    totalMoneyLabel.text = "Total Budget: ${formatMoney(userInfo.totalMoney)}"
                }
            }
            thread.start()
        }else if (obj is List<*>) {
            val budgetModels = obj.filterIsInstance<BudgetModel>()
            allRecords = budgetModels
            setupTableView(allRecords)
        }
    }

    private fun setTotalAmount() {
        val thread = Thread {
            val dao = DAOFactory.getDAO(UserInfoModel::class.java) as DAO<UserInfoModel>
            val record = dao.get(1)
            //TODO: handle missing record.
            if (record != null) {
                userInfo = record
                onTotalInitialized()
            }
        }
        thread.start()
    }

    private fun onTotalInitialized() {
        userInfo.addObserver(this)
        totalMoneyLabel.text = "Total Budget: ${formatMoney(userInfo.totalMoney)}"
    }

    private fun formatMoney(value: Double): String {
        return String.format("€%.2f", value)
    }

    override fun setModels(vararg models: Any) {
        models.forEach {
            if (it is UserInfoModel) {
                userInfo = it
            }
        }
    }

    fun handleLoadAction(actionEvent: ActionEvent) {
        val thread = Thread {
            val daoBudgets = DAOFactory.getDAO(BudgetModel::class.java) as DAO<BudgetModel>
            daoBudgets.addObserver(this)
            daoBudgets.create(BudgetModel(50.0, 40.0, "test"))
            val daoExpenses = DAOFactory.getDAO(ExpenseModel::class.java) as DAO<ExpenseModel>
            daoExpenses.create(ExpenseModel(1, 50.0, LocalDate.now(), "test"))
        }
        thread.start()
    }

    fun search(actionEvent: ActionEvent){
        val result = mutableListOf<BudgetModel>()
        allRecords.forEach{budget ->
            if(budget.description.contains(searchTerm.text)){
                result.add(budget)
            }
        }
        setupTableView(result)
    }

    private fun setupAddBudgetButtonAction() {
        addBudgetButton.setOnAction {
            val popup = Stage()
            popup.initModality(Modality.APPLICATION_MODAL)
            popup.title = "Add Budget"
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

            val label1 = Label("Total Budget:")
            val textFieldBudget = TextField()
            layout.add(label1, 0, 0)
            layout.add(textFieldBudget, 1, 0)

            val label2 = Label("Description:")
            val textFieldDescription = TextField()
            layout.add(label2, 0, 1)
            layout.add(textFieldDescription, 1, 1)

            val okButton = Button("Add")
            okButton.setOnAction {
                val totalBudget = textFieldBudget.text.toDoubleOrNull()
                val description = textFieldDescription.text

                if (totalBudget == null || description.isEmpty()) {
                    val errorAlert = Alert(Alert.AlertType.ERROR)
                    errorAlert.title = "Error"
                    errorAlert.headerText = "Please fill in all the fields!"
                    errorAlert.showAndWait()
                } else {
                    val newBudget = BudgetModel(totalBudget, totalBudget, description)

                    val thread = Thread {
                        val dao = DAOFactory.getDAO(BudgetModel::class.java) as DAO<BudgetModel>
                        val id = dao.create(newBudget)

                        Platform.runLater {
                            if (id != -1) {
                                val successAlert = Alert(Alert.AlertType.INFORMATION)
                                successAlert.title = "Success"
                                successAlert.headerText = "Budget added successfully"
                                successAlert.showAndWait()
                                popup.close()
                            } else {
                                val errorAlert = Alert(Alert.AlertType.ERROR)
                                errorAlert.title = "Error"
                                errorAlert.headerText = "Failed to add budget"
                                errorAlert.showAndWait()
                            }
                        }
                    }
                    thread.start()
                }
            }

            val cancelButton = Button("Cancel")
            cancelButton.setOnAction {
                popup.close()
            }

            val buttonBox = HBox(10.0)
            buttonBox.alignment = Pos.CENTER
            buttonBox.children.addAll(okButton, cancelButton)
            layout.add(buttonBox, 0, 2, 2, 1)

            val scene = Scene(layout, 300.0, 150.0)
            popup.scene = scene

            popup.showAndWait()
        }
    }
}