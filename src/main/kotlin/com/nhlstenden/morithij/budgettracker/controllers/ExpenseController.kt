package com.nhlstenden.morithij.budgettracker.controllers

import java.lang.reflect.Field

class ExpenseController() : Controller() {


    override fun setModels(vararg models: Any) {
        val fields: Array<Field> = this.javaClass.declaredFields
        for (field in fields) {
            for (model in models) {
                if (model.javaClass == field.type) {
                    field.isAccessible = true
                    field.set(this, model)
                }
            }
        }
    }
}