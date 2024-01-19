package de.htwg.se.TradingGame.model.DataSave

import com.google.inject.Inject
import de.htwg.se.TradingGame.model.FileIO.TradeDataFileIO
import de.htwg.se.TradingGame.model.GetMarketDataComponent.GetMarketData.getFirsDateofFile
import de.htwg.se.TradingGame.model.GetMarketDataComponent.GetMarketData.getLastDateofFile
import de.htwg.se.TradingGame.model.GetMarketDataComponent.GetMarketData.getPairNames
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeDoneCalculations
import scalafx.scene.input.KeyCode.T

import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.xml.Elem
import scala.xml.Node
import scala.xml.XML

object TradeData {
  val trades: ArrayBuffer[TradeComponent] = ArrayBuffer.empty[TradeComponent]
  val donetrades: ArrayBuffer[TradeDoneCalculations] = ArrayBuffer.empty[TradeDoneCalculations]
  var balance: Double = 0.0
  var pair: String = ""
  var backtestDate: Long = 0
  var savename: String = ""
  var endDate: Long = 0
  var startDate: Long = 0
  var databaseConnectionString: String = ""
  val databaseStrings: List[String] = Source.fromResource("de/htwg/se/TradingGame/Database/DatabaseconnectionStrings").getLines().toList
  var pairList: List[String] = null
  var distancecandles = 3600
  var numbercandles = 100
  var interval = "1h"
  var lowestLoadedDate: Long = 0
  var highestLoadedDate: Long = 0
  var alwayslowestLoadedDate: Long = 0
  var alwayshighestLoadedDate: Long = 0
  val loadFileList: List[String] = 
    Files.list(Paths.get(getClass.getResource("/de/htwg/se/TradingGame/Data/").toURI))
      .iterator()
      .asScala
      .map(_.getFileName.toString)
      .toList
  
  def intervalasSeconds: Int = {
    interval match {
      case "1m" => 1 * 60 
      case "5m" => 5 * 60 
      case "15m" => 15 * 60 
      case "1h" => 60 * 60 
      case "4h" => 60 * 4 * 60 
      case "1d" => 60 * 24 * 60 
      case "1w" => 60 * 24 * 7 * 60 
      case _ => throw new IllegalArgumentException("Invalid interval")
    }
  }

  def printTradeData(): Unit = {
    println(s"Trades: $trades")
    println(s"Done Trades: $donetrades")
    println(s"Balance: $balance")
    println(s"Pair: $pair")
    println(s"Backtest Date: $backtestDate")
    println(s"Save Name: $savename")
  }


  def convertToEpochSeconds(dateString: String): Long = {
    val formatter = DateTimeFormatter.ofPattern("yyy.MM.dd,HH:mm")
    val date = LocalDateTime.parse(dateString, formatter)
    val epochSeconds = date.atZone(ZoneId.systemDefault()).toEpochSecond
    epochSeconds
  }
}

class TradeDataclass @Inject() (tradeDataFileIO: TradeDataFileIO) {

  def loadData(filename: String): Unit = {
    val (loadedDoneTrades, loadedBalance, loadedpair, loadedBacktestDate) = tradeDataFileIO.loadData(filename)

    TradeData.donetrades.clear()
    TradeData.donetrades ++= loadedDoneTrades
    TradeData.balance = loadedBalance
    TradeData.pair = loadedpair
    TradeData.backtestDate = loadedBacktestDate.toLong
  }

  def saveData(filename: String): Unit = {
    tradeDataFileIO.saveData(TradeData.donetrades, TradeData.balance,TradeData.pair, TradeData.backtestDate, filename)
  }
}
