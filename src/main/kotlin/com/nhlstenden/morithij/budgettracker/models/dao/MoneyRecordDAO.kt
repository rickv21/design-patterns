package com.nhlstenden.morithij.budgettracker.models.dao

import com.nhlstenden.morithij.budgettracker.models.MoneyRecordModel
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * A DAO for MoneyRecord objects.
 */
class MoneyRecordDAO : DAO<MoneyRecordModel>() {

    override fun get(id: Int): MoneyRecordModel? {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM records WHERE id = $id")

        var moneyRecord :  MoneyRecordModel? = null

        if(resultSet.next()){
            val timestamp = resultSet.getLong("record_date")
            val recordDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
            moneyRecord = MoneyRecordModel(
                resultSet.getInt("id"),
                resultSet.getDouble("money"),
                recordDate,
                resultSet.getString("description")
            )
        }

        resultSet.close()
        statement.close()
        return moneyRecord
    }

    override fun getAll(): List<MoneyRecordModel> {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM records")

        val moneyRecords = mutableListOf<MoneyRecordModel>()

        while(resultSet.next()){
            val timestamp = resultSet.getLong("record_date")
            val recordDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
            moneyRecords.add(
                MoneyRecordModel(
                    resultSet.getInt("id"),
                    resultSet.getDouble("money"),
                    recordDate,
                    resultSet.getString("description")
                )
            )
        }

        resultSet.close()
        statement.close()
        return moneyRecords
    }

    override fun create(obj: MoneyRecordModel) : Int {
        val sql = "INSERT INTO records (money, record_date, description) VALUES (?, ?, ?)"
        val statement = connection.prepareStatement(sql)
        statement.setDouble(1, obj.money)
        statement.setTimestamp(2, Timestamp(obj.recordDate.toInstant(ZoneOffset.UTC).toEpochMilli()))
        statement.setString(3, obj.description)
        statement.executeUpdate()

        val stmt = connection.createStatement()
        val resultSet = stmt.executeQuery("SELECT last_insert_rowid()")
        val id = if (resultSet.next()) resultSet.getInt(1) else -1

        statement.close()
        connection.close()

        return id
    }

    override fun update(obj: MoneyRecordModel) {
        val statement = connection.prepareStatement("UPDATE records SET money = ?, record_date = ?, description = ? WHERE id = ?")
        statement.setDouble(1, obj.money)
        statement.setTimestamp(2, java.sql.Timestamp.valueOf(obj.recordDate))
        statement.setString(3, obj.description)
        statement.setInt(4, obj.id)
        statement.executeUpdate()
        statement.close()
    }
}