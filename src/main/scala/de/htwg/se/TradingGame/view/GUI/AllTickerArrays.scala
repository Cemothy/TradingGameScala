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
    val lastLine = Source.fromFile(file).getLines().toList.last
    val lastValues = lastLine.split(",")
    var endDate: LocalDateTime = LocalDateTime.parse(s"${lastValues(0)},${lastValues(1)}", formatter)
    var currentPrice: Double = 0.0
    
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
    def getNextCandle(): CandleStick = {
      // Increment the endDate by the timeFrameMinutes
      endDate = endDate.plusMinutes(timeFrameMinutes)

      // Recalculate the candleSticks
      candleSticks = calculateCandleSticks()

      // Return the last candlestick
      candleSticks.last
  }
    def incrementDataByNCandles(n: Int): Unit = {
      // Increment the endDate by n times the timeFrameMinutes
      endDate = endDate.plusMinutes(timeFrameMinutes * n)

      // If the endDate is a Saturday or Sunday, increment it by the necessary number of days
      endDate.getDayOfWeek match {
          case java.time.DayOfWeek.SATURDAY => endDate = endDate.plusDays(2) // Skip to Monday
          case java.time.DayOfWeek.SUNDAY => endDate = endDate.plusDays(1) // Skip to Monday
          case _ => // Do nothing
      }

      // Recalculate the candleSticks
      candleSticks = calculateCandleSticks()
  }

   
    def decrementDataByNCandles(n: Int): Unit = {
      // Decrement the endDate by n times the timeFrameMinutes
      endDate = endDate.minusMinutes(timeFrameMinutes * n)

      // If the endDate is a Saturday or Sunday, decrement it by the necessary number of days
      endDate.getDayOfWeek match {
          case java.time.DayOfWeek.MONDAY => endDate = endDate.minusDays(2) // Skip to Friday
          case java.time.DayOfWeek.SUNDAY => endDate = endDate.minusDays(1) // Skip to Saturday
          case _ => // Do nothing
      }

    
      // Recalculate the candleSticks
      candleSticks = calculateCandleSticks()
  }
    def setTicker(newTicker: String): Unit = {
        ticker = newTicker
        file = new File(Path).getParent + s"/Symbols/$ticker.csv"
        candleSticks = calculateCandleSticks()
    }
     def setTimeFrame(newTimeFrame: String): Unit = {
        timeFrame = newTimeFrame
        timeFrameMinutes = parseTimeFrame(timeFrame)
        AdvCandleStickChartSample.distancecandles = timeFrameMinutes * 60
        candleSticks = calculateCandleSticks()  
    }
    var timeFrameMinutes = parseTimeFrame(timeFrame)
    var candleSticks: ListBuffer[CandleStick] = ListBuffer()
    def setDate(date: String): Unit = {
        endDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm"))
        candleSticks = calculateCandleSticks()
    }
    def calculateCandleSticks(): ListBuffer[CandleStick] = {
    val lines = Source.fromFile(file).getLines().toList
    val endDateEpochSec = endDate.atZone(ZoneId.systemDefault()).toEpochSecond
    val startRangeEpochSec = endDateEpochSec - 500 * timeFrameMinutes * 60

    val rawCandles = ListBuffer(lines.map { line =>
        val values = line.split(",")
        val candleDateEpochSec = LocalDateTime.parse(s"${values(0)},${values(1)}", formatter).atZone(ZoneId.systemDefault()).toEpochSecond
        if (candleDateEpochSec >= startRangeEpochSec && candleDateEpochSec <= endDateEpochSec) {
            CandleStick(
                day = candleDateEpochSec,
                open = values(2).toDouble,
                close = values(5).toDouble,
                high = values(3).toDouble,
                low = values(4).toDouble,
            )
        } else null
    }.filter(_ != null): _*)
    
    val groupedCandles = rawCandles.grouped(timeFrameMinutes).toList

    val newCandleSticks = ListBuffer(groupedCandles.map { group =>
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
    currentPrice = newCandleSticks.last.close
    newCandleSticks
}

    def getCandleSticks: ListBuffer[CandleStick] = candleSticks
    def createchart(newTicker: String, newTimeFrame: String): CandleStickChart = {
    setTicker(newTicker)
    setTimeFrame(newTimeFrame)
    createChart(candleSticks)
}
}