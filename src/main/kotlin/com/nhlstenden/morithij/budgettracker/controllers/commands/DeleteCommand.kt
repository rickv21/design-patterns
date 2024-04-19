package com.nhlstenden.morithij.budgettracker.controllers.commands

import javafx.application.Platform
import java.sql.Connection

class DeleteCommand(private val tableName: String): Command {


    override fun execute(id: Int, connection: Connection) {
        val statement = connection.prepareStatement("DELETE FROM $tableName WHERE id = ?")
        statement.setInt(1, id)

        statement.executeUpdate()
        statement.close()
    }
}