package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.models.*
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import javafx.fxml.FXML
import javafx.scene.control.Label
import java.lang.reflect.Field


class OverviewController : Controller() {
    lateinit var records : List<MoneyRecordModel>
    @FXML
    lateinit var totalMoneyLabel: Label

    fun initialize() {
        //TODO use MoneyRecordsDOA to get all records

        val dao = DAOFactory.getDAO(TotalMoneyModel::class.java) as DAO<TotalMoneyModel>
        //TODO take this out when real users exist
//        dao.save(TotalMoneyModel(1, 2.2))
//        val record = dao.get(1)
        val record = TotalMoneyModel(1, 2.2)
        totalMoneyLabel.text = "Total Budget: $record"
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
}