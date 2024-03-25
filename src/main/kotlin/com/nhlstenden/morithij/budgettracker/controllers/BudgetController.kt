package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.models.MoneyRecordModel
import com.nhlstenden.morithij.budgettracker.models.TagModel
import com.nhlstenden.morithij.budgettracker.models.dao.MoneyRecordDAO
import com.nhlstenden.morithij.budgettracker.models.dao.TagDAO
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ColorPicker
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
    lateinit var addTagTextfield: TextField
    @FXML
    lateinit var addTagColor: ColorPicker

    fun addBudgetRecord()
    {
        val budget = addBudgetTextfield.text
        val description = addDescriptionTextfield.text
        val tag = addTagTextfield.text

        if (budget.isBlank() || description.isBlank() || tag.isBlank()) {
            val alert = Alert(Alert.AlertType.ERROR)
            alert.title = "Error"
            alert.headerText = "Not all the fields are filled"
            alert.contentText = "Please fill all the fields in"
            alert.showAndWait()
        } else {
            val moneyRecordDAO = MoneyRecordDAO()
            val tagDAO = TagDAO()

            val tagColorHex = addTagColor.value

            val tag = TagModel(
                    id = 0,                              // Database will generate an id automatically
                    tag_name = tag,
                    hexcode = String.format("#%02X%02X%02X", (tagColorHex.red * 255).toInt(), (tagColorHex.green * 255).toInt(), (tagColorHex.blue * 255).toInt())
            )

            val tagId = tagDAO.save(tag)

            // New moneyRecord object
            val moneyRecord = MoneyRecordModel(
                    id = 0,                             // Database will generate an id automatically
                    money = budget.toDouble(),
                    recordDate = LocalDateTime.now(),
                    description = description,
                    tagId = tagId
            )

            // Save record
            moneyRecordDAO.save(moneyRecord)

            // Success message
            val successAlert = Alert(Alert.AlertType.INFORMATION)
            successAlert.title = "Success"
            successAlert.headerText = "Added budget record"
            successAlert.showAndWait()
        }
    }

    override fun setModels(vararg models: Any) {

    }
}