package de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteFactorieCreators

import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.ProfitcalculationStrategyPattern._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteStrategies._

// Concrete Creator A
class TakeProfitCalculationStrategyCreator extends ProfitCalculationStrategyCreator {
  def createProfitCalculationStrategy(trade: TradeComponent): ProfitCalculationStrategy = {
  trade match {
    case volumeTrade: TradeWithVolume =>
      new TakeProfitCalculationStrategyVolume()
    case _ =>
      new TakeProfitCalculationStrategyRisk()
    }
  }
}

