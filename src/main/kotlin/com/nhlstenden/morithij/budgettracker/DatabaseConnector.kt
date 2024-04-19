package com.nhlstenden.morithij.budgettracker

import java.io.File
import java.sql.Connection
import java.sql.DriverManager

/**
 * The class responsible for connecting to the SQLite database.
 */
object DatabaseConnector {

    const val DATABASE_FILE = "budgetDB.db"
    const val RESET_ON_START = false

    private var connection: Connection? = null

    private fun init(startup: Boolean = false) {
        val dbFile = File(DATABASE_FILE)

        if (dbFile.exists() && startup && RESET_ON_START) {
            println("Deleting database file")
            dbFile.delete()
        }
        val url = "jdbc:sqlite:$DATABASE_FILE"
        connection = DriverManager.getConnection(url)
        val pragmaStatement = connection!!.createStatement()
        pragmaStatement.execute("PRAGMA foreign_keys = ON")
        pragmaStatement.close()
        initDatabase()
    }

    private fun initDatabase(){
        val statement = connection!!.createStatement()

        statement.execute("CREATE TABLE IF NOT EXISTS expenses (id INTEGER PRIMARY KEY, budget_id INTEGER, money DOUBLE, record_date TEXT, description TEXT, `interval` TEXT, end_date TEXT, FOREIGN KEY (budget_id) REFERENCES budgets(id) ON DELETE CASCADE)")
        statement.execute("CREATE TABLE IF NOT EXISTS budgets (id INTEGER PRIMARY KEY, total_budget DOUBLE, current_budget DOUBLE, description TEXT, currency TEXT)")
        statement.execute("CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY, total_money DOUBLE, expense_limit DOUBLE DEFAULT 0.0)")
        statement.execute("CREATE TABLE IF NOT EXISTS reminder (id INTEGER PRIMARY KEY, description DOUBLE, reminder_date TEXT)")

        //check if user exist other while create:
        val resultSet = statement.executeQuery("SELECT * FROM user WHERE id = 1")
        if(!resultSet.next()){
            statement.execute("INSERT INTO user (id, total_money) VALUES (1, 0.0)")
        }

        //statement.execute("INSERT INTO user (id, total_money) VALUES (1, 0.0)")
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