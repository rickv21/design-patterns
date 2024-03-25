package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.models.MoneyRecordModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.TextField
import java.time.LocalDateTime

/**
 * A controller class for testing SQLite functionality.
 */
class SQLTestController() : Controller() {

    @FXML
    lateinit var idField: TextField

    @FXML
    lateinit var inputField: TextField

    @FXML
    lateinit var label: Label

    var id = -1

    override val title = "SQL Test"

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
        val record = MoneyRecordModel(0, 0.0, LocalDateTime.now(), inputField.text, "EUR", 0)

        val thread = Thread {
            val dao = DAOFactory.getDAO(MoneyRecordModel::class.java) as DAO<MoneyRecordModel>
            id = dao.create(record)
            dao.close()
            println("Saved record with id: $id")
            Platform.runLater{
                label.text = "Saved record with id: $id"
            }
        }
        thread.start()
    }

    @FXML
    fun handleLoadAction(event: ActionEvent) {
        val id = idField.text.toIntOrNull()
        if(id == null){
            label.text = "Invalid ID"
            return
        }

        val thread = Thread {
            val dao = DAOFactory.getDAO(MoneyRecordModel::class.java) as DAO<MoneyRecordModel>
            val record = dao.get(id)
            dao.close()
            println("Loaded record: $record")
            Platform.runLater{
                label.text = record.toString()
            }
        }
        thread.start()
    }

    override fun setModels(vararg models: Any) {
        // No models needed
    }
}