package de.htwg.se.TradingGame.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import de.htwg.se.TradingGame.model.GetMarketData._

class TradeDoneCalculationsSpec extends AnyWordSpec with Matchers {
    "A TradeDoneCalculations" when {
        "created" should {
            "calculate the end profit correctly" in {
                val trade: TradeComponent = Trade(1.07, 1.06, 1.08, 2.0, "2023.03.03,12:12", "EURUSD")
                val tradeDoneCalculations: TradeDoneCalculations = new TradeDoneCalculations(trade)

                // Assert the expected values for the properties of TradeDoneCalculations
                tradeDoneCalculations.dateTradeTriggered shouldBe "2023.03.03,12:13"
                tradeDoneCalculations.tradeWinOrLose shouldBe "Trade hit stop loss"
                tradeDoneCalculations.dateTradeDone shouldBe "2023.03.03,17:02"
                tradeDoneCalculations.profit shouldBe -0.02
                tradeDoneCalculations.endProfit shouldBe -0.02
            }

            "calculate the end profit correctly when trade hits take profit" in {
                val trade: TradeComponent = Trade(1.07, 1.05, 1.072, 2.0, "2023.03.03,12:12", "EURUSD")
                val tradeDoneCalculations: TradeDoneCalculations = new TradeDoneCalculations(trade)

                // Assert the expected values for the properties of TradeDoneCalculations
                tradeDoneCalculations.dateTradeTriggered shouldBe "2023.03.03,12:13"
                tradeDoneCalculations.tradeWinOrLose shouldBe "Trade hit take profit"
                tradeDoneCalculations.dateTradeDone shouldBe "2023.03.13,04:53"
                tradeDoneCalculations.profit shouldBe 0.002
                tradeDoneCalculations.endProfit shouldBe 0.002
            }
        }
    }
}
