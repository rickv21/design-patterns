package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.models.Model

interface Observer {
    fun update(obj : Any)
}