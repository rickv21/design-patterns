package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.controllers.popUps.CreatePopUp
import com.nhlstenden.morithij.budgettracker.controllers.popUps.DeletePopUp
import com.nhlstenden.morithij.budgettracker.controllers.popUps.UpdatePopUp
import com.nhlstenden.morithij.budgettracker.models.ExpenseModel
import com.nhlstenden.morithij.budgettracker.models.UserInfoModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.AnchorPane
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ExpenseController() : Controller() {

    @FXML
    lateinit var anchorPane : AnchorPane
    private lateinit var userInfo : UserInfoModel
    @FXML
    private lateinit var lineChart: LineChart<Number, Number>
    @FXML
    private lateinit var startDatePicker : DatePicker
    @FXML
    private lateinit var endDatePicker : DatePicker


    @FXML
    // This function is called when the FXML file is loaded
    fun initialize() {
        val thread = Thread {
            val dao = DAOFactory.getDAO(UserInfoModel::class.java) as DAO<UserInfoModel>
            userInfo = dao.get(1) as UserInfoModel

            val expenseDAO = DAOFactory.getDAO(ExpenseModel::class.java) as DAO<ExpenseModel>
            val expenses = expenseDAO.getAll()

            Platform.runLater{
                anchorPane.setOnKeyPressed { event ->
                    if (event.code == KeyCode.C) {
                        CreatePopUp(userInfo)
                    }else if(event.code == KeyCode.U){
                        UpdatePopUp(userInfo)
                    }else if(event.code == KeyCode.D){
                        DeletePopUp(userInfo)
                    }
                }

                startDatePicker.setOnAction {updateChart(expenses)}
                endDatePicker.setOnAction { updateChart(expenses)}

                // Add data to the chart
                val series = generatePeriodicSeriesData(expenses)

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
            }
        }
        thread.start()
    }

    private fun updateChart(records: List<ExpenseModel>) {
        try {
            val startDate = startDatePicker.value?.atStartOfDay()
            val endDate = endDatePicker.value?.atTime(23, 59, 59)

            // Generate the series data based on the filtered records
            val series = generatePeriodicSeriesData(records, startDate, endDate)

            // Clear the existing data and add the updated series to the chart
            lineChart.data.clear()
            lineChart.data.add(series)
        } catch (e: Exception) {
            e.printStackTrace()
            lineChart.data.clear()
        }
    }

    // Generates a series of data for the line chart
    private fun generatePeriodicSeriesData(records: Collection<ExpenseModel>, beginDate: LocalDateTime? = null, endDate: LocalDateTime? = null): XYChart.Series<Number, Number> {
        val sortedRecords = records.sortedBy { it.recordDate }

        // Determine beginDate and endDate if not provided
        val earliestDate = sortedRecords.firstOrNull()?.recordDate?.atStartOfDay()
        val latestDate = sortedRecords.lastOrNull()?.recordDate?.atStartOfDay()

        val actualBeginDate = beginDate ?: earliestDate ?: LocalDateTime.now()
        val actualEndDate = endDate ?: latestDate ?: LocalDateTime.now()

        startDatePicker.value = actualBeginDate?.toLocalDate()
        endDatePicker.value = actualEndDate?.toLocalDate()

        if(!actualBeginDate.isBefore(actualEndDate)){
            return XYChart.Series<Number, Number>()
        }

        val filteredRecords =
            sortedRecords.filter { it.recordDate.atStartOfDay() in actualBeginDate..actualEndDate }
        val duration = Duration.between(actualBeginDate, actualEndDate)
        val numberOfDivisions = filteredRecords.size
        if(filteredRecords.isEmpty()){
            return XYChart.Series<Number, Number>()
        }
        try {
            val periodDuration = duration.dividedBy(numberOfDivisions.toLong())
            val series = XYChart.Series<Number, Number>()

            var currentPeriodStart = actualBeginDate

            var currentPeriodEnd = currentPeriodStart.plus(periodDuration)

            var sum = 0.0
            val recordsIterator = filteredRecords.iterator()
            var record: ExpenseModel? = if (recordsIterator.hasNext()) recordsIterator.next() else null

            repeat(numberOfDivisions + 1) { periodIndex ->
                while (record != null && record!!.recordDate.atStartOfDay() < currentPeriodEnd) {
                    sum += record!!.money
                    record = if (recordsIterator.hasNext()) recordsIterator.next() else null
                }

                val data = XYChart.Data<Number, Number>(currentPeriodStart.toEpochSecond(ZoneOffset.UTC), sum)
                series.data.add(data)
                currentPeriodStart = currentPeriodEnd
                currentPeriodEnd = currentPeriodEnd.plus(periodDuration)

            }
            return series
        } catch (e: ArithmeticException) {
            e.printStackTrace()
        }
        return XYChart.Series<Number, Number>()
    }
}