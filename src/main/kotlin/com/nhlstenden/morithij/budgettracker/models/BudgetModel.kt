package com.nhlstenden.morithij.budgettracker.models

/**
 * A model class representing a budget.
 */
data class BudgetModel(val totalBudget: Double, val currentBudget: Double, val description: String, val currency: String = "EUR", val id : Int = 0) : Model
