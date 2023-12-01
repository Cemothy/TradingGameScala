package de.htwg.se.TradingGame.view.GUI

import javafx.scene.chart.XYChart
import scalafx.scene.chart.LineChart
import scalafx.scene.chart.NumberAxis

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChartDataLoader {
  def loadDataAndUpdateChart(lineChart: LineChart[Number, Number], xAxis: NumberAxis, yAxis: NumberAxis, ticker: String, datapoints: Int): Unit = {
    val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
    val file = new java.io.File(Path).getParent + s"/Symbols/${ticker}.csv"
    val fileObj = new File(file)
    if (fileObj.exists()) {
      val allData = io.Source.fromFile(file).getLines().toList.drop(1).map(_.split(",")).map(arr => (LocalDateTime.parse(arr(0)+ "," +  arr(1), DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")), arr(5).toDouble))
      val data = allData.takeRight(datapoints) // Take only the last 1000 data points
      val series = new XYChart.Series[Number, Number]()
      data.foreach(d => series.getData.add(new XYChart.Data[Number, Number](d._1.toEpochSecond(java.time.ZoneOffset.UTC), d._2)))
      lineChart.getData.clear()
      lineChart.getData.add(series)
      xAxis.setAutoRanging(false)
      val numDataPoints = data.length
      val timeInterval = data.last._1.toEpochSecond(java.time.ZoneOffset.UTC) - data.head._1.toEpochSecond(java.time.ZoneOffset.UTC)
      val timePerDataPoint = timeInterval / numDataPoints
      val visibleDataPoints = 200
      val lowerBound = data.last._1.toEpochSecond(java.time.ZoneOffset.UTC) - (visibleDataPoints * timePerDataPoint)
      xAxis.setLowerBound(lowerBound)
      xAxis.setUpperBound(data.last._1.toEpochSecond(java.time.ZoneOffset.UTC))
      yAxis.setAutoRanging(false)
      val minValue = data.takeRight(visibleDataPoints).map(_._2).min
      val maxValue = data.takeRight(visibleDataPoints).map(_._2).max
      val range = maxValue - minValue
      yAxis.setLowerBound(minValue - range * 0.1) // Set lower bound to 10% below the minimum value
      yAxis.setUpperBound(maxValue + range * 0.1) // Set upper bound to 10% above the maximum value
      yAxis.setTickUnit(range * 1.2) // Set tick unit to 20% of the range
    }
  }
}
