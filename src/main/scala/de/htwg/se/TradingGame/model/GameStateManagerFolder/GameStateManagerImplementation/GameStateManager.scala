package de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateManagerImplementation


import com.google.inject.Inject
import de.htwg.se.TradingGame.controller._
import de.htwg.se.TradingGame.model.FileIO.TradeDataFileIO
import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder.DefaultGameStateimpl.DefaultGameState
import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder._
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model.GaneStateManagerFolder.GameCommand.ChangegameCommandImplementation._
import de.htwg.se.TradingGame.model.GaneStateManagerFolder.GameCommand.IGameCommand
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.TradeDoneCalculations
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent

class GameStateManager  @Inject() (fileIO: TradeDataFileIO) extends IGameStateManager{
  var currentState: GameState = new DefaultGameState()
  def executeCommand(command: IGameCommand): Unit = currentState = command.execute(currentState)
  def changeBalance(newBalance: Double): Unit = executeCommand(new ChangeBalanceCommand(newBalance))
  def changeBacktestDate(newBacktestDate: Long): Unit = executeCommand(new ChangeBacktestDateCommand(newBacktestDate))
  def changeStartBalance(newStartBalance: Double): Unit = executeCommand(new ChangeStartBalanceCommand(newStartBalance))
  def changePair(newPair: String): Unit = executeCommand(new ChangePairCommand(newPair))
  def changeSaveName(newSaveName: String): Unit = executeCommand(new ChangeSaveNameCommand(newSaveName))
  def changeEndDate(newEndDate: Long): Unit = executeCommand(new ChangeEndDateCommand(newEndDate))
  def changeStartDate(newStartDate: Long): Unit = executeCommand(new ChangeStartDateCommand(newStartDate))
  def changeDatabaseConnectionString(newDatabaseConnectionString: String): Unit = executeCommand(new ChangeDatabaseConnectionStringCommand(newDatabaseConnectionString))
  def changeDistanceCandles(newDistanceCandles: Int): Unit = executeCommand(new ChangeDistanceCandlesCommand(newDistanceCandles))
  def changeInterval(newInterval: String): Unit = executeCommand(new ChangeIntervalCommand(newInterval))
  def changePairList(newPairList: List[String]): Unit = executeCommand(new ChangePairListCommand(newPairList))
  def changeLoadFileList(newLoadFileList: List[String]): Unit = executeCommand(new ChangeLoadFileListCommand(newLoadFileList))
  def changeTrades(newTrades: scala.collection.mutable.ArrayBuffer[TradeComponent]): Unit = executeCommand(new ChangeTradesCommand(newTrades))
  def changeDoneTrades(newDoneTrades: scala.collection.mutable.ArrayBuffer[TradeDoneCalculations]): Unit = executeCommand(new ChangeDoneTradesCommand(newDoneTrades))
  def getCurrentState: GameState = currentState
  def loadCurrentState(): Unit = currentState = fileIO.loadData(currentState.savename, this)
  def saveCurrentState(): Unit = fileIO.saveData(currentState, currentState.savename)
}