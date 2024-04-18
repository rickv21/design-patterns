package com.nhlstenden.morithij.budgettracker.controllers

abstract class Controller {

    open val title = "Budget Tracker"
    open val width = 900.0
    open val height = 600.0

    open fun setModels(vararg models: Any){
        //Empty implementation for controllers to implement.
        //Is not abstract due to it being empty also being valid.
        //This way, controllers can choose to implement this method or not.
    }

}