
package de.htwg.se.TradingGame.view.GUI.Stages.BacktestStageFolder



import de.htwg.se.TradingGame.controller.IController
import de.htwg.se.TradingGame.view.GUI.Stages.BacktestStageFolder._
import javafx.collections.FXCollections
import javafx.scene.{chart => jfxsc}
import javafx.scene.{layout => jfxsl}
import javafx.scene.{shape => jfxss}
import javafx.{scene => jfxs}
import org.checkerframework.checker.units.qual.g
import org.checkerframework.checker.units.qual.s
import play.api.libs.json.JsArray
import scalafx.Includes._
import scalafx.animation.FadeTransition
import scalafx.application.JFXApp
import scalafx.application.JFXApp3
import scalafx.application.Platform
import scalafx.beans.property.DoubleProperty
import scalafx.beans.value.ObservableValue
import scalafx.collections.ObservableBuffer
import scalafx.concurrent.Task
import scalafx.event.ActionEvent
import scalafx.event.EventHandler
import scalafx.geometry.Point2D
import scalafx.scene.Node
import scalafx.scene.Scene
import scalafx.scene.chart.Axis
import scalafx.scene.chart.CategoryAxis
import scalafx.scene.chart.NumberAxis
import scalafx.scene.chart.ValueAxis
import scalafx.scene.chart.XYChart
import scalafx.scene.control.Label
import scalafx.scene.control.Tooltip
import scalafx.scene.input.ContextMenuEvent
import scalafx.scene.input.MouseEvent
import scalafx.scene.input.ScrollEvent
import scalafx.scene.layout.GridPane
import scalafx.scene.layout.Pane
import scalafx.scene.layout.Region
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.scene.shape.Line
import scalafx.scene.shape.LineTo
import scalafx.scene.shape.MoveTo
import scalafx.scene.shape.Path

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import scala.collection.mutable
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.TreeMap
import scala.compiletime.ops.boolean
import scala.jdk.CollectionConverters._


class DraggableCandleStickChart (controller: IController) extends StackPane {
  val size = 5004
  val gameStateManager = controller.interpreter.gameStateManager
  val chartData = new ChartData.ChartData(size, gameStateManager)
  val candleData = chartData.initialize(gameStateManager.currentState.backtestDate)
  val candleStickChart = createChart(candleData)
  updateXYAxis
  setBoundsToBacktestDate
  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  var numbercandles = 0
  var horizontalLines: List[Line] = List()
  var startX = 0.0
  var startY = 0.0
  var linePrices: Map[Line, String] = Map()
  var lineStartXs: Map[Line, Double] = Map()
  var entryprice = ""
  var entryLine: Line = new Line()
  var stopLossPrice = ""
  var takeProfitPrice = ""
  var stopLossLine: Line = new Line()
  var takeProfitLine: Line = new Line()
  var dragStartX: Double = 0
  var dragStartY: Double = 0

  candleStickChart.verticalGridLinesVisible = false
  candleStickChart.horizontalGridLinesVisible = false
  children.add(candleStickChart)
  val plotBackground = candleStickChart.lookup(".chart-plot-background")

  if (plotBackground != null) 
    plotBackground.setStyle("-fx-background-color: #020209;")
  
