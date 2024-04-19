package com.nhlstenden.morithij.budgettracker.controllers.popUps;

import com.calendarfx.model.Entry
import com.nhlstenden.morithij.budgettracker.calendar.CustomEntry
import com.nhlstenden.morithij.budgettracker.calendar.EntryType
import com.nhlstenden.morithij.budgettracker.models.ReminderModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.HBox
import java.time.LocalDate

class ReminderCreatePopUp () : PopUp(null, null) {

    fun show(date: LocalDate? = LocalDate.now()) : Entry<String> {
        stage.title = "Create Reminder"
        val label1 = Label("Description:")
        val textField1 = TextField()
        layout.add(label1, 0, 0)
        layout.add(textField1, 1, 0)

        val label2 = Label("Date:")
        val textField2 = DatePicker()
        textField2.value = date
        layout.add(label2, 0, 1)
        layout.add(textField2, 1, 1)

        val okButton = Button("Add")
        okButton.setOnAction {

            if (textField1.text.isEmpty() || textField2.value == null) {
                val errorAlert = Alert(Alert.AlertType.ERROR)
                errorAlert.title = "Error"
                errorAlert.headerText = "Please fill in all fields!"
                errorAlert.showAndWait()
                return@setOnAction
            }

            val thread = Thread {
                val dao = DAOFactory.getDAO(ReminderModel::class.java) as DAO<ReminderModel>
                dao.create(ReminderModel( textField1.text.toString(), textField2.value))
                Platform.runLater {
                    val successAlert = Alert(Alert.AlertType.INFORMATION)
                    successAlert.title = "Success"
                    successAlert.headerText = "Added reminder!"
                    successAlert.showAndWait()
                    stage.close()
                }
            }
            thread.start()

            stage.close()
        }

        val cancelButton = Button("Cancel")
        cancelButton.setOnAction {
            stage.close()
        }

        val buttonBox = HBox(10.0)
        buttonBox.alignment = Pos.CENTER
        buttonBox.children.addAll(okButton, cancelButton)
        layout.add(buttonBox, 0, 3, 2, 1)

        var scene = Scene(layout, 300.0, 200.0)


        stage.scene = scene
        stage.showAndWait()

        //CalendarFX always wants a entry and ALWAYS adds it.
        //So we always need to return and add a Entry even if we cancel the popup.
        //So instead we add a entry to a place you would never look and then refresh the calendar ourselves later.
        var entry = CustomEntry(EntryType.TEMP)
        return entry
    }
}
