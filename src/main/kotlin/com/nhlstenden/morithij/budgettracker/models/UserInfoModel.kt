package com.nhlstenden.morithij.budgettracker.models

import com.nhlstenden.morithij.budgettracker.controllers.Observer

/**
 * A model class representing a money record.
 */
data class UserInfoModel(val user: Int, var expenseLimit : Double) : Model, Observable {
    private val observers : ArrayList<Observer>  = ArrayList()

    override fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun notifyObservers(obj: Any) {
        observers.forEach { it.update(obj) }
    }
}

