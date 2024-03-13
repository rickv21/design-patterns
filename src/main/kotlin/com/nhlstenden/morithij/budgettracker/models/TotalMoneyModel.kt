package com.nhlstenden.morithij.budgettracker.models

import java.time.LocalDateTime

/**
 * A model class representing a money record.
 */
data class TotalMoneyModel(val user: Int, val total: Double) : Model {
    override fun toString(): String {
        return total.toString()
    }
}

