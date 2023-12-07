package de.htwg.se.TradingGame.view.GUI
import scalafx.Includes._
import scalafx.geometry.Point2D
import scalafx.geometry.Pos
import scalafx.geometry.Side
import scalafx.scene.SceneIncludes.jfxLineChart2sfx
import scalafx.scene.chart.LineChart
import scalafx.scene.chart.NumberAxis
import scalafx.scene.input.KeyCode.C
import scalafx.scene.input.MouseEvent
import scalafx.scene.input.ScrollEvent
import scalafx.scene.layout.Pane
import scalafx.scene.layout.Priority
import scalafx.scene.layout.StackPane
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.shape.Line
import scalafx.scene.shape.Rectangle

class LinechartPane extends StackPane {
  val xAxis: NumberAxis = new NumberAxis()
  val yAxis: NumberAxis = new NumberAxis()
  val lineChart: LineChart[Number, Number] = new LineChart[Number, Number](xAxis, yAxis)
  private var currentSelectedTimeframe: String = "1m"
  private var selectedTicker: String = "EURUSD"
  private val chartDataLoader: ChartDataLoader = new ChartDataLoader()
  var lastPrice = chartDataLoader.lastPrice
  var selectedDate: java.time.LocalDateTime = java.time.LocalDateTime.now()
  
  def createLongPositionBox(entryPoint: Double, stopLoss: Double, takeProfit: Double): Unit = {
    val entryY = yAxis.getDisplayPosition(entryPoint)
    val stopLossY = yAxis.getDisplayPosition(stopLoss)
    val takeProfitY = yAxis.getDisplayPosition(takeProfit)

    val boxHeight = Math.abs(entryY - stopLossY)
    val boxWidth = 50 // Adjust this value as needed

    val box = new Rectangle {
      x = 10 // Adjust this value as needed
      y = Math.min(entryY, stopLossY)
      width = boxWidth
      height = boxHeight
      fill = Color.Green.opacity(0.3)
    }

    val profitLine = new Line {
      startX = box.x.value
      startY = takeProfitY
      endX = box.x.value + boxWidth
      endY = takeProfitY
      stroke = Color.Green
    }

    children.addAll(box, profitLine)
}
  def updateDate(selectedDate: java.time.LocalDateTime): Unit = {
  val updatedDate = selectedDate
  this.selectedDate = updatedDate
  chartDataLoader.loadDataAndUpdateChart(lineChart, xAxis, yAxis, selectedTicker, 1000, currentSelectedTimeframe, updatedDate)
  lastPrice = chartDataLoader.lastPrice
}

  def updateTimeframe(selectedTimeframe: String): Unit = {
    currentSelectedTimeframe = selectedTimeframe
    // Reload the chart data with the new timeframe
   

    chartDataLoader.loadDataAndUpdateChart(lineChart, xAxis, yAxis, selectedTicker, 1000, selectedTimeframe, selectedDate)
    lastPrice = chartDataLoader.lastPrice
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
    lastPrice = chartDataLoader.lastPrice

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