package com.nhlstenden.morithij.budgettracker.models.dao

import com.nhlstenden.morithij.budgettracker.controllers.Observer
import com.nhlstenden.morithij.budgettracker.controllers.commands.DeleteCommand
import com.nhlstenden.morithij.budgettracker.models.UserInfoModel

/**
 * A DAO for MoneyRecord objects.
 */
class UserInfoDAO : DAO<UserInfoModel>() {
    private val deleteCommand = DeleteCommand("user")

    override fun get(id: Int): UserInfoModel? {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM user WHERE id = $id")

        var total :  UserInfoModel? = null

        if(resultSet.next()){
            total = UserInfoModel(
                    resultSet.getInt("id"),
                    resultSet.getDouble("expense_limit")
            )
        }

        resultSet.close()
        statement.close()
        return total
    }

    override fun getAll(): List<UserInfoModel> {
        TODO("Not needed due to the multiple users feature being cut.")
    }

    override fun delete(id: Int) {
        deleteCommand.execute(id, connection)
    }

    override fun addObserver(observer: Observer) {
        TODO("Not yet implemented")
    }

    override fun notifyObservers(obj: Any) {
        TODO("Not yet implemented")
    }

    override fun create(obj: UserInfoModel): Int {
        val sql = "INSERT INTO user (id) VALUES (?)"
        val statement = connection.prepareStatement(sql)
        statement.setInt(1, obj.user)
        statement.executeUpdate()

        val stmt = connection.createStatement()
        val resultSet = stmt.executeQuery("SELECT last_insert_rowid()")
        val id = if (resultSet.next()) resultSet.getInt(1) else -1

        statement.close()
        return id
    }


    override fun update(obj: UserInfoModel) {
        val statement = connection.prepareStatement("UPDATE user SET expense_limit = ? WHERE id = ?")
        statement.setDouble(1, obj.expenseLimit)
        statement.setInt(2, obj.user)
        statement.executeUpdate()
        statement.close()
    }
}