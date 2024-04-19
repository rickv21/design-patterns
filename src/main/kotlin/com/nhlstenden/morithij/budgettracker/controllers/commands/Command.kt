package com.nhlstenden.morithij.budgettracker.controllers.commands

import java.sql.Connection

interface Command  {

    fun execute(id: Int, connection: Connection)
}