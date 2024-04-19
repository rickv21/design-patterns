package com.nhlstenden.morithij.budgettracker.models.dao

import com.nhlstenden.morithij.budgettracker.controllers.Observer
import com.nhlstenden.morithij.budgettracker.models.ReminderModel
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * A DAO for Reminder objects.
 */
class ReminderDAO : DAO<ReminderModel>() {

    override fun get(id: Int): ReminderModel? {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM reminder WHERE id = $id")

        var reminder: ReminderModel? = null

        if (resultSet.next()) {
            val remindDateTimestamp = resultSet.getLong("reminder_date")
            val remindDate = LocalDate.ofInstant(Instant.ofEpochMilli(remindDateTimestamp), ZoneId.systemDefault())
            reminder = ReminderModel(
                resultSet.getString("description"),
                remindDate,
                id
            )
        }

        resultSet.close()
        statement.close()
        return reminder
    }

    override fun getAll(): List<ReminderModel> {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM reminder")

        val reminders = mutableListOf<ReminderModel>()

        while (resultSet.next()) {
            val remindDateTimestamp = resultSet.getLong("reminder_date")
            val remindDate = LocalDate.ofInstant(Instant.ofEpochMilli(remindDateTimestamp), ZoneId.systemDefault())

            reminders.add(
                ReminderModel(
                    resultSet.getString("description"),
                    remindDate,
                    resultSet.getInt("id")
                )
            )
        }

        resultSet.close()
        statement.close()
        return reminders
    }


    override fun create(obj: ReminderModel): Int {
        val sql = "INSERT INTO reminder (description, reminder_date) VALUES (?, ?)"
        val statement = connection.prepareStatement(sql)
        statement.setString(1, obj.description)
        statement.setTimestamp(2, Timestamp(obj.remindDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()))

        statement.executeUpdate()

        val stmt = connection.createStatement()
        val resultSet = stmt.executeQuery("SELECT last_insert_rowid()")
        val id = if (resultSet.next()) resultSet.getInt(1) else -1

        statement.close()
        return id
    }

    override fun update(obj: ReminderModel) {
        //We do not support updating reminders.
        return
    }

    override fun delete(id: Int){
        val statement = connection.prepareStatement("DELETE FROM reminder WHERE id = ?")
        statement.setInt(1, id)

        statement.executeUpdate()
        statement.close()
    }

    override fun addObserver(observer: Observer) {
        TODO("Not yet implemented")
    }

    override fun notifyObservers(obj: Any) {
        TODO("Not yet implemented")
    }
}