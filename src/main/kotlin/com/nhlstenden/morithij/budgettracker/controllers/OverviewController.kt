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

class OverviewController : Controller(), Observer {
    lateinit var userInfo: UserInfoModel

    private val tagNamesMap = mutableMapOf<Int?, String?>()

    @FXML
    lateinit var addBudgetButton: Button

    @FXML
    lateinit var anchorPane: AnchorPane

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
        // Get budget money records
        val thread = Thread {
            val budgetDAO = DAOFactory.getDAO(BudgetModel::class.java) as DAO<BudgetModel>
            val allRecords = budgetDAO.getAll()
            Platform.runLater {
                overviewBudgetRecords.items = FXCollections.observableArrayList(allRecords)
            }
        }

        // budget column
        val budgetColumn = TableColumn<BudgetModel, String>("Budget")
        budgetColumn.setCellValueFactory { cellData -> SimpleStringProperty(formatMoney(cellData.value.totalBudget)) }
        budgetColumn.isResizable = false
        budgetColumn.prefWidth = 100.0

        // description column
        val descriptionColumn = TableColumn<BudgetModel, String>("Description")
        descriptionColumn.setCellValueFactory { cellData -> SimpleStringProperty(cellData.value.description) }
        descriptionColumn.isResizable = false
        descriptionColumn.prefWidth = 346.0

        // action column
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

        overviewBudgetRecords.columns.setAll(budgetColumn, descriptionColumn, actionColumn)

        thread.start()
    }

    private fun getSelectedBudgetModel(): BudgetModel? {
        val selectedIndex = overviewBudgetRecords.selectionModel.selectedIndex
        return if (selectedIndex != -1) {
            overviewBudgetRecords.items[selectedIndex]
        } else {
            null
        }
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
}