/*
* Copyright 2013 ScalaFX Project
* All right reserved.
*/
package de.htwg.se.TradingGame.view.GUI

import de.htwg.se.TradingGame.view.GUI.AdvCandleStickChartSample.CandleStick
import de.htwg.se.TradingGame.view.GUI.AdvCandleStickChartSample.updateXYAxis
import de.htwg.se.TradingGame.view.GUI.GetAPINewsSentiment
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.scene.{chart => jfxsc}
import javafx.scene.{layout => jfxsl}
import javafx.scene.{shape => jfxss}
import javafx.{scene => jfxs}
import play.api.libs.json.JsArray
import scalafx.Includes._
import scalafx.animation.FadeTransition
import scalafx.application.JFXApp
import scalafx.application.JFXApp3
import scalafx.beans.property.DoubleProperty
import scalafx.beans.value.ObservableValue
import scalafx.collections.ObservableBuffer
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
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import scala.collection.mutable
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.TreeMap
import scala.jdk.CollectionConverters._
import scala.language.postfixOps


class DraggableCandleStickChart(candleStickChart: AdvCandleStickChartSample.CandleStickChart) extends StackPane {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private var currentSelectedTimeframe: String = "1m"
    private var selectedTicker: String = "EURUSD"
    var selectedDate: java.time.LocalDateTime = java.time.LocalDateTime.now()
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
    var circleEpochTimes: Map[Circle, Long] = Map()
    candleStickChart.verticalGridLinesVisible = false
    candleStickChart.horizontalGridLinesVisible = false
    children.add(candleStickChart)
    val plotBackground = candleStickChart.lookup(".chart-plot-background")
    if (plotBackground != null) {
    plotBackground.setStyle("-fx-background-color: #020209;")
    }
    var dragStartX: Double = 0
    var dragStartY: Double = 0

    def setupperboundxtolastdata(candleData: ListBuffer[CandleStick]): Unit = {
        // Get the last data point
        val lastDataPoint = candleData.last.day

        // Get the X axis
        val xAxis = candleStickChart.getXAxis

        // Set the upper bound of the X axis to the X value of the last data point
        val setUpperBoundMethodx = xAxis.getClass.getMethod("setUpperBound", classOf[Double])
        setUpperBoundMethodx.invoke(xAxis, lastDataPoint.asInstanceOf[AnyRef])
    }
    
     def calculateYCoordinate(price: String): Double = {
        // Get the y-axis
        val formattedPrice = price.replace(",", ".")
        val yAxis = candleStickChart.yAxis.delegate

        // Get the display position for the price
        val y = yAxis.getDisplayPosition(formattedPrice.toDouble)

        y
    }
    def calculateYPrice(me: MouseEvent): String = {
        // Get the y-axis
        
        val yAxis = candleStickChart.yAxis.delegate

        // Get the y-coordinate of the mouse event in the local coordinate system of the y-axis
        val yInAxisCoords = yAxis.sceneToLocal(me.getSceneX, me.getSceneY).getY

        // Get the value of the y-axis at the y-coordinate
        val price = yAxis.getValueForDisplay(yInAxisCoords).doubleValue()

        // Format the price as a string
        f"$price%.5f"
    }

    def calculateXCoordinate(date: Double): Double = {
        // Get the x-axis
        val xAxis = candleStickChart.xAxis.delegate

        // Get the display position for the date
        val x = xAxis.getDisplayPosition(date)

        x
    }

    def calculateXDate(me: MouseEvent): Double = {
        // Get the x-axis
        val xAxis = candleStickChart.xAxis.delegate

        // Get the x-coordinate of the mouse event in the local coordinate system of the x-axis
        val xInAxisCoords = xAxis.sceneToLocal(me.getSceneX, me.getSceneY).getX

        // Get the value of the x-axis at the x-coordinate
        val date = xAxis.getValueForDisplay(xInAxisCoords).intValue()

        date
    }

    def deleteAllCircles(): Unit = {
        for ((circle, _) <- circleEpochTimes) {
            // Remove the circle from the chart
            candleStickChart.plotChildren.remove(circle)
        }
        // Clear the circleEpochTimes map
        circleEpochTimes = Map()
    }
    def addcircle(symbol: String): Unit = {
        GetAPINewsSentiment.getNewsSentiment(symbol).as[JsArray].value.foreach { item =>
        val epochTime = (item \ "date").as[Long]
        val weightedSentiment = (item \ "weighted_sentiment").as[Double]

        // Create a new Circle object
        val circle = new Circle()

        // Set the center of the circle to the date and the top of the chart
        circle.centerX = calculateXCoordinate(epochTime)
        circle.centerY = 15

        // Set the radius of the circle
        circle.radius = 20 * weightedSentiment.abs

        // Set the color of the circle based on the sentiment value
        circle.fill = if (weightedSentiment > 0) Color.GREEN else Color.RED

        // Add the circle to the chart
        candleStickChart.addCustomNode(circle)
        circleEpochTimes = circleEpochTimes + (circle -> epochTime)
        }
    }
   