  def update = candleStickChart.layoutPlotChildren()
  def getxUpperBound = candleStickChart.getXAxis.asInstanceOf[javafx.scene.chart.NumberAxis].getClass.getMethod("getUpperBound").invoke(candleStickChart.getXAxis.asInstanceOf[javafx.scene.chart.NumberAxis]).asInstanceOf[Double]
  def getyUpperBound = candleStickChart.getYAxis.asInstanceOf[javafx.scene.chart.NumberAxis].getClass.getMethod("getUpperBound").invoke(candleStickChart.getYAxis.asInstanceOf[javafx.scene.chart.NumberAxis]).asInstanceOf[Double]
  def getxLowerBound = candleStickChart.getXAxis.asInstanceOf[javafx.scene.chart.NumberAxis].getClass.getMethod("getLowerBound").invoke(candleStickChart.getXAxis.asInstanceOf[javafx.scene.chart.NumberAxis]).asInstanceOf[Double]
  def getyLowerBound = candleStickChart.getYAxis.asInstanceOf[javafx.scene.chart.NumberAxis].getClass.getMethod("getLowerBound").invoke(candleStickChart.getYAxis.asInstanceOf[javafx.scene.chart.NumberAxis]).asInstanceOf[Double]
  def setxUpperBound(value: Double) = candleStickChart.getXAxis.asInstanceOf[javafx.scene.chart.NumberAxis].getClass.getMethod("setUpperBound", classOf[Double]).invoke(candleStickChart.getXAxis.asInstanceOf[javafx.scene.chart.NumberAxis], value.asInstanceOf[AnyRef])
  def setyUpperBound(value: Double) = candleStickChart.getYAxis.asInstanceOf[javafx.scene.chart.NumberAxis].getClass.getMethod("setUpperBound", classOf[Double]).invoke(candleStickChart.getYAxis.asInstanceOf[javafx.scene.chart.NumberAxis], value.asInstanceOf[AnyRef])
  def setxLowerBound(value: Double) = candleStickChart.getXAxis.asInstanceOf[javafx.scene.chart.NumberAxis].getClass.getMethod("setLowerBound", classOf[Double]).invoke(candleStickChart.getXAxis.asInstanceOf[javafx.scene.chart.NumberAxis], value.asInstanceOf[AnyRef])
  def setyLowerBound(value: Double) = candleStickChart.getYAxis.asInstanceOf[javafx.scene.chart.NumberAxis].getClass.getMethod("setLowerBound", classOf[Double]).invoke(candleStickChart.getYAxis.asInstanceOf[javafx.scene.chart.NumberAxis], value.asInstanceOf[AnyRef])
  def xAxis = candleStickChart.xAxis.delegate
  def yAxis = candleStickChart.yAxis.delegate

  def setBoundsToBacktestDate: Unit = 
    setxLowerBound(gameStateManager.currentState.backtestDate - gameStateManager.currentState.distancecandles*200)
    setxUpperBound(gameStateManager.currentState.backtestDate)
    setyLowerBound(candleData.minBy(_.low).low)
    setyUpperBound(candleData.maxBy(_.high).high)
    updateXYAxis

  def updateCandleStickChartAxis(candleData: ListBuffer[CandleStick]): Unit = 
    val minDatax = candleData.takeRight(200).minBy(_.day).day
    val maxDatax = candleData.takeRight(200).maxBy(_.day).day
    val minDatay = candleData.takeRight(200).minBy(_.low).low
    val maxDatay = candleData.takeRight(200).maxBy(_.high).high
    setxLowerBound(minDatax)
    setxUpperBound(maxDatax)
    setyLowerBound(minDatay)
    setyUpperBound(maxDatay)
    updateXYAxis 

  def createChart(candleData: ListBuffer[CandleStick]): AdvCandleStickChartSample.CandleStickChart = 
    val cssURL = this.getClass.getResource("/de/htwg/se/TradingGame/view/GUI/CSS/AdvCandleStickChartSample.css")
    if (cssURL != null) 
      val css = cssURL.toExternalForm
      val xAxis = new NumberAxis(candleData(0).day, candleData(0).day,1) 
      val yAxis = new NumberAxis(candleData(0).low, candleData(0).high, 1)
      val dataPoints = candleData.map { d => XYChart.Data[Number, Number](d.day, d.open, d)}
      val series = XYChart.Series[Number, Number]("Candles", ObservableBuffer(dataPoints.toSeq: _*))
      val chart = new AdvCandleStickChartSample.CandleStickChart(gameStateManager,xAxis, yAxis) {
        data = ObservableBuffer(series)
        getStylesheets += css
      }
      chart 
    else
      println("Resource not found: AdvCandleStickChartSample.css")
      null
  
