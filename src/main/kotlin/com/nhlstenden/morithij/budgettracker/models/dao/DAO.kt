package com.nhlstenden.morithij.budgettracker.models.dao

import com.nhlstenden.morithij.budgettracker.DatabaseConnector
import com.nhlstenden.morithij.budgettracker.models.Observable
import java.sql.Connection

/**
 * A Data Access Object (DAO) abstract class.
 */
abstract class DAO<T> : Observable{

    protected val connection: Connection = DatabaseConnector.getConnection()

    abstract fun get(id: Int): T?
    abstract fun getAll(): List<T>
    abstract fun create(obj: T) : Int
    abstract fun update(obj: T)
    abstract fun delete(id: Int)

    fun close(){
        connection.close()
    }
}