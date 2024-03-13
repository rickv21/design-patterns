package com.nhlstenden.morithij.budgettracker.models.dao

import com.nhlstenden.morithij.budgettracker.models.*

/**
 * A factory for creating DAO objects.
 */
object DAOFactory {

    fun <T> getDAO(modelClass: Class<T>): DAO<*> {
        return when (modelClass) {
            MoneyRecordModel::class.java -> MoneyRecordDAO()
            TotalMoneyModel::class.java -> TotalMoneyDAO()
            else -> throw IllegalArgumentException("Invalid model class: $modelClass")
        }
    }
}