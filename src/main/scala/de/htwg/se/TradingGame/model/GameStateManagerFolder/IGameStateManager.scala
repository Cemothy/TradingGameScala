package de.htwg.se.TradingGame.model.GameStateManagerFolder

import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder._
import de.htwg.se.TradingGame.model.GaneStateManagerFolder.GameCommand.IGameCommand
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.TradeDoneCalculations
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent

trait IGameStateManager {
  var currentState: GameState
  def executeCommand(command: IGameCommand): Unit
  def changeBalance(newBalance: Double): Unit
  def changeBacktestDate(newBacktestDate: Long): Unit
  def changeStartBalance(newStartBalance: Double): Unit
  def changePair(newPair: String): Unit
  def changeSaveName(newSaveName: String): Unit
  def changeEndDate(newEndDate: Long): Unit
  def changeStartDate(newStartDate: Long): Unit
  def changeDatabaseConnectionString(newDatabaseConnectionString: String): Unit
  def changeDistanceCandles(newDistanceCandles: Int): Unit
  def changeInterval(newInterval: String): Unit
  def changePairList(newPairList: List[String]): Unit
  def changeLoadFileList(newLoadFileList: List[String]): Unit
  def changeTrades(newTrades: scala.collection.mutable.ArrayBuffer[TradeComponent]): Unit
  def changeDoneTrades(newDoneTrades: scala.collection.mutable.ArrayBuffer[TradeDoneCalculations]): Unit
  def getCurrentState: GameState
  def loadCurrentState(): Unit
  def saveCurrentState(): Unit
}