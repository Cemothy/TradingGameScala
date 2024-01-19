package de.htwg.se.TradingGame.view.GUI.Stages.BacktestStageFolder


import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import javafx.collections.FXCollections
import javafx.scene.{chart => jfxsc}
import javafx.scene.{layout => jfxsl}
import javafx.scene.{shape => jfxss}
import javafx.{scene => jfxs}
import org.checkerframework.checker.units.qual.g
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
import scala.collection.parallel.CollectionConverters._
import scala.compiletime.ops.boolean
import scala.jdk.CollectionConverters._
import scala.language.postfixOps

object AdvCandleStickChartSample extends JFXApp3{

override def start(): Unit ={

}

class CandleStickChart(gameStateManager: IGameStateManager, xa: NumberAxis, ya:NumberAxis, initialData: ObservableBuffer[jfxsc.XYChart.Series[Number, Number]] = ObservableBuffer.empty ) extends jfxsc.XYChart[Number, Number](xa, ya) {
    setData(initialData)
    setAnimated(false)
    xAxis.animated = false
    yAxis.animated = false
    this.setLegendVisible(false)
    def title: String = getTitle
    def title_=(t: String): Unit = setTitle(t)
    def data: ObservableBuffer[jfxsc.XYChart.Series[Number, Number]] = getData
    def data_=(d: ObservableBuffer[jfxsc.XYChart.Series[Number, Number]]): Unit = setData(d)
    def plotChildren = getPlotChildren
    def xAxis = getXAxis
    def yAxis = getYAxis
    def addCustomNode(node: Node): Unit = getPlotChildren.add(node)
        

    override def layoutPlotChildren(): Unit = {
        if (data == null) 
            return

        val getxLowerBoundMethod = xAxis.getClass.getMethod("getLowerBound")
        val xAxisLowerBound = getxLowerBoundMethod.invoke(xAxis).asInstanceOf[Double]

        val getxUpperBoundMethod = xAxis.getClass.getMethod("getUpperBound")
        val xAxisUpperBound = getxUpperBoundMethod.invoke(xAxis).asInstanceOf[Double]

        val updates = data.par.flatMap { series =>
            val seriesPath: Option[Path] = series.node() match 
                case path: jfxss.Path => Some(path)
                case _ => None
            
            seriesPath.foreach(_.elements.clear())
            getDisplayedDataIterator(series).asScala.flatMap { item =>
                item.extraValue() match 
                    case dayValues: CandleStick =>
                        val x = xAxis.displayPosition(dayValues.day)
                        item.node() match 
                            case candle: Candle =>
                                val yOpen = yAxis.displayPosition(dayValues.open)
                                val yClose = yAxis.displayPosition(dayValues.close)
                                val yHigh = yAxis.displayPosition(dayValues.high)
                                val yLow = yAxis.displayPosition(dayValues.low)
                                val candleWidth = xAxis match 
                                    case xa: jfxsc.NumberAxis =>
                                        val pos1 = xa.displayPosition(1)
                                        val pos2 = xa.displayPosition(gameStateManager.currentState.distancecandles + 1)
                                        (pos2 - pos1) * 0.8
                                    case _ => -1
                                Some((candle, yClose - yOpen, yHigh - yOpen, yLow - yOpen, candleWidth, x, yOpen))
                            case _ => None
                    case _ => None
            }
        }.seq

        Platform.runLater(() => {
            updates.foreach { case (candle, yCloseOpen, yHighOpen, yLowOpen, candleWidth, x, yOpen) =>
                if(x >= xAxis.displayPosition(gameStateManager.currentState.backtestDate)) {
                    candle.setVisible(false)
                } else if(x >= xAxis.displayPosition(xAxisUpperBound) || x <= xAxis.displayPosition(xAxisLowerBound)) {
                    candle.setVisible(false)
                }
                else {
                    candle.setVisible(true)
                }
                if(candle.isVisible()) {
                candle.update(yCloseOpen, yHighOpen, yLowOpen, candleWidth)
                candle.layoutX = x
                candle.layoutY = yOpen
            }

            }
        })
            }

    override def dataItemChanged(item: jfxsc.XYChart.Data[Number, Number]): Unit = {}

    override def dataItemAdded(series: jfxsc.XYChart.Series[Number, Number], itemIndex: Int, item: jfxsc.XYChart.Data[Number, Number]): Unit = 
        val candle = Candle(getData.indexOf(series), item, itemIndex)
        if (shouldAnimate) 
            candle.opacity = 0
            plotChildren += candle
            new FadeTransition(500 ms, candle) {
                toValue = 1
            }.play()
        else
            plotChildren += candle
        if (series.node() != null) 
            series.node().toFront()
        
