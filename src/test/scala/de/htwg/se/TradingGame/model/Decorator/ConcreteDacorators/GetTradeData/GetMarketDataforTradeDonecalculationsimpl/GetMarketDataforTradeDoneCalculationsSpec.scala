package de.htwg.se.TradingGame.model.Decorator.ConcreteDacorators.GetTradeData.GetMarketDataforTradeDonecalculationsimpl

import de.htwg.se.TradingGame.model.GameStateManagerFolder.FileIO.FileIOXMLComponent.TradeDataXMLFileIO
import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder.GameState
import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateManagerImplementation.GameStateManager
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.GetTradeData.GetMarketDataforTradeDonecalculationsimpl.GetMarketDataforTradeDoneCalculations
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.TradeDoneCalculations
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.Trade
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.LocalDateTime

class GetMarketDataforTradeDoneCalculationsSpec extends AnyFlatSpec with Matchers {
  val trade1 = new Trade(10, 5, 20, 2.0, "2023.04.02,12:12", "EURUSD")
  val trade2 = new Trade(1.06873, 1.04717, 1.08410, 2.0, "2017.01.23,12:12", "EURUSD")
  val fileio = new TradeDataXMLFileIO
  val gameStateManager: IGameStateManager = new GameStateManager(fileio)
  gameStateManager.changeDatabaseConnectionString("jdbc:sqlite:src/main/scala/de/htwg/se/TradingGame/Database/litedbCandleSticks.db")
  val tradeDoneCalculations1 = new TradeDoneCalculations(trade1, gameStateManager)
  val tradeDoneCalculations2 = new TradeDoneCalculations(trade2, gameStateManager)


  "GetMarketDataforTradeDoneCalculations" should "have right currentprofit" in {
      tradeDoneCalculations1.currentprofit should be(0.0)
    }

    it should "have right endProfit" in {
      tradeDoneCalculations1.endProfit should be(0.0)
    }

    it should "have right tradeWinOrLose" in {
      tradeDoneCalculations1.tradeWinOrLose should be("Trade was not triggered")
    }

    it should "have right dateTradeDone" in {
      tradeDoneCalculations1.dateTradeDone should be("Trade was not triggered")
    }

    it should "have right dateTradeTriggered" in {
      tradeDoneCalculations1.dateTradeTriggered should be("Trade was not triggered")
    }

  "GetMarketDataforTradeDoneCalculations with different trade data" should "have a current profit of 0.0" in {
    tradeDoneCalculations2.currentprofit should be(0.0)
  }

  it should "endProfit have an end profit of 0.0" in {
    tradeDoneCalculations2.endProfit should be(0.0)
  }

  it should "tradeWinOrLose indicate that the trade was not triggered" in {
    tradeDoneCalculations2.tradeWinOrLose should be("Trade was not triggered")
  }

  it should "dateTradeDone indicate that the trade was not done" in {
    tradeDoneCalculations2.dateTradeDone should be("Trade was not triggered")
  }

  it should "dateTradeTriggered indicate that the trade was not triggered" in {
    tradeDoneCalculations2.dateTradeTriggered should be("Trade was not triggered")
  }
}