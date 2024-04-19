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
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.util.Callback
import java.time.LocalDate
import java.util.ArrayList


class OverviewController : Controller(), Observer {
    lateinit var records: List<BudgetModel>
    lateinit var userInfo: UserInfoModel

    private val tagNamesMap = mutableMapOf<Int?, String?>()

    @FXML
    private lateinit var overviewBudgetRecords: TableView<BudgetModel>

    @FXML
    lateinit var totalMoneyLabel: Label

    @FXML
    private lateinit var reminderBanner : AnchorPane

    @FXML
    private lateinit var reminderLabel : Label

    fun initialize() {
        // setTotalAmount()
        setupTableView()

        val thread = Thread {
            val reminderDAO = DAOFactory.getDAO(ReminderModel::class.java) as DAO<ReminderModel>
            val allReminders = reminderDAO.getAll()
            val today = LocalDate.now()
            val reminders = ArrayList<ReminderModel>()
            for(reminder in allReminders){
                if(reminder.remindDate.isEqual(today)){
                    reminders.add(reminder)
                }
            }
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

    private fun setupTableView() {
        // get budget money records
        val thread = Thread {
            val moneyRecordDAO = DAOFactory.getDAO(BudgetModel::class.java) as DAO<BudgetModel>
            val allRecords = moneyRecordDAO.getAll()
            Platform.runLater {
                overviewBudgetRecords.items = FXCollections.observableArrayList(allRecords)
            }
        }

        // Get money value for budget column
        val budgetColumn = TableColumn<BudgetModel, String>("Budget")
        budgetColumn.setCellValueFactory { cellData -> SimpleStringProperty(formatMoney(cellData.value.totalBudget)) }

        // Get tag name value for type column
        val typeColumn = TableColumn<BudgetModel, String>("Type")

        // Get record description value for description column
        val descriptionColumn = TableColumn<BudgetModel, String>("Description")
        descriptionColumn.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.description) }

        // Action column
        val actionColumn = TableColumn<BudgetModel, BudgetModel>("Action")
        actionColumn.cellFactory = Callback { param ->
            object : TableCell<BudgetModel, BudgetModel>() {
                private val button = Button("Edit")

                init {
                    button.setOnAction {
                        //To DO action
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
        overviewBudgetRecords.columns.setAll(budgetColumn, typeColumn, descriptionColumn, actionColumn)

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
                dao.update(userInfo)
                Platform.runLater {
                    totalMoneyLabel.text = "Total Budget: ${formatMoney(userInfo.totalMoney)}"
                }
            }
            thread.start()
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
    }

    fun onAddBudgetClick() {
        SceneManager.switchScene("add", TestModel())

    }
}