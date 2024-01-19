package de.htwg.se.TradingGame.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.TradeWithVolume
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.Trade
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteFactorieCreators.TakeProfitCalculationStrategyCreator
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteStrategies.TakeProfitCalculationStrategyRisk
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteStrategies.TakeProfitCalculationStrategyVolume

class TakeProfitCalculationStrategyVolumeSpec extends AnyWordSpec with Matchers {
    "A TakeProfitCalculationStrategyVolume" when {
        "calculateProfit method is called" should {
            "return the correct profit" in {
                val strategy = new TakeProfitCalculationStrategyVolume()
                val trade = new TradeWithVolume(Trade(10.0, 5, 20, 2.0, "2023.03.03", "EURUSD"),100) 
                val expectedProfit = 1000.0
                
                val actualProfit = strategy.calculateProfit(trade)
                
                actualProfit should be(expectedProfit)
            }
        }
    }
}
