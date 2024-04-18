package com.nhlstenden.morithij.budgettracker.models

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * A model class representing a budget.
 */
data class ExpenseModel(val budgetID : Int, val money: Double, val recordDate: LocalDate, val description : String, val interval : String? = null, val endDate : LocalDate? = null, val id : Int = 0) : Model