    def plotHorizontalLine(me: MouseEvent): Unit = {
        val point = this.sceneToLocal(new Point2D(me.sceneX, me.sceneY))
        startX = point.x
        startY = point.y

            // Get the y-coordinate from the mouse event
            val y = me.getY - 15

            // Get the width of the chart
            val chartWidth = candleStickChart.width.value

            // Create a new Line object
            val line = new Line() 
            line.startX = 0
            line.endX = chartWidth * 4
            line.startY = y
            line.endY = y


            // Calculate the price at the y-coordinate
            val price = calculateYPrice(me)

            // Store the line and its price
            linePrices = linePrices + (line -> price)

            horizontalLines = line :: horizontalLines
            candleStickChart.addCustomNode(line)


            // Set the stroke color and width of the line
            line.setStroke(Color.WHITE)
            line.setStrokeWidth(2)
            var dragDeltaX = 0.0
            var dragDeltaY = 0.0

            line.setOnMousePressed((event: MouseEvent) => {
                // Record a delta distance for the drag and drop operation.
                dragDeltaX = event.getSceneX
                dragDeltaY = event.getSceneY
                event.consume()
            })
            line.setOnContextMenuRequested((event: ContextMenuEvent) => {
                // Remove the line from the chart
                candleStickChart.plotChildren.remove(line)

                // Remove the line from the collections
                horizontalLines = horizontalLines.filterNot(_ == line)
                linePrices = linePrices - line
                lineStartXs = lineStartXs - line

                event.consume()
            })
            line.setOnMouseDragged((event: MouseEvent) => {
                val deltaY = event.getSceneY - dragDeltaY
                line.setStartY(line.getStartY + deltaY)
                line.setEndY(line.getEndY + deltaY)
                dragDeltaY = event.getSceneY
                val newPrice = calculateYPrice(event)
                linePrices = linePrices + (line -> newPrice)
                event.consume()
            })
        
    }
    
    def plotHorizontalStartLine(me: MouseEvent): Unit = {
        val point = this.sceneToLocal(new Point2D(me.sceneX, me.sceneY))
        startX = point.x
        startY = point.y

            // Get the y-coordinate from the mouse event
            val y = me.getY - 15

            // Get the width of the chart
            val chartWidth = candleStickChart.width.value

            // Create a new Line object
            val line = new Line() 
            line.startX = startX - 50 // Start the line at the x-coordinate of the mouse event
            line.endX = chartWidth * 4
            line.startY = y
            line.endY = y
            line.stroke = Color.WHITE

            // Calculate the price at the y-coordinate
            val price = calculateYPrice(me)
            val date = calculateXDate(me)
            // Store the line and its price
            linePrices = linePrices + (line -> price)
            lineStartXs = lineStartXs + (line -> date)
            horizontalLines = line :: horizontalLines
            candleStickChart.addCustomNode(line)


            // Set the stroke color and width of the line
            line.setStroke(Color.WHITE)
            line.setStrokeWidth(2)
            var dragDeltaX = 0.0
            var dragDeltaY = 0.0

            line.setOnMousePressed((event: MouseEvent) => {
                // Record a delta distance for the drag and drop operation.
                dragDeltaX = event.getSceneX
                dragDeltaY = event.getSceneY
                event.consume()
            })
            line.setOnContextMenuRequested((event: ContextMenuEvent) => {
                // Remove the line from the chart
                candleStickChart.plotChildren.remove(line)

                // Remove the line from the collections
                horizontalLines = horizontalLines.filterNot(_ == line)
                linePrices = linePrices - line
                lineStartXs = lineStartXs - line

                event.consume()
            })
            line.setOnMouseDragged((event: MouseEvent) => {
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
            
            })
        }
    

    
    def entryline(price: String): Unit = {

        // Get the y-coordinate from the mouse event
        val y = calculateYCoordinate(price)

        // Get the width of the chart
        val chartWidth = candleStickChart.width.value


        entryLine.startX = 0
        entryLine.endX = chartWidth * 4
        entryLine.startY = y
        entryLine.endY = y

        
        // Calculate the price at the y-coordinate

        // Store the line and its price
        linePrices = linePrices + (entryLine -> price)
        
        horizontalLines = entryLine :: horizontalLines
        candleStickChart.addCustomNode(entryLine)
        entryprice = price

        // Set the stroke color and width of the line
        
    }
    
    
         entryLine.setStroke(Color.YELLOW)
            entryLine.setStrokeWidth(3)
            var dragDeltaX = 0.0
            var dragDeltaY = 0.0