  def addDataLeftwhenNeeded: Unit = 
    if(chartData.lowestLoadedDate >= getxLowerBound && candleStickChart.getData.size() == 1) 
      val candleData = chartData.getLowerThird
      val dataPoints = candleData.map { d => XYChart.Data[Number, Number](d.day, d.open, d)}
      val series = XYChart.Series[Number, Number](ObservableBuffer(dataPoints.toSeq: _*))
      candleStickChart.getData.add(0,series)
    else if(chartData.lowestLoadedDate - ((size/9) * gameStateManager.currentState.distancecandles) > getxUpperBound && candleStickChart.getData.size() == 2)
      val data = candleStickChart.getData
      data.remove(1) 
      val lowestSeries = data.minBy(series => series.getData.get(0).getXValue.longValue())
      val lowestDataPoint = lowestSeries.getData.minBy(dataPoint => dataPoint.getXValue.longValue())
      chartData.highestLoadedDate = chartData.lowestLoadedDate
      chartData.alwayshighestLoadedDate = chartData.highestLoadedDate
      chartData.lowestLoadedDate = lowestDataPoint.getXValue.longValue()
    else if(candleStickChart.getData.size() == 2 && chartData.highestLoadedDate - ((2*(size/9)) * gameStateManager.currentState.distancecandles) < getxLowerBound && chartData.alwayslowestLoadedDate != chartData.lowestLoadedDate)
      chartData.alwayslowestLoadedDate = chartData.lowestLoadedDate
      candleStickChart.getData.remove(0)
      chartData.moveBufferRight
  
  def addDataRightWhenNeeded: Unit = 
    if(chartData.highestLoadedDate <= getxUpperBound && candleStickChart.getData.size() == 1) 
      val candleData = chartData.getUpperThird
      val dataPoints = candleData.map { d => XYChart.Data[Number, Number](d.day, d.open, d)}
      val series = XYChart.Series[Number, Number](ObservableBuffer(dataPoints.toSeq: _*))
      candleStickChart.getData.add(1, series)
    else if(chartData.highestLoadedDate +((size/9) * gameStateManager.currentState.distancecandles) < getxLowerBound && candleStickChart.getData.size() == 2)
      val data = candleStickChart.getData
      data.remove(0)
      val highestSeries = data.maxBy(series => series.getData.get(0).getXValue.longValue())
      val highestDataPoint = highestSeries.getData.maxBy(dataPoint => dataPoint.getXValue.longValue())
      chartData.lowestLoadedDate = chartData.highestLoadedDate
      chartData.highestLoadedDate = highestDataPoint.getXValue.longValue()
      chartData.alwayslowestLoadedDate = chartData.lowestLoadedDate
    else if(candleStickChart.getData.size() == 2 && chartData.lowestLoadedDate + ((2*(size/9)) * gameStateManager.currentState.distancecandles) > getxUpperBound && chartData.alwayshighestLoadedDate != chartData.highestLoadedDate)
      chartData.alwayshighestLoadedDate = chartData.highestLoadedDate
      candleStickChart.getData.remove(1)
      chartData.moveBufferLeft
  
  def addDataWhenNeeded: Unit = 
    addDataLeftwhenNeeded
    addDataRightWhenNeeded
  
  def setupperboundxtolastdata(candleData: ListBuffer[CandleStick]): Unit = 
    val lastDataPoint = candleData.last.day
    setxUpperBound(lastDataPoint)
  
  def calculateYCoordinate(price: String): Double = 
    val formattedPrice = price.replace(",", ".")
    val y = yAxis.getDisplayPosition(formattedPrice.toDouble)
    y
  
  def calculateYPrice(me: MouseEvent): String = 
    val yInAxisCoords = yAxis.sceneToLocal(me.getSceneX, me.getSceneY).getY
    val price = yAxis.getValueForDisplay(yInAxisCoords).doubleValue()
    f"$price%.5f"
  
