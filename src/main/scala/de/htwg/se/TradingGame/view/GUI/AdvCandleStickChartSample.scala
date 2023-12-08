/*
* Copyright 2013 ScalaFX Project
* All right reserved.
*/
package de.htwg.se.TradingGame.view.GUI

import javafx.scene.{chart => jfxsc}
import javafx.scene.{layout => jfxsl}
import javafx.scene.{shape => jfxss}
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.animation.FadeTransition
import scalafx.application.JFXApp
import scalafx.application.JFXApp3
import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.Node
import scalafx.scene.Scene
import scalafx.scene.chart.Axis
import scalafx.scene.chart.NumberAxis
import scalafx.scene.chart.ValueAxis
import scalafx.scene.chart.XYChart
import scalafx.scene.control.Label
import scalafx.scene.control.Tooltip
import scalafx.scene.input.MouseEvent
import scalafx.scene.input.ScrollEvent
import scalafx.scene.layout.GridPane
import scalafx.scene.layout.Pane
import scalafx.scene.layout.Region
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Line
import scalafx.scene.shape.LineTo
import scalafx.scene.shape.MoveTo
import scalafx.scene.shape.Path

import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.language.postfixOps

class DraggableCandleStickChart(candleStickChart: AdvCandleStickChartSample.CandleStickChart) extends StackPane {
    private var currentSelectedTimeframe: String = "1m"
    private var selectedTicker: String = "EURUSD"
    private val chartDataLoader: ChartDataLoader = new ChartDataLoader()
    var lastPrice = chartDataLoader.lastPrice
    var selectedDate: java.time.LocalDateTime = java.time.LocalDateTime.now()
    var lines: mutable.ListBuffer[Line] = mutable.ListBuffer()
    children.add(candleStickChart)
        
    var dragStartX: Double = 0
    var dragStartY: Double = 0

    def calculateYPrice(me: MouseEvent): String = {
        // Get the height of the chart pane
        val chartHeight = candleStickChart.height.value

        // Calculate the ratio of the y position to the chart height
        val ratio = me.getY / chartHeight

        val yAxis = candleStickChart.yAxis.delegate

        val getLowerBoundMethody = yAxis.getClass.getMethod("getLowerBound")
        val yAxisLowerBound = getLowerBoundMethody.invoke(yAxis).asInstanceOf[Double]

        val getUpperBoundMethody = yAxis.getClass.getMethod("getUpperBound")
        val yAxisUpperBound = getUpperBoundMethody.invoke(yAxis).asInstanceOf[Double]
        // Get the range of your prices
        val priceRange = yAxisUpperBound - yAxisLowerBound

        // Calculate the price corresponding to the y position
        val price = (1 - ratio) * priceRange + yAxisLowerBound

        // Format the price as a string
        f"$price%.5f"
    }


    def plotHorizontalLine(me: MouseEvent): Unit = {
        // Get the y-coordinate from the mouse event
        val y = me.getY - 15

        // Get the width of the chart
        val chartWidth = candleStickChart.width.value

        // Create a new Line object
        val line = new Line(){
            startX = 0
            endX = chartWidth * 4
            startY = y
            endY = y
        }
        lines += line
        candleStickChart.addCustomNode(line)

        // Set the stroke color and width of the line
        line.setStroke(Color.BLACK)
        line.setStrokeWidth(2)
    }


    


    def updateOnMousePress(me: MouseEvent): Unit ={
        dragStartX = me.getX
        dragStartY = me.getY
    }

    def updateAllLines(pointY: Double, startY: Double): Unit = {
        for (line <- lines) {
            line.setStartY(pointY + (line.getStartY - startY))
            line.setEndY(pointY + (line.getEndY - startY))
        }
    }
    def updateOnDrag(me: MouseEvent): Unit ={
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
            updateAllLines(dragEndY, dragStartY)
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
        AdvCandleStickChartSample.updateCandleStickChartAxis(candleStickChart, newLowerBound)
    } else {
        // Zoom in
        val newLowerBound = xAxisLowerBound + zoomStep
        AdvCandleStickChartSample.updateCandleStickChartAxis(candleStickChart, newLowerBound)
    }
    }
}


object AdvCandleStickChartSample extends JFXApp3 {
 case class CandleStick(day: Double, open: Double, close: Double, high: Double, low: Double)
  
    override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Adv Candle Stick Chart Example"
      scene = new Scene {
        root = {
          val data = AllTickerArrays.candleSticks
          val chart = createChart(data)
          val draggableChart = new DraggableCandleStickChart(chart)
          draggableChart
        }
      }
    }
  }

 
 import scala.collection.mutable.ListBuffer

    def updateCandleStickChartAxis(chart: CandleStickChart, xLower: Double): Unit = {

        val xAxis = chart.getXAxis
        val setLowerBoundMethodx = xAxis.getClass.getMethod("setLowerBound", classOf[Double])
        setLowerBoundMethodx.invoke(xAxis, xLower.asInstanceOf[AnyRef])

    }


    def createChart(candleData: ListBuffer[CandleStick]): CandleStickChart = {
    //Style Sheet loaded from external
    val cssURL = this.getClass.getResource("/de/htwg/se/TradingGame/view/GUI/AdvCandleStickChartSample.css")
    if (cssURL != null) {
        val css = cssURL.toExternalForm

        val minDatay = candleData.minBy(_.low).low
        val maxDatay = candleData.maxBy(_.high).high

        val firstCandle = candleData.head.day
        val minDatax = (candleData.minBy(_.day).day)
        val maxDatax = (candleData.maxBy(_.day).day)

        val xAxis = new NumberAxis(minDatax, maxDatax,2) {
        }

        val yAxis = new NumberAxis(minDatay, maxDatay, 0.0001) {
        }

        val seriesData = candleData.map { d => 
        val data = XYChart.Data[Number, Number](d.day, d.open, d)
        XYChart.Series[Number, Number](ObservableBuffer(data))
        }

        new CandleStickChart(xAxis, yAxis) {
        data = ObservableBuffer(seriesData.toSeq: _*)
        getStylesheets += css
        }
    } else {
        println("Resource not found: AdvCandleStickChartSample.css")
        null
    }
    }



    class CandleStickChart(xa: Axis[Number], ya: Axis[Number], initialData: ObservableBuffer[jfxsc.XYChart.Series[Number, Number]] = ObservableBuffer.empty)
    extends jfxsc.XYChart[Number, Number](xa, ya) {
    setData(initialData)
    setAnimated(false)
    xAxis.animated = false
    yAxis.animated = false

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
                            val pos2 = xa.displayPosition(2)
                            (pos2 - pos1) * 0.90
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


