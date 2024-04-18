package com.nhlstenden.morithij.budgettracker.calendar

import com.calendarfx.model.Entry

class CustomEntry(val entryType: EntryType, val id : Int = -1): Entry<String>() {

    init {
        if(entryType == EntryType.TEMP) {
            this.title = "Dummy"
        }
    }

}