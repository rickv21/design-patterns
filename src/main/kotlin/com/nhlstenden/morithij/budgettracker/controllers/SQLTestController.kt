package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.models.MoneyRecordModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.TextField
import java.time.LocalDateTime

/**
 * A controller class for testing SQLite functionality.
 */
class SQLTestController {

    @FXML
    lateinit var idField: TextField

    @FXML
    lateinit var inputField: TextField

    @FXML
    lateinit var label: Label

    var id = -1

    @FXML
    fun initialize(){
        inputField.promptText = "Input"
        idField.promptText = "ID"

        idField.addEventFilter(javafx.scene.input.KeyEvent.KEY_TYPED) {
            val c = it.character.firstOrNull()
            if(c != null && !c.isDigit()){
                it.consume()
            }
        }
    }

    @FXML
    fun handleSaveAction(event: ActionEvent) {
        val record = MoneyRecordModel(0, 0.0, LocalDateTime.now(), inputField.text)
        val dao = DAOFactory.getDAO(MoneyRecordModel::class.java) as DAO<MoneyRecordModel>
        id = dao.save(record)
        println("Saved record with id: $id")
        label.text = "Saved record with id: $id"
    }

    @FXML
    fun handleLoadAction(event: ActionEvent) {
        val dao = DAOFactory.getDAO(MoneyRecordModel::class.java) as DAO<MoneyRecordModel>
        val id = idField.text.toIntOrNull()
        if(id == null){
            label.text = "Invalid ID"
            return
        }
        val record = dao.get(id)
        label.text = record.toString()
        println("Loaded record: $record")
    }
}