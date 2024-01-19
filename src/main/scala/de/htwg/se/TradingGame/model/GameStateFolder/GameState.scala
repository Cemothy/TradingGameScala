package de.htwg.se.TradingGame.model.GameStateFolder
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeDoneCalculations
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import java.util.concurrent.CopyOnWriteArrayList
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
}

