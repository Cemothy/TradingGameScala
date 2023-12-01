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



  def initializeLineChart(): Unit = {
    // Set up the line chart and crosshair lines
    lineChart.setAnimated(false)
    lineChart.setCreateSymbols(false)
    lineChart.setLegendVisible(false)
    lineChart.verticalGridLinesVisible = false
    lineChart.horizontalGridLinesVisible = false
    lineChart.setMaxSize(Double.MaxValue, Double.MaxValue)

    xAxis.delegate.setAutoRanging(false)
    yAxis.delegate.setAutoRanging(false)


    children.addAll(lineChart)
    
    val chartDataLoader = new ChartDataLoader()
    chartDataLoader.loadDataAndUpdateChart(lineChart, xAxis, yAxis, "EURUSD", 1000)
    updateYAxis()
    

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

}