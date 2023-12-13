package de.htwg.se.TradingGame.view.GUI
import de.htwg.se.TradingGame.view.GUI.AdvCandleStickChartSample._

import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ListBuffer
import scala.io.Source

case class AllTickerArrays(var ticker: String, var timeFrame: String) {
    val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
    var file = new File(Path).getParent + s"/Symbols/$ticker.csv"
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
    val firstline = Source.fromFile(file).getLines().take(1).toList.head
    val firstValues = firstline.split(",")
    val firstCandleEpochSec = LocalDateTime.parse(s"${firstValues(0)},${firstValues(1)}", formatter).atZone(ZoneId.systemDefault()).toEpochSecond()

    def parseTimeFrame(timeFrame: String): Int = {
        val unit = timeFrame.takeRight(1)
        val value = timeFrame.dropRight(1).toInt

        unit match {
            case "m" => value
            case "h" => value * 60
            case "d" => value * 60 * 24
            case _ => throw new IllegalArgumentException("Invalid timeframe")
        }
    }
    
    def setTicker(newTicker: String): Unit = {
        ticker = newTicker
        file = new File(Path).getParent + s"/Symbols/$ticker.csv"
        candleSticks = calculateCandleSticks()
    }
     def setTimeFrame(newTimeFrame: String): Unit = {
        timeFrame = newTimeFrame
        timeFrameMinutes = parseTimeFrame(timeFrame)
        AdvCandleStickChartSample.distancecandles = timeFrameMinutes
        candleSticks = calculateCandleSticks()
    }
    var timeFrameMinutes = parseTimeFrame(timeFrame)
    var candleSticks: ListBuffer[CandleStick] = ListBuffer()
    def calculateCandleSticks(): ListBuffer[CandleStick] = {
    val lines = Source.fromFile(file).getLines().toList
    val rawCandles = ListBuffer(lines.tail.takeRight(500*timeFrameMinutes).map { line =>
        val values = line.split(",")
        CandleStick(
            day = (LocalDateTime.parse(s"${values(0)},${values(1)}", formatter).atZone(ZoneId.systemDefault()).toEpochSecond() - firstCandleEpochSec) /60,
            open = values(2).toDouble,
            close = values(5).toDouble,
            high = values(3).toDouble,
            low = values(4).toDouble,
        )
    }: _*)

    val groupedCandles = rawCandles.grouped(timeFrameMinutes).toList

    ListBuffer(groupedCandles.map { group =>
        val open = group.head.open
        val close = group.last.close
        val high = group.map(_.high).max
        val low = group.map(_.low).min

        CandleStick(
            day = group.head.day,
            open = open,
            close = close,
            high = high,
            low = low,
        )
    }: _*)
}

    def getCandleSticks: ListBuffer[CandleStick] = candleSticks
    def createchart(newTicker: String, newTimeFrame: String): CandleStickChart = {
    setTicker(newTicker)
    setTimeFrame(newTimeFrame)
    createChart(candleSticks)
}
}