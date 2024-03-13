package com.nhlstenden.morithij.budgettracker

import java.sql.Connection
import java.sql.DriverManager

/**
 * The class responsible for connecting to the SQLite database.
 */
object DatabaseConnector {

    private var connection: Connection? = null

    private fun init() {
        //Hardcoded for now.
        val url = "jdbc:sqlite:budgetDB.db"
        connection = DriverManager.getConnection(url)

        // Date is stored as a string because SQLite does not have a DATEIME column type.
        var statement = connection!!.createStatement()
        statement.execute("CREATE TABLE IF NOT EXISTS records (id INTEGER PRIMARY KEY, money DOUBLE, record_date TEXT, description TEXT)")
        statement = connection!!.createStatement()

        statement.execute("CREATE TABLE IF NOT EXISTS total (id INTEGER PRIMARY KEY, user INTEGER, total DOUBLE)")
    }

    fun getConnection(): Connection {
        if (connection == null || connection!!.isClosed) {
            init()
        }
        if(connection == null) throw Exception("Connection is null")
        return connection as Connection
    }

    fun close() {
        //At the moment this is not yet used.
        //I read that closing and re-opening connections is an expensive operation.
        //So when functionality is completed we should look when we want to close the connection.
        connection?.close()
    }
}