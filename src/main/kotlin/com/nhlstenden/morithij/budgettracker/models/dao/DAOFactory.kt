package com.nhlstenden.morithij.budgettracker.models.dao

import com.nhlstenden.morithij.budgettracker.models.MoneyRecordModel
import com.nhlstenden.morithij.budgettracker.models.UserInfoModel
import javafx.application.Platform

/**
 * A factory for creating DAO objects.
 */
object DAOFactory {

    fun <T> getDAO(modelClass: Class<T>): DAO<*> {
        if(Platform.isFxApplicationThread()) {
            throw IllegalStateException("DAO classes should not be called on the main thread due to database connection!")
        }

        return when (modelClass) {
            MoneyRecordModel::class.java -> MoneyRecordDAO()
            UserInfoModel::class.java -> UserInfoDAO()
            else -> throw IllegalArgumentException("Invalid model class: $modelClass")
        }
    }
}