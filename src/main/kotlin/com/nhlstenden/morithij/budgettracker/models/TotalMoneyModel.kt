package com.nhlstenden.morithij.budgettracker.models

import com.nhlstenden.morithij.budgettracker.controllers.Observer
import java.time.LocalDateTime

/**
 * A model class representing a money record.
 */
data class TotalMoneyModel(val user: Int, var total: Double) : Model, Observable {
    private val observers : ArrayList<Observer>  = ArrayList<Observer>()

    public fun setTotalAmount(amount : Double){
        total = amount
        notifyObservers()
    }

    override fun toString(): String {
        return total.toString()
    }

    override fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun notifyObservers() {
        observers.forEach { it.update(this) }
    }
}

