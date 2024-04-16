package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.models.MoneyRecordModel
import com.nhlstenden.morithij.budgettracker.models.TagModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import com.nhlstenden.morithij.budgettracker.models.dao.MoneyRecordDAO
import com.nhlstenden.morithij.budgettracker.models.dao.TagDAO
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ColorPicker
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import java.lang.reflect.Field
import java.time.LocalDateTime

class BudgetController() : Controller() {
    @FXML
    lateinit var addBudgetButton: Button

    @FXML
    lateinit var addBudgetTextfield: TextField

    @FXML
    lateinit var addDescriptionTextfield: TextField

    @FXML
    lateinit var addTagCombobox: ComboBox<String>

    @FXML
    fun initialize() {

        val thread = Thread {
            val dao = DAOFactory.getDAO(TagModel::class.java) as DAO<TagModel>
            val tags = dao.getAll()

            for (tag in tags) {
                addTagCombobox.items.add(tag.tag_name)
            }
        }

        thread.start()

//        val thread = Thread {
////            val tag1 = TagModel(2, "CAR", "#EF3525")
////            val tag2 = TagModel(3, "Rent", "#DW34E53")
////
//            val dao = DAOFactory.getDAO(TagModel::class.java) as DAO<TagModel>
////            dao.create(tag2)
//
//            val tags = dao.getAll()
//            for (tag in tags) {
//                addTagCombobox.items.add(tag.tag_name)
//            }
//        }

    }

    fun addBudgetRecord() {
        val budget = addBudgetTextfield.text
        val description = addDescriptionTextfield.text

        // Check if everything is filled
        if (budget.isBlank() || description.isBlank() || addTagCombobox.selectionModel.selectedItem == null) {
            val alert = Alert(Alert.AlertType.ERROR)
            alert.title = "Error"
            alert.headerText = "Not all the fields are filled"
            alert.contentText = "Please fill all the fields in"
            alert.showAndWait()
            return
        }

        // Check if budget is a valid number
        if (!budget.matches("-?\\d+(\\.\\d+)?".toRegex())) {
            val alert = Alert(Alert.AlertType.ERROR)
            alert.title = "Error"
            alert.headerText = "Invalid Budget"
            alert.contentText = "Budget must be a valid number"
            alert.showAndWait()
            return
        }

        val selectedTagName = addTagCombobox.selectionModel.selectedItem

        val thread = Thread {
            // Retrieve the tag_id corresponding to the selected tag_name
            val dao = DAOFactory.getDAO(TagModel::class.java) as DAO<TagModel>
            val selectedTag = dao.getAll().find { it.tag_name == selectedTagName }
            dao.close()

            // Ensure selected tag is not null
            if (selectedTag != null) {
                // Create a new moneyRecord object with the selected tag's id
                val moneyRecord = MoneyRecordModel(
                        money = budget.toDouble(),
                        recordDate = LocalDateTime.now(),
                        description = description,
                        tagId = selectedTag.id
                )

                // Save record
                val dao = DAOFactory.getDAO(MoneyRecordModel::class.java) as DAO<MoneyRecordModel>
                val moneyRecordId = dao.create(moneyRecord)
                dao.close()

                // Success message
                Platform.runLater {
                    val successAlert = Alert(Alert.AlertType.INFORMATION)
                    successAlert.title = "Success"
                    successAlert.headerText = "Added budget record"
                    successAlert.showAndWait()
                }

                println("Saved record with id: $moneyRecordId")

            } else {
                // Handle case where selected tag is not found
                println("Error: Selected tag not found")
            }
        }
        thread.start()
    }
}