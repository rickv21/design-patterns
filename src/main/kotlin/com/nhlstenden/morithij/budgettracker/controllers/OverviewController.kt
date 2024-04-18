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
import javafx.scene.input.KeyCode
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.util.Callback


class OverviewController : Controller(), Observer {
    lateinit var records: List<BudgetModel>
    lateinit var userInfo: UserInfoModel

    private val tagNamesMap = mutableMapOf<Int?, String?>()

    @FXML
    lateinit var addBudgetButton: Button

    @FXML
    lateinit var anchorPane : AnchorPane

    @FXML
    private lateinit var overviewBudgetRecords: TableView<BudgetModel>

    @FXML
    lateinit var totalMoneyLabel: Label

    fun initialize() {
        // setTotalAmount()
        setupTableView()
        setupAddBudgetButtonAction()

    }

    private fun setupTableView() {

//        // get budget money records
//        val thread = Thread {
//            val moneyRecordDAO = BudgetDAO()
//            val allRecords = moneyRecordDAO.getAll()
//            Platform.runLater {
//                overviewBudgetRecords.items = FXCollections.observableArrayList(allRecords)
//            }
//        }
//
//        // Get money value for budget column
//        val budgetColumn = TableColumn<BudgetModel, String>("Budget")
//        budgetColumn.setCellValueFactory { cellData -> SimpleStringProperty(formatMoney(cellData.value.totalBudget)) }
//
//        // Get tag name value for type column
//        val typeColumn = TableColumn<BudgetModel, String>("Type")
//
//        // Get record description value for description column
//        val descriptionColumn = TableColumn<BudgetModel, String>("Description")
//        descriptionColumn.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.description) }
//
//        // Action column
//        val actionColumn = TableColumn<BudgetModel, BudgetModel>("Action")
//        actionColumn.cellFactory = Callback { param ->
//            object : TableCell<BudgetModel, BudgetModel>() {
//                private val button = Button("Edit")
//
//                init {
//                    button.setOnAction {
//                        //To DO action
//                    }
//                    alignment = Pos.CENTER
//                }
//
//                override fun updateItem(item: BudgetModel?, empty: Boolean) {
//                    super.updateItem(item, empty)
//                    if (empty) {
//                        graphic = null
//                    } else {
//                        graphic = button
//                    }
//                }
//            }
//        }
//        overviewBudgetRecords.columns.setAll(budgetColumn, typeColumn, descriptionColumn, actionColumn)
//
//        thread.start()
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

            val label1 = Label("Budget:")
            val textField1 = TextField()
            layout.add(label1, 0, 0)
            layout.add(textField1, 1, 0)

            val label3 = Label("Description:")
            val textField3 = TextField()
            layout.add(label3, 0, 2)
            layout.add(textField3, 1, 2)

            val okButton = Button("Add")
            okButton.setOnAction {
                // TO DO add budgett
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