package com.nhlstenden.morithij.budgettracker.models

import java.time.LocalDate

/**
 * A model class representing a reminder.
 */
data class ReminderModel(val description : String, val remindDate: LocalDate, val id : Int = 0) : Model
