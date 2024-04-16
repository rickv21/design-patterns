package com.nhlstenden.morithij.budgettracker.models.dao

import com.nhlstenden.morithij.budgettracker.models.ExpenseModel
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * A DAO for Expense objects.
 */
class ExpenseDAO : DAO<ExpenseModel>() {

    override fun get(id: Int): ExpenseModel? {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM budgets WHERE id = $id")

        var expense: ExpenseModel? = null

        if (resultSet.next()) {
            val timestamp = resultSet.getLong("record_date")
            val recordDate = LocalDate.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
            expense = ExpenseModel(
                resultSet.getInt("budget_id"),
                resultSet.getDouble("money"),
                recordDate,
                resultSet.getString("description"),
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
            val timestamp = resultSet.getLong("record_date")
            val recordDate = LocalDate.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())

            expenses.add(
                ExpenseModel(
                    resultSet.getInt("budget_id"),
                    resultSet.getDouble("money"),
                    recordDate,
                    resultSet.getString("description")
                )
            )
        }

        resultSet.close()
        statement.close()
        return expenses
    }

    fun getAllByBudgetID(budgetID : Int): List<ExpenseModel> {
        val sql = "SELECT * FROM expenses WHERE budget_id = ?"
        val statement = connection.prepareStatement(sql)

        statement.setInt(1, budgetID)

        val resultSet = statement.executeQuery()

        val expenses = mutableListOf<ExpenseModel>()

        while (resultSet.next()) {
            val timestamp = resultSet.getLong("record_date")
            val recordDate = LocalDate.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())

            expenses.add(
                ExpenseModel(
                    resultSet.getInt("budget_id"),
                    resultSet.getDouble("money"),
                    recordDate,
                    resultSet.getString("description")
                )
            )
        }

        resultSet.close()
        statement.close()
        return expenses
    }

    override fun create(obj: ExpenseModel): Int {
        val sql = "INSERT INTO expenses (budget_id, money, record_date, description) VALUES (?, ?, ?, ?)"
        val statement = connection.prepareStatement(sql)
        statement.setInt(1, obj.budgetID)
        statement.setDouble(2, obj.money)
        statement.setTimestamp(3, Timestamp(obj.recordDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()))
        statement.setString(4, obj.description)

        statement.executeUpdate()

        val stmt = connection.createStatement()
        val resultSet = stmt.executeQuery("SELECT last_insert_rowid()")
        val id = if (resultSet.next()) resultSet.getInt(1) else -1

        statement.close()
        connection.close()

        return id
    }

    override fun update(obj: ExpenseModel) {
        val statement = connection.prepareStatement("UPDATE expenses SET money = ?, record_date = ?, description = ? WHERE id = ?")
        statement.setDouble(1, obj.money)
        statement.setTimestamp(2, Timestamp(obj.recordDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()))
        statement.setString(3, obj.description)
        statement.setInt(4, obj.id)

        statement.executeUpdate()
        statement.close()
    }
}