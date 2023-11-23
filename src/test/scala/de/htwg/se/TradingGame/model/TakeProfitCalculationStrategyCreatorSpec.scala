package de.htwg.se.TradingGame.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.TakeProfitCalculationStrategyCreator
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeWithVolume
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Trade
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.TakeProfitCalculationStrategyVolume
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.TakeProfitCalculationStrategyRisk

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
