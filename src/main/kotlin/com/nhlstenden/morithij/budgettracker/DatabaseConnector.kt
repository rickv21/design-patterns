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
        initDatabase()
    }

    private fun initDatabase(){
        val statement = connection!!.createStatement()
        //Set the user_id to 1 by default, this is temporary until we have a login system.
        statement.execute("CREATE TABLE IF NOT EXISTS records (id INTEGER PRIMARY KEY, user_id INTEGER DEFAULT 1, money DOUBLE, record_date TEXT, description TEXT)")
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