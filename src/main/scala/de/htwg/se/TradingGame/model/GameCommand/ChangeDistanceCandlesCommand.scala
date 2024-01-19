package de.htwg.se.TradingGame.model.GameCommand

import de.htwg.se.TradingGame.model.GameStateFolder._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
class ChangeDistanceCandlesCommand(newDistanceCandles: Int) extends GameCommand {
  override def execute(state: GameState): GameState = {
    new DefaultGameState {
      override def balance: Double = state.balance
      override def backtestDate: Long = state.backtestDate
      override def trades: ArrayBuffer[TradeComponent] = state.trades
      override def doneTrades: ArrayBuffer[TradeDoneCalculations] = state.doneTrades
      override def startbalance: Double = state.startbalance
      override def pair: String = state.pair
      override def savename: String = state.savename
      override def endDate: Long = state.endDate
      override def startDate: Long = state.startDate
      override def databaseConnectionString: String = state.databaseConnectionString
      override def distancecandles: Int = newDistanceCandles
      override def interval: String = state.interval
      override def pairList: List[String] = state.pairList
      override def loadFileList: List[String] = state.loadFileList
    }
  }
}