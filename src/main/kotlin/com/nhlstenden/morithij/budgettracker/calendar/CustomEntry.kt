package com.nhlstenden.morithij.budgettracker.calendar

import com.calendarfx.model.Entry

/**
 * Extends the build-in Entry class from CalendarFX.
 * Contains some extra info that helps distinguish between different types of entries.
 */
class CustomEntry(val entryType: EntryType, val id : Int = -1): Entry<String>() {
    init {
        if(entryType == EntryType.TEMP) {
            this.title = "Dummy"
        }
    }

}