            entryLine.setOnMousePressed((event: MouseEvent) => {
                // Record a delta distance for the drag and drop operation.
                dragDeltaX = event.getSceneX
                dragDeltaY = event.getSceneY
                event.consume()
            })

            entryLine.setOnMouseDragged((event: MouseEvent) => {
                val deltaY = event.getSceneY - dragDeltaY
                entryLine.setStartY(entryLine.getStartY + deltaY)
                entryLine.setEndY(entryLine.getEndY + deltaY)
                dragDeltaY = event.getSceneY
                val newPrice = calculateYPrice(event)
                linePrices = linePrices + (entryLine -> newPrice)
                entryprice = newPrice
                event.consume()
            })


    def stopLossLine(price: String): Unit = {
        val y = calculateYCoordinate(price)
        val chartWidth = candleStickChart.width.value

        stopLossLine.startX = 0
        stopLossLine.endX = chartWidth * 4
        stopLossLine.startY = y
        stopLossLine.endY = y

        linePrices = linePrices + (stopLossLine -> price)

        horizontalLines = stopLossLine :: horizontalLines
        candleStickChart.addCustomNode(stopLossLine)
        stopLossPrice = price

        stopLossLine.setStroke(Color.RED)
        stopLossLine.setStrokeWidth(3)

        stopLossLine.setOnMousePressed((event: MouseEvent) => {
            dragDeltaX = event.getSceneX
            dragDeltaY = event.getSceneY
            event.consume()
        })

        stopLossLine.setOnMouseDragged((event: MouseEvent) => {
            val deltaY = event.getSceneY - dragDeltaY
            stopLossLine.setStartY(stopLossLine.getStartY + deltaY)
            stopLossLine.setEndY(stopLossLine.getEndY + deltaY)
            dragDeltaY = event.getSceneY
            val newPrice = calculateYPrice(event)
            linePrices = linePrices + (stopLossLine -> newPrice)
            stopLossPrice = newPrice
            event.consume()
        })
    }

    def takeProfitLine(price: String): Unit = {
        val y = calculateYCoordinate(price)
        val chartWidth = candleStickChart.width.value

        takeProfitLine.startX = 0
        takeProfitLine.endX = chartWidth * 4
        takeProfitLine.startY = y
        takeProfitLine.endY = y

        linePrices = linePrices + (takeProfitLine -> price)

        horizontalLines = takeProfitLine :: horizontalLines
        candleStickChart.addCustomNode(takeProfitLine)
        takeProfitPrice = price

        takeProfitLine.setStroke(Color.GREEN)
        takeProfitLine.setStrokeWidth(3)

        takeProfitLine.setOnMousePressed((event: MouseEvent) => {
            dragDeltaX = event.getSceneX
            dragDeltaY = event.getSceneY
            event.consume()
        })

        takeProfitLine.setOnMouseDragged((event: MouseEvent) => {
            val deltaY = event.getSceneY - dragDeltaY
            takeProfitLine.setStartY(takeProfitLine.getStartY + deltaY)
            takeProfitLine.setEndY(takeProfitLine.getEndY + deltaY)
            dragDeltaY = event.getSceneY
            val newPrice = calculateYPrice(event)
            linePrices = linePrices + (takeProfitLine -> newPrice)
            takeProfitPrice = newPrice
            event.consume()
        })
    }
    def updateOnMousePress(me: MouseEvent): Unit ={
        dragStartX = me.getX
        dragStartY = me.getY
    }

    def updateAllLines(): Unit = {
        for ((line, price) <- linePrices) {
            val y = calculateYCoordinate(price)
            line.startY = y
            line.endY = y
        }
        for ((line, startX) <- lineStartXs) {
            val x = calculateXCoordinate(startX)
            line.setStartX(x)
        }
       for ((circle, epochTime) <- circleEpochTimes) {
            val x = calculateXCoordinate(epochTime)
            circle.centerX = x
        }
    }

