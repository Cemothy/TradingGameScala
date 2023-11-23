package de.htwg.se.TradingGame.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.StopLossCalculationStrategyVolume
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeWithVolume
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Trade

class StopLossCalculationStrategyVolumeSpec extends AnyFlatSpec with Matchers {
    // Test case 1: Calculate profit for a trade with positive entry and stop loss
    "StopLossCalculationStrategyVolume" should "calculate profit correctly for a trade with positive entry and stop loss" in {
        val strategy = new StopLossCalculationStrategyVolume()
        val trade = new TradeWithVolume(Trade(10, 5, 20, 2.0, "2023.04.02,12:12", "EURUSD"), 1000) 
        val expectedProfit = -5000.0 // (10 - 5) * 100 * -1 = -500

        val actualProfit = strategy.calculateProfit(trade)

        actualProfit should be(expectedProfit)
    }

    // Test case 2: Calculate profit for a trade with negative entry and stop loss
    it should "calculate profit correctly for a trade with negative entry and stop loss" in {
        val strategy = new StopLossCalculationStrategyVolume()
        val trade = new TradeWithVolume(Trade(5, 10, 2, 2.0, "2023.04.02,12:12", "EURUSD"), 200) 
        val expectedProfit = -1000.0 

        val actualProfit = strategy.calculateProfit(trade)

        actualProfit should be(expectedProfit)
    }

}
