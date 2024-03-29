package com.nhlstenden.morithij.budgettracker.models.dao

import com.nhlstenden.morithij.budgettracker.DatabaseConnector
import com.nhlstenden.morithij.budgettracker.models.*
import java.sql.Connection

/**
 * A DAO for MoneyRecord objects.
 */
class UserInfoDAO : DAO<UserInfoModel> {
    private val connection: Connection = DatabaseConnector.getConnection()

    override fun get(id: Int): UserInfoModel? {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM user WHERE id = $id")

        var total :  UserInfoModel? = null

        if(resultSet.next()){
            total = UserInfoModel(
                    resultSet.getInt("id"),
                    resultSet.getDouble("total_money")
            )
        }

        resultSet.close()
        statement.close()
        return total
    }

    override fun save(obj: UserInfoModel) : Int {
        val sql = "INSERT INTO user (id, total_money) VALUES (?, ?)"
        val statement = connection.prepareStatement(sql)
        statement.setInt(1, obj.user)
        statement.setDouble(2, obj.totalMoney)
        statement.executeUpdate()

        val stmt = connection.createStatement()
        val resultSet = stmt.executeQuery("SELECT last_insert_rowid()")
        val id = if (resultSet.next()) resultSet.getInt(1) else -1

        statement.close()
//        connection.close()
        return id
    }

    override fun update(obj: UserInfoModel) {
        val statement = connection.prepareStatement("UPDATE user SET total_money = ? WHERE id = ?")
        statement.setDouble(1, obj.totalMoney)
        statement.setInt(2, obj.user)
        statement.executeUpdate()
        statement.close()
    }
}