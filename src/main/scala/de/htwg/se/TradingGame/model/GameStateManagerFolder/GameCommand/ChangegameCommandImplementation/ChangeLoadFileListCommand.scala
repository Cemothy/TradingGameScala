package de.htwg.se.TradingGame.model.GaneStateManagerFolder.GameCommand.ChangegameCommandImplementation

import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import scala.collection.mutable.ArrayBuffer
import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder.GameState
import de.htwg.se.TradingGame.model.GaneStateManagerFolder.GameCommand.IGameCommand
import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder.DefaultGameStateimpl.DefaultGameState
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.TradeDoneCalculations
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent

class ChangeLoadFileListCommand(newLoadFileList: List[String]) extends IGameCommand {
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
      override def distancecandles: Int = state.distancecandles
      override def interval: String = state.interval
      override def pairList: List[String] = state.pairList
      override def loadFileList: List[String] = newLoadFileList
    }
  }
}