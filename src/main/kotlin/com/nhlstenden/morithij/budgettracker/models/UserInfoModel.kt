package com.nhlstenden.morithij.budgettracker.models

import com.nhlstenden.morithij.budgettracker.controllers.Observer

/**
 * A model class representing a money record.
 */
data class UserInfoModel(val user: Int, var totalMoney: Double) : Model, Observable {
    private val observers : ArrayList<Observer>  = ArrayList()

    fun setTotalAmount(amount : Double){
        totalMoney = amount
        notifyObservers()
    }

    override fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun notifyObservers() {
        observers.forEach { it.update(this) }
    }
}

