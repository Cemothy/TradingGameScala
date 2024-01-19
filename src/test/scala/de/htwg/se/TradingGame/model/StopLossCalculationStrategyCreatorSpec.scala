package de.htwg.se.TradingGame.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.TradeWithVolume
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.Trade
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteFactorieCreators.StopLossCalculationStrategyCreator
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteStrategies.StopLossCalculationStrategyRisk
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteStrategies.StopLossCalculationStrategyVolume


class StopLossCalculationStrategyCreatorSpec extends AnyWordSpec with Matchers {
    "StopLossCalculationStrategyCreator" should {
  val creator = new StopLossCalculationStrategyCreator()

  "create a StopLossCalculationStrategyVolume when trade is a TradeWithVolume" in {
    val trade = new TradeWithVolume(Trade(10.0, 5, 20, 2.0, "2023.03.03", "EURUSD"),100)
    val strategy = creator.createProfitCalculationStrategy(trade)
    strategy shouldBe a[StopLossCalculationStrategyVolume]
  }

  "create a StopLossCalculationStrategyRisk when trade is not a TradeWithVolume" in {
    val trade = new Trade(10.0, 5, 20, 2.0, "2023.03.03", "EURUSD")
    val strategy = creator.createProfitCalculationStrategy(trade)
    strategy shouldBe a[StopLossCalculationStrategyRisk]
  }
}
}