    def updateOnDrag(me: MouseEvent): Unit ={
        updateXYAxis(candleStickChart)
        val dragEndX = me.getX
        val dragEndY = me.getY

        // Calculate the difference between the start and end points
        val diffX = dragEndX - dragStartX
        val diffY = dragEndY - dragStartY

        if (dragEndX < 50) {
            val zoomFactor = 0.01 // Adjust this value to control the zoom speed
            val dragY = dragEndY - dragStartY
            val yAxis = candleStickChart.yAxis.delegate

            val getLowerBoundMethody = yAxis.getClass.getMethod("getLowerBound")
            val yAxisLowerBound = getLowerBoundMethody.invoke(yAxis).asInstanceOf[Double]

            val getUpperBoundMethody = yAxis.getClass.getMethod("getUpperBound")
            val yAxisUpperBound = getUpperBoundMethody.invoke(yAxis).asInstanceOf[Double]

            val range = yAxisUpperBound - yAxisLowerBound
            val zoomStep = range * zoomFactor

            if (dragY < 0) {

                // Dragging up, show lower numbers
               
                val newLowerBound = yAxisLowerBound + zoomStep
                val newUpperBound = yAxisUpperBound - zoomStep
                val setLowerBoundMethodY = yAxis.getClass.getMethod("setLowerBound", classOf[Double])
                setLowerBoundMethodY.invoke(yAxis, newLowerBound.asInstanceOf[AnyRef])

                val setUpperBoundMethodY = yAxis.getClass.getMethod("setUpperBound", classOf[Double])
                setUpperBoundMethodY.invoke(yAxis, newUpperBound.asInstanceOf[AnyRef])




            } else {

                // Dragging down, show higher numbers
              

                val newLowerBound = yAxisLowerBound - zoomStep
                val newUpperBound = yAxisUpperBound + zoomStep
                val setLowerBoundMethodY = yAxis.getClass.getMethod("setLowerBound", classOf[Double])
                setLowerBoundMethodY.invoke(yAxis, newLowerBound.asInstanceOf[AnyRef])

                val setUpperBoundMethodY = yAxis.getClass.getMethod("setUpperBound", classOf[Double])
                setUpperBoundMethodY.invoke(yAxis, newUpperBound.asInstanceOf[AnyRef])


               
          
            }
        } else {
            val yAxis = candleStickChart.yAxis.delegate
            val getLowerBoundMethody = yAxis.getClass.getMethod("getLowerBound")
            val yAxisLowerBound = getLowerBoundMethody.invoke(yAxis).asInstanceOf[Double]

            val getUpperBoundMethody = yAxis.getClass.getMethod("getUpperBound")
            val yAxisUpperBound = getUpperBoundMethody.invoke(yAxis).asInstanceOf[Double]
            // Calculate the pixel-to-y-axis-unit ratio
            val yAxisHeight = candleStickChart.yAxis.delegate.getHeight
            val yAxisRange = yAxisUpperBound - yAxisLowerBound
            val pixelToYAxisUnitRatio = yAxisRange / yAxisHeight

            // Calculate the y-difference in pixels
            val yDiffPixels = dragEndY - dragStartY

            // Convert the y-difference in pixels to the y-difference in y-axis units
            val yDiff = yDiffPixels * pixelToYAxisUnitRatio

            // Adjust the bounds for the Y-axis
        

            
            val xAxis = candleStickChart.xAxis.delegate
            val setLowerBoundMethodY = yAxis.getClass.getMethod("setLowerBound", classOf[Double])
            setLowerBoundMethodY.invoke(yAxis, (yAxisLowerBound + yDiff).asInstanceOf[AnyRef])

            val setUpperBoundMethodY = yAxis.getClass.getMethod("setUpperBound", classOf[Double])
            setUpperBoundMethodY.invoke(yAxis, (yAxisUpperBound + yDiff).asInstanceOf[AnyRef])

            val getLowerBoundMethodx = xAxis.getClass.getMethod("getLowerBound")
            val xAxisLowerBound = getLowerBoundMethodx.invoke(xAxis).asInstanceOf[Double]

            val getUpperBoundMethodx = xAxis.getClass.getMethod("getUpperBound")
            val xAxisUpperBound = getUpperBoundMethodx.invoke(xAxis).asInstanceOf[Double]
            // Calculate the pixel-to-x-axis-unit ratio
            val xAxisWidth = candleStickChart.xAxis.delegate.getWidth
            val xAxisRange = xAxisUpperBound - xAxisLowerBound
            val pixelToXAxisUnitRatio = xAxisRange / xAxisWidth

            // Calculate the x-difference in pixels
            val xDiffPixels = dragEndX - dragStartX

            // Convert the x-difference in pixels to the x-difference in x-axis units
            val xDiff = xDiffPixels * pixelToXAxisUnitRatio

            // Adjust the bounds for the X-axis


        

            val setLowerBoundMethodX = xAxis.getClass.getMethod("setLowerBound", classOf[Double])
            setLowerBoundMethodX.invoke(xAxis, (xAxisLowerBound - xDiff).asInstanceOf[AnyRef])

            val setUpperBoundMethodX = xAxis.getClass.getMethod("setUpperBound", classOf[Double])
            setUpperBoundMethodX.invoke(xAxis, (xAxisUpperBound - xDiff).asInstanceOf[AnyRef])


        }
        

        // Update the start points for the next drag event
        dragStartX = dragEndX
        dragStartY = dragEndY
        
    }