  def calculateXCoordinate(date: Double): Double = 
    val x = xAxis.getDisplayPosition(date)
    x

  def calculateXDate(me: MouseEvent): Double = 
    val xInAxisCoords = xAxis.sceneToLocal(me.getSceneX, me.getSceneY).getX
    val date = xAxis.getValueForDisplay(xInAxisCoords).intValue()
    date

  
  def plotHorizontalLine(me: MouseEvent): Unit = 
    val point = this.sceneToLocal(new Point2D(me.sceneX, me.sceneY))
    startX = point.x
    startY = point.y
    val y = me.getY - 15
    val chartWidth = candleStickChart.width.value
    val line = new Line()
    line.startX = 0
    line.endX = chartWidth * 4
    line.startY = y
    line.endY = y
    line.setStroke(Color.WHITE)
    line.setStrokeWidth(2)
    val price = calculateYPrice(me)
    linePrices = linePrices + (line -> price)
    horizontalLines = line :: horizontalLines
    candleStickChart.addCustomNode(line)
    var dragDeltaX = 0.0
    var dragDeltaY = 0.0
    line.setOnMousePressed((event: MouseEvent) => 
      dragDeltaX = event.getSceneX
      dragDeltaY = event.getSceneY
      event.consume()
    )
    line.setOnContextMenuRequested((event: ContextMenuEvent) => 
      candleStickChart.plotChildren.remove(line)
      horizontalLines = horizontalLines.filterNot(_ == line)
      linePrices = linePrices - line
      lineStartXs = lineStartXs - line
      event.consume()
    )
    line.setOnMouseDragged((event: MouseEvent) => 
      val deltaY = event.getSceneY - dragDeltaY
      line.setStartY(line.getStartY + deltaY)
      line.setEndY(line.getEndY + deltaY)
      dragDeltaY = event.getSceneY
      val newPrice = calculateYPrice(event)
      linePrices = linePrices + (line -> newPrice)
      event.consume()
    )
        
  def plotHorizontalStartLine(me: MouseEvent): Unit = 
    val point = this.sceneToLocal(new Point2D(me.sceneX, me.sceneY))
    startX = point.x
    startY = point.y
    val y = me.getY - 15
    val chartWidth = candleStickChart.width.value
    val line = new Line() 
    line.startX = startX - 50
    line.endX = chartWidth * 4
    line.startY = y
    line.endY = y
    line.setStroke(Color.WHITE)
    line.setStrokeWidth(2)
    val price = calculateYPrice(me)
    val date = calculateXDate(me)
    linePrices = linePrices + (line -> price)
    lineStartXs = lineStartXs + (line -> date)
    horizontalLines = line :: horizontalLines
    candleStickChart.addCustomNode(line)
    var dragDeltaX = 0.0
    var dragDeltaY = 0.0
    line.setOnMousePressed((event: MouseEvent) => 
      dragDeltaX = event.getSceneX
      dragDeltaY = event.getSceneY
      event.consume()
    )
    line.setOnContextMenuRequested((event: ContextMenuEvent) => 
      candleStickChart.plotChildren.remove(line)
      horizontalLines = horizontalLines.filterNot(_ == line)
      linePrices = linePrices - line
      lineStartXs = lineStartXs - line
      event.consume()
    )
    line.setOnMouseDragged((event: MouseEvent) => 
      val deltaY = event.getSceneY - dragDeltaY
      val deltaX = event.getSceneX - dragDeltaX
      line.setStartY(line.getStartY + deltaY)
      line.setEndY(line.getEndY + deltaY)
      line.setStartX(line.getStartX + deltaX)
      dragDeltaY = event.getSceneY
      dragDeltaX = event.getSceneX
      val newPrice = calculateYPrice(event)
      val newDate = calculateXDate(event)
      linePrices = linePrices + (line -> newPrice)
      lineStartXs = lineStartXs + (line -> newDate)
      event.consume()
    )
  
