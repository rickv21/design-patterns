package com.nhlstenden.morithij.budgettracker.controllers

import com.calendarfx.model.Calendar
import com.calendarfx.model.CalendarSource
import com.calendarfx.model.Entry
import com.calendarfx.view.CalendarView
import com.calendarfx.view.DateControl.*
import com.nhlstenden.morithij.budgettracker.calendar.CustomEntry
import com.nhlstenden.morithij.budgettracker.calendar.EntryType
import com.nhlstenden.morithij.budgettracker.controllers.popUps.ReminderCreatePopUp
import com.nhlstenden.morithij.budgettracker.models.ExpenseModel
import com.nhlstenden.morithij.budgettracker.models.ReminderModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.layout.AnchorPane
import javafx.util.Callback
import java.time.LocalTime

class PlannerController() : Controller() {

    @FXML
    lateinit var calendarContainer: AnchorPane

    @FXML
    fun initialize() {

        val thread = Thread {
            val expenseDAO = DAOFactory.getDAO(ExpenseModel::class.java) as DAO<ExpenseModel>
            val expensesData = expenseDAO.getAll()

            val reminderDAO = DAOFactory.getDAO(ReminderModel::class.java) as DAO<ReminderModel>
            val reminderData = reminderDAO.getAll()

            Platform.runLater {
                val calendarView = CalendarView(CalendarView.Page.WEEK) // (1)
                calendarView.minHeight = 550.0
                calendarView.minWidth = 673.0
                calendarView.maxHeight = 550.0
                calendarView.maxWidth = 673.0
                calendarView.calendarSources[0].calendars[0].name = "Reminders"
                calendarView.isShowPrintButton = false
                calendarView.isShowAddCalendarButton = false
                calendarView.isShowToolBar = false

                val expenses = Calendar<String>("Expenses")

                expenses.setStyle(Calendar.Style.STYLE2) // (3)
                expenses.isReadOnly = true

                expenses.addEntries(expensesData.map {
                    val entry = CustomEntry(EntryType.EXPENSE)
                    entry.changeStartDate(it.recordDate)
                    entry.changeEndDate(it.recordDate)
                    entry.isFullDay = true
                    entry.title = ("-" + it.money.toString() + " - " + it.description)
                    entry
                })

                calendarView.calendarSources[0].calendars[0].addEntries(reminderData.map {
                    val entry = CustomEntry(EntryType.REMINDER, it.id)
                    entry.changeStartDate(it.remindDate)
                    entry.changeEndDate(it.remindDate)
                    entry.isFullDay = true
                    entry.title = (it.description)
                    entry
                })

                val myCalendarSource = CalendarSource("Expenses") // (4)
                myCalendarSource.calendars.addAll(expenses)

                calendarView.calendarSources.addAll(myCalendarSource) // (5)

                calendarView.requestedTime = LocalTime.now()

                calendarView.entryDetailsCallback = Callback {
                    println("Entry clicked: ${it.entry}")
                    if(it.entry is CustomEntry) {
                        it.entry.isHidden = true
                    }
                    null
                }

                calendarView.entryContextMenuCallback = Callback { entryObj ->
                    if((entryObj.entry as CustomEntry).entryType == EntryType.EXPENSE){
                        null
                    } else {
                        val reminderContextMenu = ContextMenu()
                        val item = MenuItem("Delete reminder")

                        item.setOnAction { _ ->
                            run {
                                val thread = Thread {
                                    val reminderDAO = DAOFactory.getDAO(ReminderModel::class.java) as DAO<ReminderModel>
                                    reminderDAO.delete((entryObj.entry as CustomEntry).id)
                                    Platform.runLater {
                                        calendarView.calendarSources[0].calendars[0].clear()
                                        val reminderData = reminderDAO.getAll()
                                        calendarView.calendarSources[0].calendars[0].addEntries(reminderData.map {
                                            val entry = Entry<String>()
                                            entry.changeStartDate(it.remindDate)
                                            entry.changeEndDate(it.remindDate)
                                            entry.isFullDay = true
                                            entry.title = (it.description)
                                            entry
                                        })
                                    }
                                }
                                thread.start()
                            }
                        }

                        reminderContextMenu.items.add(item)
                        reminderContextMenu
                    }
                }


                var testEntryMenu = ContextMenu()
                var entryItem = MenuItem("Add reminder")

                entryItem.setOnAction { event ->
                    println("Add reminder")}

                testEntryMenu.items.add(entryItem)

                calendarView.contextMenuCallback = Callback {
                    testEntryMenu
                }

                calendarView.entryFactory = Callback {
                    val entry = ReminderCreatePopUp().show()
                    val entrytwo = Entry<String>()
                    entrytwo.title = "testing123"
                    calendarView.calendarSources[0].calendars[0].clear()
                    val thread = Thread {
                        val reminderDAO = DAOFactory.getDAO(ReminderModel::class.java) as DAO<ReminderModel>
                        val reminderData = reminderDAO.getAll()

                        Platform.runLater {
                            calendarView.calendarSources[0].calendars[0].addEntries(reminderData.map {
                                val entry = Entry<String>()
                                entry.changeStartDate(it.remindDate)
                                entry.changeEndDate(it.remindDate)
                                entry.isFullDay = true
                                entry.title = (it.description)
                                entry
                            })
                        }
                    }
                    thread.start()
                    entry
                }
//
//                val updateTimeThread: Thread = object : Thread("Calendar: Update Time Thread") {
//                    override fun run() {
//                        while (true) {
//                            Platform.runLater {
//                                calendarView.today = LocalDate.now()
//                                calendarView.time = LocalTime.now()
//                            }
//
//                            try {
//                                // update every 10 seconds
//                                sleep(10000)
//                            } catch (e: InterruptedException) {
//                                e.printStackTrace()
//                            }
//                        }
//                    }
//                }
//
//                updateTimeThread.priority = Thread.MIN_PRIORITY
//                updateTimeThread.isDaemon = true
//                updateTimeThread.start()

                calendarContainer.children.add(calendarView)

            }
        }
        thread.start()




    }


}