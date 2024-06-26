package com.nhlstenden.morithij.budgettracker.models.dao

import com.nhlstenden.morithij.budgettracker.controllers.Observer
import com.nhlstenden.morithij.budgettracker.controllers.commands.DeleteCommand
import com.nhlstenden.morithij.budgettracker.models.BudgetModel
import javafx.application.Platform

/**
 * A DAO for Budget objects.
 */
class BudgetDAO : DAO<BudgetModel>() {

    private var observers = mutableListOf<Observer>()
    private val deleteCommand = DeleteCommand("budgets")

    override fun get(id: Int): BudgetModel? {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM budgets WHERE id = $id")

        var moneyRecord: BudgetModel? = null

        if (resultSet.next()) {
            moneyRecord = BudgetModel(
                resultSet.getDouble("total_budget"),
                resultSet.getDouble("current_budget"),
                resultSet.getString("description"),
                resultSet.getString("currency"),
                resultSet.getInt("id")
            )
        }

        resultSet.close()
        statement.close()
        return moneyRecord
    }

    override fun getAll(): List<BudgetModel> {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM budgets")

        val moneyRecords = mutableListOf<BudgetModel>()

        while (resultSet.next()) {
            moneyRecords.add(
                BudgetModel(
                    resultSet.getDouble("total_budget"),
                    resultSet.getDouble("current_budget"),
                    resultSet.getString("description"),
                    resultSet.getString("currency"),
                    resultSet.getInt("id")
                )
            )
        }

        resultSet.close()
        statement.close()
        return moneyRecords
    }

    override fun create(obj: BudgetModel): Int {
        val sql = "INSERT INTO budgets (total_budget, current_budget, description, currency) VALUES (?, ?, ?, ?)"
        val statement = connection.prepareStatement(sql)
        statement.setDouble(1, obj.totalBudget)
        statement.setDouble(2, obj.currentBudget)
        statement.setString(3, obj.description)
        statement.setString(4, obj.currency)
        statement.executeUpdate()

        val stmt = connection.createStatement()
        val resultSet = stmt.executeQuery("SELECT last_insert_rowid()")
        val id = if (resultSet.next()) resultSet.getInt(1) else -1

        statement.close()
        Platform.runLater {
            notifyObservers(getAll())
        }

        return id
    }

    override fun update(obj: BudgetModel) {
        val statement = connection.prepareStatement("UPDATE budgets SET total_budget = ?, current_budget = ?, description = ? WHERE id = ?")
        statement.setDouble(1, obj.totalBudget)
        statement.setDouble(2, obj.currentBudget)
        statement.setString(3, obj.description)
        statement.setInt(4, obj.id)

        statement.executeUpdate()
        Platform.runLater {
            notifyObservers(getAll())
        }
        statement.close()
    }

    override fun delete(id: Int){
        deleteCommand.execute(id, connection)
        Platform.runLater {
            notifyObservers(getAll())
        }
    }

    override fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun notifyObservers(obj: Any) {
        observers.forEach { observer ->
            observer.update(obj)
        }
    }
}