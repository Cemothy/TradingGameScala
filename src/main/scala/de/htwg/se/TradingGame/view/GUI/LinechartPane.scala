package de.htwg.se.TradingGame.view.GUI
import scalafx.Includes._
import scalafx.geometry.Point2D
import scalafx.geometry.Pos
import scalafx.geometry.Side
import scalafx.scene.SceneIncludes.jfxLineChart2sfx
import scalafx.scene.chart.LineChart
import scalafx.scene.chart.NumberAxis
import scalafx.scene.input.MouseEvent
import scalafx.scene.input.ScrollEvent
import scalafx.scene.layout.Pane
import scalafx.scene.layout.Priority
import scalafx.scene.layout.StackPane
import scalafx.scene.layout.VBox
import scalafx.scene.shape.Line

class LinechartPane extends StackPane {
  val xAxis: NumberAxis = new NumberAxis()
  val yAxis: NumberAxis = new NumberAxis()
  val lineChart: LineChart[Number, Number] = new LineChart[Number, Number](xAxis, yAxis)
  private var currentSelectedTimeframe: String = "1m"
  private var selectedTicker: String = "EURUSD"
  private val chartDataLoader: ChartDataLoader = new ChartDataLoader()
  var selectedDate: java.time.LocalDateTime = java.time.LocalDateTime.now()

  def updateDate(selectedDate: java.time.LocalDateTime): Unit = {
  val updatedDate = selectedDate
  this.selectedDate = updatedDate
  chartDataLoader.loadDataAndUpdateChart(lineChart, xAxis, yAxis, selectedTicker, 1000, currentSelectedTimeframe, updatedDate)
}

  def updateTimeframe(selectedTimeframe: String): Unit = {
    currentSelectedTimeframe = selectedTimeframe
    // Reload the chart data with the new timeframe


    chartDataLoader.loadDataAndUpdateChart(lineChart, xAxis, yAxis, selectedTicker, 1000, selectedTimeframe, selectedDate)
  }

  def initializeLineChart(ticker: String): Unit = {
    selectedTicker = ticker
    // Set up the line chart and crosshair lines
    lineChart.setAnimated(false)
    lineChart.setCreateSymbols(false)
    lineChart.setLegendVisible(false)
    lineChart.verticalGridLinesVisible = false
    lineChart.horizontalGridLinesVisible = false
    lineChart.setMaxSize(Double.MaxValue, Double.MaxValue)

    xAxis.delegate.setAutoRanging(false)
    yAxis.delegate.setAutoRanging(false)

    children.clear()
    children.addAll(lineChart)
    



    chartDataLoader.loadDataAndUpdateChart(lineChart, xAxis, yAxis, ticker, 1000, currentSelectedTimeframe, selectedDate)
    updateYAxis()
    updateXAxis()
    

    // Add event handlers for mouse enter and exit
  
  }

  private def updateYAxis(): Unit = {
    // Update the Y-axis tick labels
    val tickCount = 10 // Adjust this value to control the number of tick labels
    val yAxisLowerBound = yAxis.getLowerBound
    val yAxisUpperBound = yAxis.getUpperBound
    val range = yAxisUpperBound - yAxisLowerBound
    val tickUnit = range / tickCount

    yAxis.setTickUnit(tickUnit)
  }
  private def updateXAxis(): Unit = {
    // Update the Y-axis tick labels
    val tickCount = 10 // Adjust this value to control the number of tick labels
    val xAxisLowerBound = xAxis.getLowerBound
    val xAxisUpperBound = xAxis.getUpperBound
    val range = xAxisUpperBound - xAxisLowerBound
    val tickUnit = range / tickCount

    xAxis.setTickUnit(tickUnit)
  }

}