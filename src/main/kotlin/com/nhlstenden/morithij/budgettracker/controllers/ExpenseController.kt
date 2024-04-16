package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.models.MoneyRecordModel
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ExpenseController() : Controller() {

    @FXML
    private lateinit var lineChart: LineChart<Number, Number>

    @FXML
    private lateinit var startDatePicker : DatePicker

    @FXML
    private lateinit var endDatePicker : DatePicker

    @FXML
    private lateinit var test : Label

    // This function is called when the FXML file is loaded
    fun initialize() {
        // Sample data collection
        val records = listOf(
            MoneyRecordModel(1, -10.0, LocalDateTime.of(2021, 4, 25, 10, 0), "Expense 1", "EUR", null),
            MoneyRecordModel(2, 20.0, LocalDateTime.of(2022, 5, 25, 11, 0), "Expense 2", "EUR", null),
            MoneyRecordModel(3, 15.0, LocalDateTime.of(2023, 1, 26, 9, 0), "Expense 3", "EUR", null),
            MoneyRecordModel(4, 30.0, LocalDateTime.of(2024, 2, 26, 15, 0), "Expense 4", "EUR", null)
        )

        startDatePicker.setOnAction { updateChart(records) }
        endDatePicker.setOnAction { updateChart(records) }

        // Add data to the chart
        val series = generatePeriodicSeriesData(records)

        // Add the series to the line chart
        lineChart.data.add(series)

        // Set the labels for the axes
        lineChart.xAxis.label = "Date"
        lineChart.yAxis.label = "Amount"

        // Set the lower bound of the x-axis to the timestamp of the first data point
        val xAxis = lineChart.xAxis as NumberAxis
        xAxis.isForceZeroInRange = false
        xAxis.tickLabelFormatter = object : NumberAxis.DefaultFormatter(xAxis) {
            override fun toString(number: Number): String {
                // Convert the number (timestamp) to LocalDateTime
                val dateTime = LocalDateTime.ofEpochSecond(number.toLong(), 0, ZoneOffset.UTC)
                // Format the LocalDateTime to the desired format
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yy")
                return formatter.format(dateTime)
            }
        }

        // Hide the legend
        lineChart.isLegendVisible = false

//        startDatePicker.setOnAction { updateChart(records) }
//        endDatePicker.setOnAction { updateChart(records) }
    }

    private fun updateChart(records: List<MoneyRecordModel>) {
        val startDate = startDatePicker.value?.atStartOfDay()
        val endDate = endDatePicker.value?.atTime(23, 59, 59)

        // Generate the series data based on the filtered records
        val series = generatePeriodicSeriesData(records, startDate, endDate)

        // Clear the existing data and add the updated series to the chart
        lineChart.data.clear()
        lineChart.data.add(series)
    }

    // Generates a series of data for the line chart
    private fun generatePeriodicSeriesData(records: Collection<MoneyRecordModel>, beginDate: LocalDateTime? = null, endDate: LocalDateTime? = null): XYChart.Series<Number, Number> {
        val sortedRecords = records.sortedBy { it.recordDate }

        // Determine beginDate and endDate if not provided
        val earliestDate = sortedRecords.firstOrNull()?.recordDate
        val latestDate = sortedRecords.lastOrNull()?.recordDate

        val actualBeginDate = beginDate ?: earliestDate ?: LocalDateTime.now()
        val actualEndDate = endDate ?: latestDate ?: LocalDateTime.now()

        startDatePicker.value = actualBeginDate?.toLocalDate()
        endDatePicker.value = actualEndDate?.toLocalDate()

        if(!actualBeginDate.isBefore(actualEndDate)){
            return XYChart.Series<Number, Number>()
        }

        val filteredRecords = sortedRecords.filter { it.recordDate in actualBeginDate..actualEndDate }
        val duration = Duration.between(actualBeginDate, actualEndDate)
        val numberOfDivisions = filteredRecords.size

        val periodDuration = duration.dividedBy(numberOfDivisions.toLong())
        val series = XYChart.Series<Number, Number>()

        var currentPeriodStart = actualBeginDate
        var currentPeriodEnd = currentPeriodStart.plus(periodDuration)

        var sum = 0.0
        val recordsIterator = filteredRecords.iterator()
        var record: MoneyRecordModel? = if (recordsIterator.hasNext()) recordsIterator.next() else null

        repeat(numberOfDivisions + 1) { periodIndex ->
            while (record != null && record!!.recordDate < currentPeriodEnd) {
                sum += record!!.money
                record = if (recordsIterator.hasNext()) recordsIterator.next() else null
            }

            val data = XYChart.Data<Number, Number>(currentPeriodStart.toEpochSecond(ZoneOffset.UTC), sum)
            series.data.add(data)
            currentPeriodStart = currentPeriodEnd
            currentPeriodEnd = currentPeriodEnd.plus(periodDuration)
        }
        return series
    }

    override fun setModels(vararg models: Any) {
        // Implementation of the abstract function setModels is not required here
        // This function can be used to set models if needed
    }
}