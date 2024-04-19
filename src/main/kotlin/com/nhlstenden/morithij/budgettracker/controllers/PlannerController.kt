package com.nhlstenden.morithij.budgettracker.controllers

import com.calendarfx.model.Calendar
import com.calendarfx.model.CalendarSource
import com.calendarfx.view.CalendarView
import com.nhlstenden.morithij.budgettracker.calendar.CustomEntry
import com.nhlstenden.morithij.budgettracker.calendar.EntryType
import com.nhlstenden.morithij.budgettracker.controllers.popUps.ReminderCreatePopUp
import com.nhlstenden.morithij.budgettracker.models.ExpenseModel
import com.nhlstenden.morithij.budgettracker.models.ReminderModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.layout.AnchorPane
import javafx.util.Callback
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class PlannerController() : Controller() {

    @FXML
    lateinit var calendarContainer: AnchorPane
    lateinit var calendarView: CalendarView

    @FXML
    fun initialize() {
        val thread = Thread {
            val expenseDAO = DAOFactory.getDAO(ExpenseModel::class.java) as DAO<ExpenseModel>
            val expensesData = expenseDAO.getAll()

            Platform.runLater {
                calendarView = CalendarView(CalendarView.Page.WEEK)
                calendarView.minHeight = 550.0
                calendarView.minWidth = 673.0
                calendarView.maxHeight = 550.0
                calendarView.maxWidth = 673.0
                calendarView.calendarSources[0].calendars[0].name = "Reminders"

                calendarView.isShowPrintButton = false
                calendarView.isShowAddCalendarButton = false
                calendarView.isShowToolBar = false
                calendarView.requestedTime = LocalTime.now()

                val expenses = Calendar<String>("Expenses")

                expenses.setStyle(Calendar.Style.STYLE2)
                expenses.isReadOnly = true

                expenses.addEntries(expensesData.map {
                    val entry = CustomEntry(EntryType.EXPENSE)
                    entry.changeStartDate(it.recordDate)
                    entry.changeEndDate(it.recordDate)

                    if(it.interval != null){
                        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                        val formattedEndDate: String = it.endDate!!.format(formatter)

                        when(it.interval){
                            "Weekly" -> entry.recurrenceRule = "RRULE:FREQ=WEEKLY;UNTIL=$formattedEndDate";
                            "Monthly" -> entry.recurrenceRule = "RRULE:FREQ=MONTHLY;UNTIL=$formattedEndDate";
                            "Annually" -> entry.recurrenceRule = "RRULE:FREQ=YEARLY;UNTIL=$formattedEndDate";
                        }
                    }
                    entry.isFullDay = true
                    entry.title = ("-" + it.money.toString() + " - " + it.description)
                    entry
                })

                val myCalendarSource = CalendarSource("Expenses")
                myCalendarSource.calendars.addAll(expenses)
                calendarView.calendarSources.addAll(myCalendarSource)

                updateReminders()
                setupContextMenus()

                //Attempt to hide entries that it
                calendarView.entryDetailsCallback = Callback {
                    if ((it.entry as CustomEntry).entryType == EntryType.TEMP) {
                        it.entry.isHidden = true
                    }
                    null
                }

                calendarView.entryFactory = Callback { entryObj ->
                    val entry = ReminderCreatePopUp().show(entryObj.zonedDateTime.toLocalDate())
                    updateReminders()
                    entry
                }

                calendarContainer.children.add(calendarView)
            }
        }
        thread.start()
    }

    private fun setupContextMenus(){
        calendarView.entryContextMenuCallback = Callback { entryObj ->
            val contextMenu = ContextMenu()
            if ((entryObj.entry as CustomEntry).entryType == EntryType.EXPENSE) {
                val item = MenuItem("Delete " + (entryObj.entry as CustomEntry).title)
                item.setOnAction { _ ->
                    run {
                        val errorAlert = Alert(Alert.AlertType.ERROR)
                        errorAlert.title = "Error"
                        errorAlert.headerText = "You cannot delete expenses from the planning page!"
                        errorAlert.showAndWait()
                        return@setOnAction
                    }
                }
                contextMenu.items.add(item)
                contextMenu
            } else if ((entryObj.entry as CustomEntry).entryType == EntryType.REMINDER) {
                val item = MenuItem("Delete " + (entryObj.entry as CustomEntry).title)
                item.setOnAction { _ ->
                    run {
                        val thread = Thread {
                            val reminderDAO =
                                DAOFactory.getDAO(ReminderModel::class.java) as DAO<ReminderModel>
                            reminderDAO.delete((entryObj.entry as CustomEntry).id)
                            updateReminders()
                        }
                        thread.start()
                    }
                }

                contextMenu.items.add(item)
                contextMenu
            } else {
                null
            }
        }

        calendarView.contextMenuCallback = Callback {
            val backgroundContextMenu = ContextMenu()
            val entryItem = MenuItem("Add reminder")

            entryItem.setOnAction { _ ->
                ReminderCreatePopUp().show()
                updateReminders()
            }

            backgroundContextMenu.items.add(entryItem)
            backgroundContextMenu
        }

    }

    private fun updateReminders() {
        calendarView.calendarSources[0].calendars[0].clear()
        val thread = Thread {
            val reminderDAO = DAOFactory.getDAO(ReminderModel::class.java) as DAO<ReminderModel>
            val reminderData = reminderDAO.getAll()

            Platform.runLater {
                calendarView.calendarSources[0].calendars[0].addEntries(reminderData.map {
                    val entry = CustomEntry(EntryType.REMINDER, it.id)
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