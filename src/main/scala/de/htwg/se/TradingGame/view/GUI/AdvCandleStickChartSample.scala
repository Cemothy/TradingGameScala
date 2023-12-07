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
import scalafx.scene.chart.XYChart
import scalafx.scene.control.Label
import scalafx.scene.control.Tooltip
import scalafx.scene.layout.GridPane
import scalafx.scene.layout.Region
import scalafx.scene.shape.Line
import scalafx.scene.shape.LineTo
import scalafx.scene.shape.MoveTo
import scalafx.scene.shape.Path

import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.language.postfixOps




object AdvCandleStickChartSample extends JFXApp3 {
 case class CandleStick(day: Double, open: Double, close: Double, high: Double, low: Double)
  
    override def start(): Unit = {
        stage = new JFXApp3.PrimaryStage {
            title = "Adv Candle Stick Chart Example"
            scene = new Scene {
            root = {
            
                val data = AllTickerArrays.candleSticks
            
                createChart(data)
            
            }
            }
        }
    }
 
 import scala.collection.mutable.ListBuffer

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
    override protected def layoutPlotChildren(): Unit = {
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
                    case xa: jfxsc.NumberAxis => xa.displayPosition(xa.tickUnit()) * 0.90
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

    override protected def dataItemChanged(item: jfxsc.XYChart.Data[Number, Number]): Unit = {}

    override protected def dataItemAdded(series: jfxsc.XYChart.Series[Number, Number],
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

    override protected def dataItemRemoved(item: jfxsc.XYChart.Data[Number, Number], series: jfxsc.XYChart.Series[Number, Number]): Unit = {
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

    override protected def seriesAdded(series: jfxsc.XYChart.Series[Number, Number], seriesIndex: Int): Unit = {
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

    override protected def seriesRemoved(series: jfxsc.XYChart.Series[Number, Number]): Unit = {
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
    override protected def updateAxisRange(): Unit = {

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