  def entryline(price: String): Unit = 
    val y = calculateYCoordinate(price)
    val chartWidth = candleStickChart.width.value
    entryLine.startX = 0
    entryLine.endX = chartWidth * 4
    entryLine.startY = y
    entryLine.endY = y
    entryLine.setStroke(Color.YELLOW)
    entryLine.setStrokeWidth(3)
    linePrices = linePrices + (entryLine -> price)
    horizontalLines = entryLine :: horizontalLines
    candleStickChart.addCustomNode(entryLine)
    entryprice = price
    var dragDeltaX = 0.0
    var dragDeltaY = 0.0
    entryLine.setOnMousePressed((event: MouseEvent) => 
      dragDeltaX = event.getSceneX
      dragDeltaY = event.getSceneY
      event.consume()
    )
    entryLine.setOnMouseDragged((event: MouseEvent) => 
      val deltaY = event.getSceneY - dragDeltaY
      entryLine.setStartY(entryLine.getStartY + deltaY)
      entryLine.setEndY(entryLine.getEndY + deltaY)
      dragDeltaY = event.getSceneY
      val newPrice = calculateYPrice(event)
      linePrices = linePrices + (entryLine -> newPrice)
      entryprice = newPrice
      event.consume()
    )
  
  def stopLossLine(price: String): Unit = 
    val y = calculateYCoordinate(price)
    val chartWidth = candleStickChart.width.value
    var dragDeltaX = 0.0
    var dragDeltaY = 0.0
    stopLossLine.startX = 0
    stopLossLine.endX = chartWidth * 4
    stopLossLine.startY = y
    stopLossLine.endY = y
    stopLossLine.setStroke(Color.RED)
    stopLossLine.setStrokeWidth(3)
    linePrices = linePrices + (stopLossLine -> price)
    horizontalLines = stopLossLine :: horizontalLines
    candleStickChart.addCustomNode(stopLossLine)
    stopLossPrice = price
    stopLossLine.setOnMousePressed((event: MouseEvent) => 
      dragDeltaX = event.getSceneX
      dragDeltaY = event.getSceneY
      event.consume()
    )
    stopLossLine.setOnMouseDragged((event: MouseEvent) => 
      val deltaY = event.getSceneY - dragDeltaY
      stopLossLine.setStartY(stopLossLine.getStartY + deltaY)
      stopLossLine.setEndY(stopLossLine.getEndY + deltaY)
      dragDeltaY = event.getSceneY
      val newPrice = calculateYPrice(event)
      linePrices = linePrices + (stopLossLine -> newPrice)
      stopLossPrice = newPrice
      event.consume()
    )

  def takeProfitLine(price: String): Unit = 
    val y = calculateYCoordinate(price)
    val chartWidth = candleStickChart.width.value
    var dragDeltaX = 0.0
    var dragDeltaY = 0.0
    takeProfitLine.startX = 0
    takeProfitLine.endX = chartWidth * 4
    takeProfitLine.startY = y
    takeProfitLine.endY = y
    takeProfitLine.setStroke(Color.GREEN)
    takeProfitLine.setStrokeWidth(3)
    linePrices = linePrices + (takeProfitLine -> price)
    horizontalLines = takeProfitLine :: horizontalLines
    candleStickChart.addCustomNode(takeProfitLine)
    takeProfitPrice = price
    takeProfitLine.setOnMousePressed((event: MouseEvent) => 
      dragDeltaX = event.getSceneX
      dragDeltaY = event.getSceneY
      event.consume()
    )
    takeProfitLine.setOnMouseDragged((event: MouseEvent) => 
      val deltaY = event.getSceneY - dragDeltaY
      takeProfitLine.setStartY(takeProfitLine.getStartY + deltaY)
      takeProfitLine.setEndY(takeProfitLine.getEndY + deltaY)
      dragDeltaY = event.getSceneY
      val newPrice = calculateYPrice(event)
      linePrices = linePrices + (takeProfitLine -> newPrice)
      takeProfitPrice = newPrice
      event.consume()
    )
  def updateOnMousePress(me: MouseEvent): Unit =
    dragStartX = me.getX
    dragStartY = me.getY
    
