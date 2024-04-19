package com.nhlstenden.morithij.budgettracker.models

import com.nhlstenden.morithij.budgettracker.controllers.Observer

interface Observable {
    fun addObserver(observer : Observer)
    fun notifyObservers(obj: Any)
}