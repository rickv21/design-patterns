package com.nhlstenden.morithij.budgettracker.models.dao

import com.nhlstenden.morithij.budgettracker.models.*
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
            BudgetModel::class.java -> BudgetDAO()
            ExpenseModel::class.java -> ExpenseDAO()
            UserInfoModel::class.java -> UserInfoDAO()
            TagModel::class.java -> TagDAO()
            ReminderModel::class.java -> ReminderDAO()
            else -> throw IllegalArgumentException("Invalid model class: $modelClass")
        }
    }
}