package com.nhlstenden.morithij.budgettracker.controllers

import com.nhlstenden.morithij.budgettracker.models.BudgetModel
import com.nhlstenden.morithij.budgettracker.models.ExpenseModel
import com.nhlstenden.morithij.budgettracker.models.UserInfoModel
import com.nhlstenden.morithij.budgettracker.models.dao.DAO
import com.nhlstenden.morithij.budgettracker.models.dao.DAOFactory
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.DatePicker
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import java.text.DecimalFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class ExpenseController() : Controller(), Observer{

    @FXML
    lateinit var anchorPane : AnchorPane
    private lateinit var userInfo : UserInfoModel
    @FXML
    private lateinit var lineChart: LineChart<Number, Number>
    @FXML
    private lateinit var startDatePicker : DatePicker
    @FXML
    private lateinit var endDatePicker : DatePicker
    private lateinit var allBudgets : List<BudgetModel>
    @FXML
    private lateinit var totalBudgetLabel : Label
    @FXML
    private lateinit var currentBudgetLabel : Label

    override val title = super.title + " - Expenses"

    @FXML
    fun initialize() {
        val thread = Thread {
            val dao = DAOFactory.getDAO(UserInfoModel::class.java) as DAO<UserInfoModel>
            // Hardcoded because multiple users was scratched
            userInfo = dao.get(1) as UserInfoModel

            val expenseDAO = DAOFactory.getDAO(ExpenseModel::class.java) as DAO<ExpenseModel>
            val expenses = expenseDAO.getAll()

            val budgetDAO = DAOFactory.getDAO(BudgetModel::class.java) as DAO<BudgetModel>
            allBudgets = budgetDAO.getAll()
            setCurrentAndTotal(allBudgets)
            Platform.runLater{
                startDatePicker.setOnAction {updateChart(expenses)}
                endDatePicker.setOnAction { updateChart(expenses)}

                // Generate data for the chart
                val series = generatePeriodicSeriesData(expenses)
                lineChart.data.add(series)
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
                lineChart.isLegendVisible = false
            }
        }
        thread.start()
    }

    private fun setCurrentAndTotal(budgets: List<BudgetModel>){
        // Updates the list of all budgets
        allBudgets = budgets
        // Calculates the total of all totals and currents of the budgets
        val totalBudget = allBudgets.sumOf { it.totalBudget }
        val currentBudget = allBudgets.sumOf { it.currentBudget }
        val currency = Currency.getInstance("EUR").symbol //Can just hardcode this since we dropped support for multiple currencies.
        Platform.runLater {
            totalBudgetLabel.text = currency + DecimalFormat("#,##0.00").format(totalBudget)
            currentBudgetLabel.text = currency + DecimalFormat("#,##0.00").format(currentBudget)
        }
    }

    // Used to update the chart when the date pickers are used to select what period needs to be shown
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

        // Sets the dates to the specified date.
        // If no dates are specified, the earliest and latest record dates are taken.
        // If that fails, the current date is taken.
        val actualBeginDate = beginDate ?: earliestDate ?: LocalDateTime.now()
        val actualEndDate = endDate ?: latestDate ?: LocalDateTime.now()

        startDatePicker.value = actualBeginDate?.toLocalDate()
        endDatePicker.value = actualEndDate?.toLocalDate()

        if(!actualBeginDate.isBefore(actualEndDate)){
            return XYChart.Series<Number, Number>()
        }

        // Filters the records to only show the ones between the selected dates.
        val filteredRecords =
            sortedRecords.filter { it.recordDate.atStartOfDay() in actualBeginDate..actualEndDate }
        // Divides the period between the start and end-date in even parts to make up the x-axis values
        val duration = Duration.between(actualBeginDate, actualEndDate)
        val numberOfDivisions = filteredRecords.size
        if(filteredRecords.isEmpty()){
            return XYChart.Series<Number, Number>()
        }
        try {
            //calculates the total expenses per period. Each period becomes a point in the graph
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

    override fun update(obj: Any) {
        // When one of the publishers notifies ExpenseController, the chart is updated.
        val chartThread = Thread{
            val dao = DAOFactory.getDAO(ExpenseModel::class.java) as DAO<ExpenseModel>
            updateChart(dao.getAll())
        }
        chartThread.start()

        // When the notice from the publishers consists of a budgetID and a money value, the current budget should be updated
        if(obj is Pair<*, *>){
            val pair = obj as Pair<Int, Double>
            val currentBudgetThread = Thread{
                val dao = DAOFactory.getDAO(BudgetModel::class.java) as DAO<BudgetModel>
                val oldBudget = dao.get(pair.first)
                if(oldBudget != null){
                    dao.update(BudgetModel(oldBudget.totalBudget, (oldBudget.currentBudget + pair.second), oldBudget.description, oldBudget.currency, pair.first))
                }
                setCurrentAndTotal(dao.getAll())
            }
            currentBudgetThread.start()

        }
    }
}