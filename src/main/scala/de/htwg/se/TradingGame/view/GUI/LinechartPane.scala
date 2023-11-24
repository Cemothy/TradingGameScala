package de.htwg.se.TradingGame.view.GUI
import scalafx.scene.chart.{LineChart, NumberAxis}
import scalafx.scene.layout.Pane
import scalafx.scene.shape.Line
import scalafx.scene.SceneIncludes.jfxLineChart2sfx
import scalafx.scene.layout.Priority
import scalafx.scene.layout.VBox
import scalafx.scene.layout.StackPane
import scalafx.geometry.Pos
import scalafx.geometry.Point2D
import scalafx.scene.input.MouseEvent
import scalafx.Includes._
import scalafx.scene.input.ScrollEvent
import scalafx.geometry.Side

class LinechartPane extends StackPane {
  private val xAxis: NumberAxis = new NumberAxis()
  private val yAxis: NumberAxis = new NumberAxis()
  private val lineChart: LineChart[Number, Number] = new LineChart[Number, Number](xAxis, yAxis)
  private val crosshairVerticalLine: Line = new Line()
  private val crosshairHorizontalLine: Line = new Line()
  private var startX: Double = 0.0
  private var startY: Double = 0.0

  def initializeLineChart(): Unit = {
    // Set up the line chart and crosshair lines
    lineChart.setAnimated(false)
    lineChart.setCreateSymbols(false)
    lineChart.setLegendVisible(false)

    crosshairVerticalLine.setStrokeWidth(1)
    crosshairVerticalLine.getStrokeDashArray.addAll(5d, 5d)
    crosshairVerticalLine.setStartY(0)
    crosshairVerticalLine.setEndY(lineChart.getHeight)

    crosshairHorizontalLine.setStrokeWidth(1)
    crosshairHorizontalLine.getStrokeDashArray.addAll(5d, 5d)
    crosshairHorizontalLine.setStartX(0)
    crosshairHorizontalLine.setEndX(lineChart.getWidth)

    val chartPane = new StackPane()
    chartPane.getChildren.addAll(lineChart, crosshairVerticalLine, crosshairHorizontalLine)

    children.addAll(chartPane)

    updateYAxis()

    // Add event handlers for mouse enter and exit
    lineChart.onMouseEntered = (event: MouseEvent) => showCrosshair()
    lineChart.onMouseExited = (event: MouseEvent) => hideCrosshair()
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

  private def showCrosshair(): Unit = {
    crosshairVerticalLine.setVisible(true)
    crosshairHorizontalLine.setVisible(true)
  }

  private def hideCrosshair(): Unit = {
    crosshairVerticalLine.setVisible(false)
    crosshairHorizontalLine.setVisible(false)
  }
}