    def updateOnMouseRelease(): Unit ={
        dragStartX = 0
        dragStartY = 0
    }

    def updateOnScroll(event: ScrollEvent): Unit ={
    updateXYAxis(candleStickChart)
    val zoomFactor = 0.1 // Adjust this value to control the zoom speed

    val deltaY = event.deltaY
    val xAxis = candleStickChart.getXAxis

    val getLowerBoundMethod = xAxis.getClass.getMethod("getLowerBound")
    val xAxisLowerBound = getLowerBoundMethod.invoke(xAxis).asInstanceOf[Double]

    val getUpperBoundMethod = xAxis.getClass.getMethod("getUpperBound")
    val xAxisUpperBound = getUpperBoundMethod.invoke(xAxis).asInstanceOf[Double]

    val range = xAxisUpperBound - xAxisLowerBound
    val zoomStep = range * zoomFactor
    val xAxisWidth = candleStickChart.xAxis.delegate.getWidth
    val zoomhighlowlines = xAxisWidth * zoomFactor
    if (deltaY < 0) {
        // Zoom out
        val newLowerBound = xAxisLowerBound - zoomStep
        val setLowerBoundMethodx = xAxis.getClass.getMethod("setLowerBound", classOf[Double])
        setLowerBoundMethodx.invoke(xAxis, newLowerBound.asInstanceOf[AnyRef])
 

    } else {
        // Zoom in
        val newLowerBound = xAxisLowerBound + zoomStep
        val setLowerBoundMethodx = xAxis.getClass.getMethod("setLowerBound", classOf[Double])
        setLowerBoundMethodx.invoke(xAxis, newLowerBound.asInstanceOf[AnyRef])
  

    }

    }
}


object AdvCandleStickChartSample extends JFXApp3 {
    case class CandleStick(day: Double, open: Double, close: Double, high: Double, low: Double)
    override def start(): Unit = {

    }

    var distancecandles = 60
    import scala.collection.mutable.ListBuffer

    def updateCandleStickChartAxis(chart: CandleStickChart, candleData: ListBuffer[CandleStick]): Unit = {
        val xAxis = chart.getXAxis
        val yAxis = chart.getYAxis

        val minDatax = candleData.take(200).minBy(_.day).day
        val maxDatax = candleData.take(200).maxBy(_.day).day
        val minDatay = candleData.take(200).minBy(_.low).low
        val maxDatay = candleData.take(200).maxBy(_.high).high

        val setLowerBoundMethodX = xAxis.getClass.getMethod("setLowerBound", classOf[Double])
        val setUpperBoundMethodX = xAxis.getClass.getMethod("setUpperBound", classOf[Double])
        val setLowerBoundMethodY = yAxis.getClass.getMethod("setLowerBound", classOf[Double])
        val setUpperBoundMethodY = yAxis.getClass.getMethod("setUpperBound", classOf[Double])

        setLowerBoundMethodX.invoke(xAxis, minDatax.asInstanceOf[AnyRef])
        setUpperBoundMethodX.invoke(xAxis, maxDatax.asInstanceOf[AnyRef])
        setLowerBoundMethodY.invoke(yAxis, minDatay.asInstanceOf[AnyRef])
        setUpperBoundMethodY.invoke(yAxis, maxDatay.asInstanceOf[AnyRef])
        updateXYAxis(chart)
        
    }
    val chartDataMap: TreeMap[Double, XYChart.Data[Number, Number]] = TreeMap()
    def clearAndAddData(chart: CandleStickChart, newData: ListBuffer[CandleStick]): Unit = {
        // Clear the existing data from the chart
        chart.data.clear()
        chartDataMap.clear()
        // Convert the new data into the format required by the chart
        val seriesData = newData.map { d => 
            val data = XYChart.Data[Number, Number](d.day, d.open, d)
 
            XYChart.Series[Number, Number](ObservableBuffer(data))
        }

        // Add the new data to the chart
        chart.data = ObservableBuffer(seriesData.toSeq: _*)
        updateXYAxis(chart)
    }


