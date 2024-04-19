package com.nhlstenden.morithij.budgettracker.calendar

/**
 * Represents the type of calendar entry.
 * Reminder and expense speak for themselfs.
 * TEMP is used for the entries that calendarFX sometimes creates even if adding an entry is cancelled.
 * These do not get saved to the database and are a kind of "ghost entries".
 */
enum class EntryType {
    REMINDER, EXPENSE, TEMP
}