package com.nhlstenden.morithij.budgettracker.models.dao

import com.nhlstenden.morithij.budgettracker.DatabaseConnector
import com.nhlstenden.morithij.budgettracker.controllers.Observer
import com.nhlstenden.morithij.budgettracker.models.*
import java.sql.Connection
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * A DAO for MoneyRecord objects.
 */
class TotalMoneyDAO : DAO<TotalMoneyModel> {
    private val connection: Connection = DatabaseConnector.getConnection()

    override fun get(id: Int): TotalMoneyModel? {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM total WHERE user = $id")

        var total :  TotalMoneyModel? = null

        if(resultSet.next()){
            total = TotalMoneyModel(
                    resultSet.getInt("id"),
                    resultSet.getDouble("total")
            )
        }

        resultSet.close()
        statement.close()
        return total
    }

    override fun save(obj: TotalMoneyModel) : Int {
        val sql = "INSERT INTO total (user, total) VALUES (?, ?)"
        val statement = connection.prepareStatement(sql)
        statement.setInt(1, obj.user)
        statement.setDouble(2, obj.total)
        statement.executeUpdate()

        val stmt = connection.createStatement()
        val resultSet = stmt.executeQuery("SELECT last_insert_rowid()")
        val id = if (resultSet.next()) resultSet.getInt(1) else -1

        statement.close()
//        connection.close()
        return id
    }

    override fun update(obj: TotalMoneyModel) {
        val statement = connection.prepareStatement("UPDATE total SET total = ? WHERE user = ?")
        statement.setDouble(1, obj.total)
        statement.setInt(2, obj.user)
        statement.executeUpdate()
        statement.close()
    }
}