 def addDataAndHideAfterDate(chart: CandleStickChart, data: ListBuffer[CandleStick], date: Double): Unit = {
    // Convert the CandleStick objects into the format required by the chart
    val seriesData = data.map(candleStick => {
        val data = XYChart.Data[Number, Number](candleStick.day, candleStick.open, candleStick)
        data
    })

    // Convert seriesData to a Set to remove duplicates
    val seriesDataSet = seriesData.toSet

    // Convert seriesDataSet to a Seq
    val seriesDataSeq = seriesDataSet.toSeq

    // Create a new series from the data
    val series = XYChart.Series[Number, Number](ObservableBuffer(seriesDataSeq: _*))

    // Add the new series to the chart
    chart.data += series
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    // Iterate over all series in the chart
    for (series <- chart.data) {
        // Iterate over the data in the current series
        for (data <- series.getData) {
                // Get the CandleStick object associated with this data
            val candleStick = data.getExtraValue.asInstanceOf[CandleStick]
     


            // If the date of the CandleStick is after the specified date, hide the data
            if (candleStick.day > date) {
                data.getNode.setVisible(false)
                // Add the data to the TreeMap
                chartDataMap += (candleStick.day -> data)
            }
        }
    }
}
    def showData(chart: CandleStickChart): Unit = {
        // Check if the map is not empty
        if (chartDataMap.nonEmpty) {
            // Get the first entry in the map
            val firstEntry = chartDataMap.head

            // Make the first invisible candle visible
            firstEntry._2.getNode.setVisible(true)

            // Remove the first entry from the map
            chartDataMap -= firstEntry._1
        }
    }

   def deleteFirstCandle(chart: CandleStickChart): Unit = {
        if (chart.data.nonEmpty) {
            // Get the first series in the chart
            val firstSeries = chart.data.head

            // Remove the first data point from the series
            if (firstSeries.getData.nonEmpty) {
                firstSeries.getData.remove(0) // remove the first element
            }

            // If the series is now empty, remove it from the chart
            if (firstSeries.getData.isEmpty) {
                chart.data.remove(0) // remove the first series
            }
        }
    }
    
    def updateXYAxis(chart: CandleStickChart): Unit = {
        val yAxis = chart.getYAxis.asInstanceOf[javafx.scene.chart.NumberAxis]
         val xAxis = chart.getXAxis.asInstanceOf[javafx.scene.chart.NumberAxis]

        updateAxis(yAxis)
        updateAxis(xAxis)
}

    def updateAxis(axis: javafx.scene.chart.NumberAxis): Unit = {
        val getLowerBoundMethod = axis.getClass.getMethod("getLowerBound")
        val axisLowerBound = getLowerBoundMethod.invoke(axis).asInstanceOf[Double]

        val getUpperBoundMethod = axis.getClass.getMethod("getUpperBound")
        val axisUpperBound = getUpperBoundMethod.invoke(axis).asInstanceOf[Double]
        val axisRange = axisUpperBound - axisLowerBound
        val tickUnit = 0.10 * axisRange

        axis.setTickUnit(tickUnit)
    }

 
    def createChart(candleData: ListBuffer[CandleStick]): CandleStickChart = {
        //Style Sheet loaded from external
        val cssURL = this.getClass.getResource("/de/htwg/se/TradingGame/view/GUI/AdvCandleStickChartSample.css")
        if (cssURL != null) {
            val css = cssURL.toExternalForm

            val minDatay = candleData.take(200).minBy(_.low).low
            val maxDatay = candleData.take(200).maxBy(_.high).high

            val firstCandle = candleData.last.day
            val minDatax = (candleData.take(200).minBy(_.day).day)
            val maxDatax = (candleData.take(200).maxBy(_.day).day)

            val xAxis = new NumberAxis(minDatax, maxDatax,1) {
            }
            val yAxis = new NumberAxis(minDatay, maxDatay, 1) {
            }

            val seriesData = candleData.map { d => 
            val data = XYChart.Data[Number, Number](d.day, d.open, d)
            XYChart.Series[Number, Number](ObservableBuffer(data))
            }

            val chart = new CandleStickChart(xAxis, yAxis) {
            data = ObservableBuffer(seriesData.toSeq: _*)
            getStylesheets += css
            }
            updateXYAxis(chart)
            chart 
        } else {
            println("Resource not found: AdvCandleStickChartSample.css")
            null
        }
        }



