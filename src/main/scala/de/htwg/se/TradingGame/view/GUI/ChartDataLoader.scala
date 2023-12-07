package de.htwg.se.TradingGame.view.GUI

import javafx.scene.chart.XYChart
import scalafx.scene.chart.LineChart
import scalafx.scene.chart.NumberAxis

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import de.htwg.se.TradingGame.view.GUI.AdvCandleStickChartSample.CandleStickChart
import java.time.ZoneId
import de.htwg.se.TradingGame.view.GUI.AdvCandleStickChartSample.CandleStick


class ChartDataLoader {
  var lastPrice = 0.0
  def loadDataAndUpdateChart(ticker: String, datapoints: Int, timeframe: String, selectedDate: LocalDateTime): Array[CandleStick] = {
    
    var candleSticks: Array[CandleStick] = Array()
    val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
    val file = new java.io.File(Path).getParent + s"/Symbols/${ticker}.csv"
    val fileObj = new File(file)
    if (fileObj.exists()) {
      val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
      val firstline = io.Source.fromFile(file).getLines().take(1).toList.head
      val firstValues = firstline.split(",")
      val firstCandleEpochSec = LocalDateTime.parse(s"${firstValues(0)},${firstValues(1)}", formatter).atZone(ZoneId.systemDefault()).toEpochSecond()
      val allData = io.Source.fromFile(file).getLines().toList.drop(1).map(_.split(",")).map(arr => ((LocalDateTime.parse(arr(0)+ "," +  arr(1), formatter).atZone(ZoneId.systemDefault()).toEpochSecond() - firstCandleEpochSec) /60, arr(2).toDouble, arr(5).toDouble, arr(3).toDouble, arr(4).toDouble))
      
      
      def groupData(data: List[(Long, Double, Double, Double, Double)], size: Int): List[(Long, Double, Double, Double, Double)] = {
        val groupedData = data.grouped(size).toList
        groupedData.map { group =>
          val open = group.head._2
          val close = group.last._3
          val high = group.map(_._4).max
          val low = group.map(_._5).min
          (group.head._1, open, close, high, low)
        }
      }
      val data = timeframe match {
        case "1m" => allData
        case "5m" => groupData(allData, 5)
        case "15m" => groupData(allData, 15)
        case "1h" => groupData(allData, 60)
        case "4h" => groupData(allData, 240)
        case "1day" => groupData(allData, 1440)
      }

      val candleSticks = allData.map { case (time, open, close, high, low) =>
        new CandleStick(time, open, close, high, low)
      }.toArray
          //

      
    }
    return candleSticks
  }
}