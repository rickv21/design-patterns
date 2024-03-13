package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.models.*
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import com.nhlstenden.morithij.budgettracker.models.dao.TotalMoneyDAO
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import java.lang.reflect.Field


class OverviewController : Controller(), Observer {
    lateinit var records: List<MoneyRecordModel>
    lateinit var dao: DAO<TotalMoneyModel>

    @FXML
    lateinit var totalMoneyLabel: Label

    fun initialize() {
        //TODO use MoneyRecordsDOA to get all records

        dao = DAOFactory.getDAO(TotalMoneyModel::class.java) as DAO<TotalMoneyModel>
        totalMoneyLabel.text = "Total Budget: ${getTotal()}"
        if (dao is Observable) {
            (dao as Observable).addObserver(this)
        }
    }

    override fun update(obj: Any) {
        if (obj is TotalMoneyDAO) {
            totalMoneyLabel.text = "Total Budget: ${getTotal()}"
        }
    }

    fun getTotal() : TotalMoneyModel{
       // dao.save(TotalMoneyModel(1, 2.2))
       // val record = dao.get(1)
        return TotalMoneyModel(1, 2.2)
    }

    override fun setModels(vararg models: Any) {
        val fields: Array<Field> = this.javaClass.declaredFields
        for (field in fields) {
            for (model in models) {
                if (model.javaClass == field.type) {
                    field.isAccessible = true
                    field.set(this, model)
                }
            }
        }
    }

    fun handleLoadAction(actionEvent: ActionEvent) {
        dao.update(TotalMoneyModel(1, 3.2))
    }
}