package com.nhlstenden.morithij.budgettracker.models.dao

/**
 * A Data Access Object (DAO) interface.
 */
interface DAO<T> {

    fun get(id: Int): T?
    fun save(obj: T) : Int
    fun update(obj: T)
}