  class CandleStickChart(xa: NumberAxis, ya:NumberAxis, initialData: ObservableBuffer[jfxsc.XYChart.Series[Number, Number]] = ObservableBuffer.empty)
    extends jfxsc.XYChart[Number, Number](xa, ya) {
    setData(initialData)
    setAnimated(false)
    xAxis.animated = false
    yAxis.animated = false
    this.setLegendVisible(false)
    def addCustomNode(node: Node): Unit = {
        getPlotChildren.add(node)
        }

   
    def title: String = getTitle

    def title_=(t: String): Unit = {
        setTitle(t)
    }

    def data: ObservableBuffer[jfxsc.XYChart.Series[Number, Number]] = getData

    def data_=(d: ObservableBuffer[jfxsc.XYChart.Series[Number, Number]]): Unit = {
        setData(d)
    }

    def plotChildren = getPlotChildren
    def xAxis = getXAxis
    def yAxis = getYAxis

    /** Called to update and layout the content for the plot */
    override def layoutPlotChildren(): Unit = {
        if (data == null) {
        return
        }

        for (series <- data) {
        val seriesPath: Option[Path] = series.node() match {
            case path: jfxss.Path => Some(path)
            case _ => None
        }
        seriesPath.foreach(_.elements.clear())

        for (item <- getDisplayedDataIterator(series).asScala) {
            item.extraValue() match {
            case dayValues: CandleStick =>
                val x = xAxis.displayPosition(dayValues.day)

                item.node() match {
                case candle: Candle =>
                    val yOpen = yAxis.displayPosition(dayValues.open)
                    val yClose = yAxis.displayPosition(dayValues.close)
                    val yHigh = yAxis.displayPosition(dayValues.high)
                    val yLow = yAxis.displayPosition(dayValues.low)
                    val candleWidth = xAxis match {
                        case xa: jfxsc.NumberAxis => 
                            val pos1 = xa.displayPosition(1)
                            val pos2 = xa.displayPosition(distancecandles + 1)
                            (pos2 - pos1) * 0.8
                        case _ => -1
                    }
                    candle.update(yClose - yOpen, yHigh - yOpen, yLow - yOpen, candleWidth)
                    candle.updateTooltip(item.YValue().doubleValue, dayValues.close, dayValues.high, dayValues.low)
                    candle.layoutX = x
                    candle.layoutY = yOpen
                case _ =>
                }

                
            case _ =>
            }
        }
        }
    }

    override def dataItemChanged(item: jfxsc.XYChart.Data[Number, Number]): Unit = {}

    override def dataItemAdded(series: jfxsc.XYChart.Series[Number, Number],
                                            itemIndex: Int, item: jfxsc.XYChart.Data[Number, Number]): Unit = {
        val candle = Candle(getData.indexOf(series), item, itemIndex)
        if (shouldAnimate) {
        candle.opacity = 0
        plotChildren += candle
        new FadeTransition(500 ms, candle) {
            toValue = 1
        }.play()
        } else {
        plotChildren += candle
        }
        if (series.node() != null) {
        series.node().toFront()
        }
    }

    override def dataItemRemoved(item: jfxsc.XYChart.Data[Number, Number], series: jfxsc.XYChart.Series[Number, Number]): Unit = {
        val candle = item.node()
        if (shouldAnimate) {
        new FadeTransition(500 ms, candle) {
            toValue = 0
            onFinished = () => plotChildren -= candle

        }.play()
        }
        else {
        plotChildren -= candle
        }
    }

    override def seriesAdded(series: jfxsc.XYChart.Series[Number, Number], seriesIndex: Int): Unit = {
        for (j <- 0 until series.data().size) {
        val item = series.data()(j)
        val candle = Candle(seriesIndex, item, j)
        if (shouldAnimate) {
            candle.opacity = 0
            plotChildren += candle
            val ft = new FadeTransition(500 ms, candle) {
            toValue = 1
            }
            ft.play()
        }
        else {
            plotChildren += candle
        }
        }
        val seriesPath = new Path {
        styleClass = Seq("candlestick-average-line", "series" + seriesIndex)
        }
        series.node = seriesPath
        plotChildren += seriesPath
    }

    override def seriesRemoved(series: jfxsc.XYChart.Series[Number, Number]): Unit = {
        for (d <- series.getData) {
        val candle = d.node()
        if (shouldAnimate) {
            new FadeTransition(500 ms, candle) {
            toValue = 0
            onFinished = (_: ActionEvent) => plotChildren -= candle
            }.play()
        }
        else {
            plotChildren -= candle
        }
        }
    }


    /**
         * This is called when the range has been invalidated and we need to update it. If the axis are auto
         * ranging then we compile a list of all data that the given axis has to plot and call invalidateRange() on the
         * axis passing it that data.
         */
    override def updateAxisRange(): Unit = {

        if (xAxis.isAutoRanging) {
        val xData = for (series <- data; seriesData <- series.data()) yield seriesData.XValue()
        xAxis.invalidateRange(xData)
        }

        if (yAxis.isAutoRanging) {
        val yData = mutable.ListBuffer.empty[Number]
        for (series <- data; seriesData <- series.data()) {
            seriesData.extraValue() match {
            case extras: CandleStick =>
                yData += extras.high
                yData += extras.low
            case _ =>
                yData += seriesData.YValue()
            }
        }

        yAxis.invalidateRange(yData)
        }
    }
    }

