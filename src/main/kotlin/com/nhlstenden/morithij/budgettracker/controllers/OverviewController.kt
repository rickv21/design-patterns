package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.models.*
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import com.nhlstenden.morithij.budgettracker.models.dao.TotalMoneyDAO
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import java.lang.reflect.Field


class OverviewController : Controller(), Observer {
    lateinit var records: List<MoneyRecordModel>
    lateinit var total: TotalMoneyModel

    @FXML
    lateinit var totalMoneyLabel: Label

    fun initialize() {
        //TODO use MoneyRecordsDOA to get all records
        setTotalAmount()
    }

    override fun update(obj: Any) {
        if (obj is TotalMoneyModel) {
            val thread = Thread {
                val dao = DAOFactory.getDAO(TotalMoneyModel::class.java) as DAO<TotalMoneyModel>
                dao.update(total)
                Platform.runLater {
                    totalMoneyLabel.text = "Total Budget: $total"
                }
            }
            thread.start()
        }
    }

    fun setTotalAmount() {
        val thread = Thread {
            val dao = DAOFactory.getDAO(TotalMoneyModel::class.java) as DAO<TotalMoneyModel>
            //val record = dao.get(1)
            val record = TotalMoneyModel(1, 2.2)
            //TODO handle no total
            if (record != null) {
                total = record
                onTotalInitialized()
            }
        }
        thread.start()
    }

    private fun onTotalInitialized(){
        total.addObserver(this)
        totalMoneyLabel.text = "Total Budget:$total"
    }

    override fun setModels(vararg models: Any) {
        models.forEach {
            if (it is TotalMoneyModel) {
                total = it
            }
        }
    }

    fun handleLoadAction(actionEvent: ActionEvent) {
        total.setTotalAmount(5.5);
    }
}