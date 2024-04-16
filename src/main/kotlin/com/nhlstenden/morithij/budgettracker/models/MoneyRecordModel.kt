package com.nhlstenden.morithij.budgettracker.models

import java.time.LocalDateTime

/**
 * A model class representing a money record.
 */
data class MoneyRecordModel(val money: Double, val recordDate: LocalDateTime, val description: String, val tagId: Int, val currency: String = "EUR", val id : Int = 0) : Model
