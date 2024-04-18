package com.nhlstenden.morithij.budgettracker.controllers.popUps

import com.nhlstenden.morithij.budgettracker.models.UserInfoModel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.GridPane
import javafx.stage.Modality
import javafx.stage.Stage

abstract class PopUp(private val userInfo: UserInfoModel) {
    protected val stage : Stage
    protected val layout : GridPane


    init {
        val popup = Stage()
        popup.initModality(Modality.APPLICATION_MODAL)
        popup.isResizable = false
        popup.minWidth = 400.0
        popup.maxWidth = 400.0
        popup.minHeight = 200.0
        popup.maxHeight = 200.0
        val layout = GridPane()
        layout.alignment = Pos.CENTER
        layout.hgap = 10.0
        layout.vgap = 10.0
        layout.padding = Insets(25.0, 25.0, 25.0, 25.0)
        this.stage = popup
        this.layout = layout
    }
}