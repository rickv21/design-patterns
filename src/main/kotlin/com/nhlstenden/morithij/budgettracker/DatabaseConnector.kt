package com.nhlstenden.morithij.budgettracker

import java.sql.Connection
import java.sql.DriverManager

object DatabaseConnector {

    private var connection: Connection? = null

    private fun init() {
        val url = "jdbc:sqlite:budgetDB.db"
        connection = DriverManager.getConnection(url)

        // Date is stored as a string because SQLite does not have a DATEIME column type.
        val statement = connection!!.createStatement()
        statement.execute("CREATE TABLE IF NOT EXISTS records (id INTEGER PRIMARY KEY, money DOUBLE, record_date TEXT, description TEXT)")
    }

    fun getConnection(): Connection {
        if (connection == null || connection!!.isClosed) {
            init()
        }
        if(connection == null) throw Exception("Connection is null")
        return connection as Connection
    }

    fun close() {
        connection?.close()
    }
}