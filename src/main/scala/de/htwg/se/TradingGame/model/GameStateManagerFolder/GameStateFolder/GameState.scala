package de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent

import java.util.concurrent.CopyOnWriteArrayList
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._
trait GameState {
  def balance: Double
  def backtestDate: Long
  def trades: ArrayBuffer[TradeComponent]
  def doneTrades: ArrayBuffer[TradeDoneCalculations]
  def startbalance: Double
  def pair: String
  def savename: String
  def endDate: Long
  def startDate: Long
  def databaseConnectionString: String
  def pairList: List[String]
  def distancecandles: Int
  def interval: String
  def loadFileList: List[String]
  override def toString: String = {
  s"""
    |Balance: $balance
    |Backtest Date: $backtestDate
    |Trades: $trades
    |Done Trades: $doneTrades
    |Start Balance: $startbalance
    |Pair: $pair
    |Save Name: $savename
    |End Date: $endDate
    |Start Date: $startDate
    |Database Connection String: $databaseConnectionString
    |Pair List: $pairList
    |Distance Candles: $distancecandles
    |Interval: $interval
    |Load File List: $loadFileList
  """.stripMargin
  }
  override def equals(obj: Any): Boolean = obj match {
    case other: GameState =>
      balance == other.balance &&
      backtestDate == other.backtestDate &&
      trades == other.trades &&
      doneTrades == other.doneTrades &&
      startbalance == other.startbalance &&
      pair == other.pair &&
      savename == other.savename &&
      endDate == other.endDate &&
      startDate == other.startDate &&
      databaseConnectionString == other.databaseConnectionString &&
      pairList == other.pairList &&
      distancecandles == other.distancecandles &&
      interval == other.interval &&
      loadFileList == other.loadFileList
    case _ => false
  }
  override def hashCode(): Int = {
    val prime = 31
    var result = 1
    result = prime * result + balance.hashCode
    result = prime * result + backtestDate.hashCode
    result = prime * result + trades.hashCode
    result = prime * result + doneTrades.hashCode
    result = prime * result + startbalance.hashCode
    result = prime * result + pair.hashCode
    result = prime * result + savename.hashCode
    result = prime * result + endDate.hashCode
    result = prime * result + startDate.hashCode
    result = prime * result + databaseConnectionString.hashCode
    result = prime * result + pairList.hashCode
    result = prime * result + distancecandles.hashCode
    result = prime * result + interval.hashCode
    result = prime * result + loadFileList.hashCode
    result
  }
}