  def updateXYAxis: Unit = 
    candleStickChart.getXAxis.asInstanceOf[javafx.scene.chart.NumberAxis].setTickUnit(0.10 * (getxUpperBound - getxLowerBound))
    candleStickChart.getYAxis.asInstanceOf[javafx.scene.chart.NumberAxis].setTickUnit(0.10 * (getyUpperBound - getyLowerBound))

  def updateAllLines(): Unit = 
    for ((line, price) <- linePrices) 
      val y = calculateYCoordinate(price)
      line.startY = y
      line.endY = y
    for ((line, startX) <- lineStartXs) 
      val x = calculateXCoordinate(startX)
      line.setStartX(x)

  def updateOnDrag(me: MouseEvent): Unit =
    addDataWhenNeeded
    updateXYAxis
    val dragEndX = me.getX
    val dragEndY = me.getY
    val diffX = dragEndX - dragStartX
    val diffY = dragEndY - dragStartY
    if (dragEndX < 50) 
      val zoomFactor = 0.01
      val dragY = dragEndY - dragStartY
      val yAxisLowerBound = getyLowerBound
      val yAxisUpperBound = getyUpperBound
      val range = yAxisUpperBound - yAxisLowerBound
      val zoomStep = range * zoomFactor
      if (dragY < 0) 
        setyLowerBound(yAxisLowerBound + zoomStep)
        setyUpperBound(yAxisUpperBound - zoomStep)
      else
        setyLowerBound(yAxisLowerBound - zoomStep)
        setyUpperBound(yAxisUpperBound + zoomStep)
      else 
        val yAxisLowerBound = getyLowerBound
        val yAxisUpperBound = getyUpperBound
        val yAxisHeight = candleStickChart.yAxis.delegate.getHeight
        val yAxisRange = yAxisUpperBound - yAxisLowerBound
        val pixelToYAxisUnitRatio = yAxisRange / yAxisHeight
        val yDiffPixels = dragEndY - dragStartY
        val yDiff = yDiffPixels * pixelToYAxisUnitRatio
        setyLowerBound(yAxisLowerBound + yDiff)
        setyUpperBound(yAxisUpperBound + yDiff)
        val xAxisLowerBound = getxLowerBound
        val xAxisUpperBound = getxUpperBound
        val xAxisWidth = candleStickChart.xAxis.delegate.getWidth
        val xAxisRange = xAxisUpperBound - xAxisLowerBound
        val pixelToXAxisUnitRatio = xAxisRange / xAxisWidth
        val xDiffPixels = dragEndX - dragStartX
        val xDiff = xDiffPixels * pixelToXAxisUnitRatio
        setxLowerBound(getxLowerBound - xDiff)
        setxUpperBound(getxUpperBound - xDiff)
    dragStartX = dragEndX
    dragStartY = dragEndY
    
  def updateOnMouseRelease(): Unit =
    dragStartX = 0
    dragStartY = 0
    
  def updateOnScroll(event: ScrollEvent): Unit = 
    addDataLeftwhenNeeded
    updateXYAxis
    val zoomFactor = 0.1
    val deltaY = event.deltaY
    val xAxisLowerBound = getxLowerBound
    val xAxisUpperBound = getxUpperBound
    val range = xAxisUpperBound - xAxisLowerBound
    val zoomStep = range * zoomFactor
    val xAxisWidth = candleStickChart.xAxis.delegate.getWidth
    val zoomhighlowlines = xAxisWidth * zoomFactor
    val maxCandles = size/10
    val distanceCandles = gameStateManager.currentState.distancecandles
    numbercandles = (range / distanceCandles).toInt
    if (deltaY < 0) 
      if (range >= (maxCandles * distanceCandles)) 
        return
      setxLowerBound(xAxisLowerBound - zoomStep)
    else
      setxLowerBound(xAxisLowerBound + zoomStep)
}
