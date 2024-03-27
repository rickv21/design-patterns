package com.nhlstenden.morithij.budgettracker.models

import java.time.LocalDateTime

/**
 * A model class representing a money record.
 */
data class MoneyRecordModel(val id: Int, val money: Double, val recordDate: LocalDateTime, val description: String, val currency: String = "EUR", val tagId: Int) : Model
