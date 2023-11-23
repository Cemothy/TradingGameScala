package de.htwg.se.TradingGame.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.TakeProfitCalculationStrategyVolume
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeWithVolume
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Trade
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.TakeProfitCalculationStrategyRisk

class TakeProfitCalculationStrategyRiskSpec extends AnyWordSpec with Matchers {
    "A TakeProfitCalculationStrategyRisk" when {
        "calculateProfit method is called" should {
            "return the correct profit" in {
                val strategy = new TakeProfitCalculationStrategyRisk()
                val trade = new Trade(100.0, 90.0, 110.0, 10.0, "2023.03.03,12:12", "EURUSD")
                val expectedProfit = 0.10
                
                val actualProfit = strategy.calculateProfit(trade)
                
                actualProfit should be(expectedProfit)
            }
        }
    }
}
