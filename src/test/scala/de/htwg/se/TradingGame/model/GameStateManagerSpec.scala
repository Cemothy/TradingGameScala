package de.htwg.se.TradingGame.model

import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder._
import de.htwg.se.TradingGame.model.GaneStateManagerFolder.GameCommand.IGameCommand
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.TradeDoneCalculations
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager

class IGameStateManagerSpec extends AnyWordSpec with Matchers {
  "An IGameStateManager" when {
    "new" should {
      var manager = new IGameStateManager {
        var currentState: GameState = _
        override def executeCommand(command: IGameCommand): Unit = {}
        override def changeBalance(newBalance: Double): Unit = {}
        override def changeBacktestDate(newBacktestDate: Long): Unit = {}
        override def changeStartBalance(newStartBalance: Double): Unit = {}
        override def changePair(newPair: String): Unit = {}
        override def changeSaveName(newSaveName: String): Unit = {}
        override def changeEndDate(newEndDate: Long): Unit = {}
        override def changeStartDate(newStartDate: Long): Unit = {}
        override def changeDatabaseConnectionString(newDatabaseConnectionString: String): Unit = {}
        override def changeDistanceCandles(newDistanceCandles: Int): Unit = {}
        override def changeInterval(newInterval: String): Unit = {}
        override def changePairList(newPairList: List[String]): Unit = {}
        override def changeLoadFileList(newLoadFileList: List[String]): Unit = {}
        override def changeTrades(newTrades: scala.collection.mutable.ArrayBuffer[TradeComponent]): Unit = {}
        override def changeDoneTrades(newDoneTrades: scala.collection.mutable.ArrayBuffer[TradeDoneCalculations]): Unit = {}
        override def getCurrentState: GameState = currentState
        override def loadCurrentState(): Unit = {}
        override def saveCurrentState(): Unit = {}
      }

      "have a currentState that is null" in {
        manager.currentState should be (null)
      }

      // Add more tests here
    }
  }
}