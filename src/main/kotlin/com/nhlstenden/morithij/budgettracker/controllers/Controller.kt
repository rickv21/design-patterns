package com.nhlstenden.morithij.budgettracker.controllers

abstract class Controller {

    open val title = "Budget Tracker"
    open val width = 800.0
    open val height = 600.0

    abstract fun setModels(vararg models: Any)

}