    private object Candle {
    /**
         * Create a new Candle node to represent a single data item
         *
         * @param seriesIndex The index of the series the data item is in
         * @param item        The data item to create node for
         * @param itemIndex   The index of the data item in the series
         * @return New candle node to represent the give data item
         */
    def apply(seriesIndex: Int, item: XYChart.Data[_, _], itemIndex: Int): Node = {
        var candle = item.node()
        candle match {
        case c: Candle =>
            c.setSeriesAndDataStyleClasses("series" + seriesIndex, "data" + itemIndex)
        case _ =>
            candle = new Candle("series" + seriesIndex, "data" + itemIndex)
            item.node = candle
        }
        candle
    }

    }

    /** Candle node used for drawing a candle */
    private class Candle(private var seriesStyleClass: String,
                        private var dataStyleClass: String) extends jfxs.Group {

    private val highLowLine: Line = new Line
    private val bar: Region = new Region
    private var openAboveClose: Boolean = true
    private val tooltip: Tooltip = new Tooltip

    private var _styleClass: Seq[String] = Seq()

    def styleClass: Seq[String] = _styleClass
    def styleClass_=(s: Seq[String]): Unit = {
    _styleClass = s
    getStyleClass.setAll(s: _*)
    }
    setAutoSizeChildren(false)
    getChildren.addAll(highLowLine, bar)
    updateStyleClasses()
    tooltip.graphic = new TooltipContent()
    Tooltip.install(bar, tooltip)

    def setSeriesAndDataStyleClasses(seriesStyleClass: String, dataStyleClass: String): Unit = {
        this.seriesStyleClass = seriesStyleClass
        this.dataStyleClass = dataStyleClass
        updateStyleClasses()
    }

    def update(closeOffset: Double, highOffset: Double, lowOffset: Double, candleWidth: Double): Unit = {
        openAboveClose = closeOffset > 0
        updateStyleClasses()
        highLowLine.startY = highOffset
        highLowLine.endY = lowOffset
        val cw = if (candleWidth == -1) {
        // FIXME: It should be possible to access this method without delegate, it is not the same as setPrefWidth
        bar.delegate.prefWidth(-1)
        } else
        candleWidth
        if (openAboveClose) {
        bar.resizeRelocate(-cw / 2, 0, cw, closeOffset)
        }
        else {
        bar.resizeRelocate(-cw / 2, closeOffset, cw, closeOffset * -1)
        }
    }

    def updateTooltip(open: Double, close: Double, high: Double, low: Double): Unit = {
        val tooltipContent: TooltipContent = tooltip.graphic().asInstanceOf[TooltipContent]
        tooltipContent.update(open, close, high, low)
    }

    private def updateStyleClasses(): Unit = {
        val closeVsOpen = if (openAboveClose) "open-above-close" else "close-above-open"

        styleClass = Seq("candlestick-candle", seriesStyleClass, dataStyleClass)
        highLowLine.styleClass = Seq("candlestick-line", seriesStyleClass, dataStyleClass, closeVsOpen)
        bar.styleClass = Seq("candlestick-bar", seriesStyleClass, dataStyleClass, closeVsOpen)
        val barColor = if (openAboveClose) "#0079db" else "#ffffff" // replace with the colors you want
        highLowLine.setStyle(s"-fx-stroke: $barColor;")
    }

    }

    private class TooltipContent extends jfxsl.GridPane {
    private val openValue = new Label()
    private val closeValue = new Label()
    private val highValue = new Label()
    private val lowValue = new Label()

    val open = new Label("OPEN:") {
        styleClass += "candlestick-tooltip-label"
    }
    val close = new Label("CLOSE:") {
        styleClass += "candlestick-tooltip-label"
    }
    val high = new Label("HIGH:") {
        styleClass += "candlestick-tooltip-label"
    }
    val low = new Label("LOW:") {
        styleClass += "candlestick-tooltip-label"
    }

    GridPane.setConstraints(open, 0, 0)
    GridPane.setConstraints(openValue, 1, 0)
    GridPane.setConstraints(close, 0, 1)
    GridPane.setConstraints(closeValue, 1, 1)
    GridPane.setConstraints(high, 0, 2)
    GridPane.setConstraints(highValue, 1, 2)
    GridPane.setConstraints(low, 0, 3)
    GridPane.setConstraints(lowValue, 1, 3)
    getChildren.addAll(open, openValue, close, closeValue, high, highValue, low, lowValue)

    def update(open: Double, close: Double, high: Double, low: Double): Unit = {
        openValue.text = open.toString
        closeValue.text = close.toString
        highValue.text = high.toString
        lowValue.text = low.toString
    }
    }

}


