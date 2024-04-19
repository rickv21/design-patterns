package com.nhlstenden.morithij.budgettracker.models.dao

import com.nhlstenden.morithij.budgettracker.controllers.Observer
import com.nhlstenden.morithij.budgettracker.controllers.commands.DeleteCommand
import com.nhlstenden.morithij.budgettracker.models.ExpenseModel
import javafx.application.Platform
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * A DAO for Expense objects.
 */
class ExpenseDAO : DAO<ExpenseModel>() {
    private var observers = mutableListOf<Observer>()
    private val deleteCommand = DeleteCommand("expenses")


    override fun get(id: Int): ExpenseModel? {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM expenses WHERE id = $id")

        var expense: ExpenseModel? = null

        if (resultSet.next()) {
            val recordDateTimestamp = resultSet.getLong("record_date")
            val recordDate = LocalDate.ofInstant(Instant.ofEpochMilli(recordDateTimestamp), ZoneId.systemDefault())
            val endDateTimestamp = resultSet.getLong("end_date")
            val endDate = LocalDate.ofInstant(Instant.ofEpochMilli(endDateTimestamp), ZoneId.systemDefault())
            expense = ExpenseModel(
                resultSet.getInt("budget_id"),
                resultSet.getDouble("money"),
                recordDate,
                resultSet.getString("description"),
                resultSet.getString("interval"),
                endDate,
                resultSet.getInt("id")
            )
        }

        resultSet.close()
        statement.close()
        return expense
    }

    override fun getAll(): List<ExpenseModel> {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM expenses")

        val expenses = mutableListOf<ExpenseModel>()

        while (resultSet.next()) {
            val recordDateTimestamp = resultSet.getLong("record_date")
            val recordDate = LocalDate.ofInstant(Instant.ofEpochMilli(recordDateTimestamp), ZoneId.systemDefault())
            val endDateTimestamp = resultSet.getLong("end_date")
            val endDate = LocalDate.ofInstant(Instant.ofEpochMilli(endDateTimestamp), ZoneId.systemDefault())

            expenses.add(
                ExpenseModel(
                    resultSet.getInt("budget_id"),
                    resultSet.getDouble("money"),
                    recordDate,
                    resultSet.getString("description"),
                    resultSet.getString("interval"),
                    endDate,
                    resultSet.getInt("id")
                )
            )
        }

        resultSet.close()
        statement.close()
        return expenses
    }

    fun getAllByBudgetID(budgetID: Int): List<ExpenseModel> {
        val sql = "SELECT * FROM expenses WHERE budget_id = ?"
        val statement = connection.prepareStatement(sql)

        statement.setInt(1, budgetID)

        val resultSet = statement.executeQuery()

        val expenses = mutableListOf<ExpenseModel>()

        while (resultSet.next()) {
            val recordDateTimestamp = resultSet.getLong("record_date")
            val recordDate = LocalDate.ofInstant(Instant.ofEpochMilli(recordDateTimestamp), ZoneId.systemDefault())
            val endDateTimestamp = resultSet.getLong("end_date")
            val endDate = LocalDate.ofInstant(Instant.ofEpochMilli(endDateTimestamp), ZoneId.systemDefault())

            expenses.add(
                ExpenseModel(
                    resultSet.getInt("budget_id"),
                    resultSet.getDouble("money"),
                    recordDate,
                    resultSet.getString("description"),
                    resultSet.getString("interval"),
                    endDate,
                    resultSet.getInt("id")
                )
            )
        }

        resultSet.close()
        statement.close()
        return expenses
    }

    override fun create(obj: ExpenseModel): Int {
        val sql = "INSERT INTO expenses (budget_id, money, record_date, description, interval, end_date) VALUES (?, ?, ?, ?, ?, ?)"
        val statement = connection.prepareStatement(sql)
        statement.setInt(1, obj.budgetID)
        statement.setDouble(2, obj.money)
        statement.setTimestamp(3, Timestamp(obj.recordDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()))
        statement.setString(4, obj.description)
        if (obj.interval != null) {
            statement.setString(5, obj.interval)
        } else {
            statement.setNull(5, java.sql.Types.VARCHAR)
        }

        if (obj.endDate != null) {
            statement.setTimestamp(6, Timestamp(obj.endDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()))
        } else {
            statement.setNull(6, java.sql.Types.TIMESTAMP)
        }
        statement.executeUpdate()

        val stmt = connection.createStatement()
        val resultSet = stmt.executeQuery("SELECT last_insert_rowid()")
        val id = if (resultSet.next()) resultSet.getInt(1) else -1

        statement.close()
        notifyObservers(Pair(obj.budgetID, -obj.money))
        return id
    }

    override fun update(obj: ExpenseModel) {
        val old = get(obj.id)
        var money = 0.0
        if(old != null){
            money = old.money - obj.money
        }
        val statement = connection.prepareStatement("UPDATE expenses SET budget_id = ?, money = ?, record_date = ?, description = ?, `interval` = ?, end_date = ? WHERE id = ?")
        statement.setInt(1, obj.budgetID)
        statement.setDouble(2, obj.money)
        statement.setTimestamp(3, Timestamp(obj.recordDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()))
        statement.setString(4, obj.description)
        if (obj.interval != null) {
            statement.setString(5, obj.interval)
        } else {
            statement.setNull(5, java.sql.Types.VARCHAR)
        }

        if (obj.endDate != null) {
            statement.setTimestamp(6, Timestamp(obj.endDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()))
        } else {
            statement.setNull(6, java.sql.Types.TIMESTAMP)
        }

        statement.setInt(7, obj.id)

        statement.executeUpdate()
        statement.close()
        notifyObservers(Pair(obj.budgetID, money))
    }

    override fun delete(id: Int){
        var money = 0.0
        val expense = get(id)
        if(expense != null){
            money = expense.money
        }
        deleteCommand.execute(id, connection)
        Platform.runLater {
            notifyObservers(Pair(expense!!.budgetID, money))
        }
    }

    override fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun notifyObservers(obj: Any) {
        observers.forEach {observer ->
            observer.update(obj)
        }
    }
}