    override def dataItemRemoved(item: jfxsc.XYChart.Data[Number, Number], series: jfxsc.XYChart.Series[Number, Number]): Unit = 
        val candle = item.node()
        if (shouldAnimate) 
            new FadeTransition(500 ms, candle) {
                toValue = 0
                onFinished = () => plotChildren -= candle
            }.play()
        else 
        plotChildren -= candle

    override def seriesAdded(series: jfxsc.XYChart.Series[Number, Number], seriesIndex: Int): Unit = 
        for (j <- 0 until series.data().size) 
            val item = series.data()(j)
            val candle = Candle(seriesIndex, item, j)
            if (shouldAnimate) 
                candle.opacity = 0
                plotChildren += candle
                val ft = new FadeTransition(500 ms, candle) {
                    toValue = 1
                }.play()
            else 
                plotChildren += candle
        val seriesPath = new Path {
            styleClass = Seq("candlestick-average-line", "series" + seriesIndex)
        }
        series.node = seriesPath
        plotChildren += seriesPath
    
    override def seriesRemoved(series: jfxsc.XYChart.Series[Number, Number]): Unit =
        for (d <- series.getData) 
            val candle = d.node()
            if (shouldAnimate) 
                new FadeTransition(500 ms, candle) {
                    toValue = 0
                    onFinished = (_: ActionEvent) => plotChildren -= candle
                }.play()     
            else 
                plotChildren -= candle

    override def updateAxisRange(): Unit = 
        if (xAxis.isAutoRanging) 
            val xData = for (series <- data; seriesData <- series.data()) yield seriesData.XValue()
            xAxis.invalidateRange(xData)
        if (yAxis.isAutoRanging)
            val yData = mutable.ListBuffer.empty[Number]
            for (series <- data; seriesData <- series.data()) 
                seriesData.extraValue() match
                    case extras: CandleStick =>
                        yData += extras.high
                        yData += extras.low
                    case _ =>
                        yData += seriesData.YValue()
            yAxis.invalidateRange(yData)
    }

    private object Candle {
        def apply(seriesIndex: Int, item: XYChart.Data[_, _], itemIndex: Int): Node = 
            var candle = item.node()
            candle match 
                case c: Candle =>
                    c.setSeriesAndDataStyleClasses("series" + seriesIndex, "data" + itemIndex)
                case _ =>
                    candle = new Candle("series" + seriesIndex, "data" + itemIndex)
                    item.node = candle
            candle
    }

    class Candle(private var seriesStyleClass: String, private var dataStyleClass: String) extends jfxs.Group {

    private val highLowLine: Line = new Line
    private val bar: Region = new Region
    private var openAboveClose: Boolean = true
    private var _styleClass: Seq[String] = Seq()
    private var lastOpenAboveClose: Boolean = openAboveClose

    def styleClass: Seq[String] = _styleClass
    def styleClass_=(s: Seq[String]): Unit = 
        _styleClass = s                                                                                                                                                                                                                                 
        getStyleClass.setAll(s: _*)
                                                                                                                                                                                   
    setAutoSizeChildren(false)
    getChildren.addAll(highLowLine, bar)

    def setSeriesAndDataStyleClasses(seriesStyleClass: String, dataStyleClass: String): Unit = 
        this.seriesStyleClass = seriesStyleClass
        this.dataStyleClass = dataStyleClass
        updateStyleClasses()

    def update(closeOffset: Double, highOffset: Double, lowOffset: Double, candleWidth: Double): Unit =
        openAboveClose = closeOffset > 0
        if (openAboveClose != lastOpenAboveClose) 
        updateStyleClasses()
        lastOpenAboveClose = openAboveClose
        highLowLine.startY = highOffset
        highLowLine.endY = lowOffset
        val cw = if (candleWidth == -1) 
        bar.delegate.prefWidth(-1)
        else
        candleWidth
        if (openAboveClose)
        bar.resizeRelocate(-cw / 2, 0, cw, closeOffset)
        else
        bar.resizeRelocate(-cw / 2, closeOffset, cw, closeOffset * -1)

    private val styleClasses = Seq("candlestick-candle", seriesStyleClass, dataStyleClass)
    private val lineStyleClasses = Seq("candlestick-line", seriesStyleClass, dataStyleClass)
    private val barStyleClasses = Seq("candlestick-bar", seriesStyleClass, dataStyleClass)

    private def updateStyleClasses(): Unit = 
        val closeVsOpen = if (openAboveClose) "open-above-close" else "close-above-open"
        styleClass = styleClasses
        highLowLine.styleClass = lineStyleClasses :+ closeVsOpen
        bar.styleClass = barStyleClasses :+ closeVsOpen
        val barColor = if (openAboveClose) "#0079db" else "#ffffff" 
        highLowLine.setStyle(s"-fx-stroke: $barColor;")
  
      updateStyleClasses()
}
}

