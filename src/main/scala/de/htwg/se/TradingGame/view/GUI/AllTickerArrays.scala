package de.htwg.se.TradingGame.view.GUI
import de.htwg.se.TradingGame.view.GUI.AdvCandleStickChartSample.CandleStick

import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import scala.io.Source

object AllTickerArrays {
    val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
    val file = new File(Path).getParent + s"/Symbols/EURUSDtest.csv"
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
    val firstline = Source.fromFile(file).getLines().take(1).toList.head
    val firstValues = firstline.split(",")
    val firstCandleEpochSec = LocalDateTime.parse(s"${firstValues(0)},${firstValues(1)}", formatter).atZone(ZoneId.systemDefault()).toEpochSecond()

    val candleSticks: Array[CandleStick] = {
        val lines = Source.fromFile(file).getLines().toList
        lines.tail.map { line =>
            val values = line.split(",")
            CandleStick(
                day = (LocalDateTime.parse(s"${values(0)},${values(1)}", formatter).atZone(ZoneId.systemDefault()).toEpochSecond() - firstCandleEpochSec) /60,
                open = values(2).toDouble,
                close = values(5).toDouble,
                high = values(3).toDouble,
                low = values(4).toDouble,
            )
        }.toArray
    }
}