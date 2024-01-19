package de.htwg.se.TradingGame.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.TradeWithVolume
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.Trade
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteFactorieCreators.TakeProfitCalculationStrategyCreator
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteStrategies.TakeProfitCalculationStrategyRisk
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteStrategies.TakeProfitCalculationStrategyVolume

class TakeProfitCalculationStrategyCreatorSpec extends AnyWordSpec with Matchers {
    "TakeProfitCalculationStrategyCreator" should {
        "create a TakeProfitCalculationStrategyVolume when given a TradeWithVolume" in {
            val creator = new TakeProfitCalculationStrategyCreator()
            val trade = new TradeWithVolume(Trade(10.0, 5, 20, 2.0, "2023.03.03", "EURUSD"),100)
            val strategy = creator.createProfitCalculationStrategy(trade)
            strategy shouldBe a[TakeProfitCalculationStrategyVolume]
        }
        
        "create a TakeProfitCalculationStrategyRisk when given any other TradeComponent" in {
            val creator = new TakeProfitCalculationStrategyCreator()
            val trade = new Trade(10.0, 5, 20, 2.0, "2023.03.03", "EURUSD")
            val strategy = creator.createProfitCalculationStrategy(trade)
            strategy shouldBe a[TakeProfitCalculationStrategyRisk]
        }
    }
}
