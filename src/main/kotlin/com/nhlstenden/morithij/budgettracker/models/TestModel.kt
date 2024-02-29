package com.nhlstenden.morithij.budgettracker.models

class TestModel {

    private var budgetMap = HashMap<String, Any>()

    fun increment() {
        val counter = budgetMap.getOrDefault("counter", 0) as Int
        budgetMap["counter"] = counter + 1
    }

    operator fun get(key: String): Any? {
        return budgetMap[key]
    }

    operator fun set(key: String, value: Any) {
        budgetMap[key] = value
    }
}