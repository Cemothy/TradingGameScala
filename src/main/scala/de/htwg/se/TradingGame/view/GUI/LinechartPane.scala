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


    // Set up event handlers for mouse interactions
    onMousePressed = (event: MouseEvent) => {
      val point = sceneToLocal(new Point2D(event.sceneX, event.sceneY))
      startX = point.getX
      startY = point.getY
    }

    onMouseDragged = (event: MouseEvent) => {
      val point = chartPane.sceneToLocal(new Point2D(event.sceneX, event.sceneY))

      if (point.getX > lineChart.width.value - 50) {
        // Zooming in or out
        val zoomFactor = 0.01 // Adjust this value to control the zoom speed
        val dragY = point.getY - startY
        val yAxisLowerBound = yAxis.getLowerBound
        val yAxisUpperBound = yAxis.getUpperBound
        val range = yAxisUpperBound - yAxisLowerBound
        val zoomStep = range * zoomFactor

        if (dragY < 0) {
          // Dragging up, show lower numbers
          val newLowerBound = yAxisLowerBound + zoomStep
          val newUpperBound = yAxisUpperBound - zoomStep
          yAxis.setLowerBound(newLowerBound)
          yAxis.setUpperBound(newUpperBound)
        } else {
          // Dragging down, show higher numbers
          val newLowerBound = yAxisLowerBound - zoomStep
          val newUpperBound = yAxisUpperBound + zoomStep
          yAxis.setLowerBound(newLowerBound)
          yAxis.setUpperBound(newUpperBound)
        }
      } else {
        // Panning
        val yAxisHeight = yAxis.getHeight
        val yAxisRange = yAxis.getUpperBound - yAxis.getLowerBound
        val pixelToYAxisUnitRatio = yAxisRange / yAxisHeight

        val yDiffPixels = point.getY - startY
        val yDiff = yDiffPixels * pixelToYAxisUnitRatio

        val yAxisLowerBound = yAxis.getLowerBound
        val yAxisUpperBound = yAxis.getUpperBound

        yAxis.setLowerBound(yAxisLowerBound + yDiff)
        yAxis.setUpperBound(yAxisUpperBound + yDiff)

        val xAxisWidth = xAxis.getWidth
        val xAxisRange = xAxis.getUpperBound - xAxis.getLowerBound
        val pixelToXAxisUnitRatio = xAxisRange / xAxisWidth

        val xDiffPixels = point.getX - startX
        val xDiff = xDiffPixels * pixelToXAxisUnitRatio

        val xAxisLowerBound = xAxis.getLowerBound
        val xAxisUpperBound = xAxis.getUpperBound

        xAxis.delegate.setLowerBound(xAxisLowerBound - xDiff)
        xAxis.delegate.setUpperBound(xAxisUpperBound - xDiff)

        crosshairVerticalLine.setStartX(crosshairVerticalLine.getStartX + xDiffPixels)
        crosshairVerticalLine.setEndX(crosshairVerticalLine.getEndX + xDiffPixels)
        crosshairHorizontalLine.setStartY(crosshairHorizontalLine.getStartY + yDiffPixels)
        crosshairHorizontalLine.setEndY(crosshairHorizontalLine.getEndY + yDiffPixels)
      }

      updateYAxis()
      startX = point.getX
      startY = point.getY
    }

    lineChart.onScroll = (event: ScrollEvent) => {
      val zoomFactor = 0.1 // Adjust this value to control the zoom speed

      val deltaY = event.getDeltaY
      val xAxisLowerBound = xAxis.getLowerBound
      val xAxisUpperBound = xAxis.getUpperBound
      val range = xAxisUpperBound - xAxisLowerBound
      val zoomStep = range * zoomFactor
      val xAxisWidth = xAxis.getWidth
      val zoomhighlowlines = xAxisWidth * zoomFactor

      if (deltaY < 0) {
        // Zoom out
        val newLowerBound = xAxisLowerBound - zoomStep
        xAxis.setLowerBound(newLowerBound)
      } else {
        // Zoom in
        val newLowerBound = xAxisLowerBound + zoomStep
        xAxis.setLowerBound(newLowerBound)
      }
    }

    

    children.addAll(chartPane)

    updateYAxis()
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