package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.SceneManager
import com.nhlstenden.morithij.budgettracker.models.*
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import com.nhlstenden.morithij.budgettracker.models.dao.MoneyRecordDAO
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView


class OverviewController : Controller(), Observer {
    lateinit var records: List<MoneyRecordModel>
    lateinit var userInfo: UserInfoModel

    private val tagNamesMap = mutableMapOf<Int?, String?>()

    @FXML
    private lateinit var overviewBudgetRecords: TableView<MoneyRecordModel>

    @FXML
    lateinit var totalMoneyLabel: Label

    fun initialize() {
        // setTotalAmount()
        setupTableView()
    }

    private fun setupTableView() {
        // get budget money records
        val thread = Thread {
            val moneyRecordDAO = MoneyRecordDAO()
            val allRecords = moneyRecordDAO.getAll()
            Platform.runLater {
                overviewBudgetRecords.items = FXCollections.observableArrayList(allRecords)
            }
        }

        // Get money value for budget column
        val budgetColumn = TableColumn<MoneyRecordModel, String>("Budget")
        budgetColumn.setCellValueFactory { cellData -> SimpleStringProperty(formatMoney(cellData.value.money)) }

        // Get tag name value for type column
        val typeColumn = TableColumn<MoneyRecordModel, String>("Type")
        typeColumn.setCellValueFactory { cellData ->
            val tagId = cellData.value.tagId
            val tagNameProperty = SimpleStringProperty()

            if (tagNamesMap.containsKey(tagId)) {
                tagNameProperty.set(tagNamesMap[tagId] ?: "")
            } else {
                val thread = Thread {
                    val tagName = getTagName(tagId)

                    // runLater is used so the property change happens in the javaFX thread.
                    Platform.runLater {
                        tagNameProperty.set(tagName ?: "")
                    }
                }
                thread.start()
            }

            tagNameProperty
        }

        // Get record description value for description column
        val descriptionColumn = TableColumn<MoneyRecordModel, String>("Description")
        descriptionColumn.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.description) }

        overviewBudgetRecords.columns.setAll(budgetColumn, typeColumn, descriptionColumn)

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
        userInfo.setTotalAmount(5.5);
    }

    fun onAddBudgetClick() {
        SceneManager.switchScene("add", TestModel())

    }
}