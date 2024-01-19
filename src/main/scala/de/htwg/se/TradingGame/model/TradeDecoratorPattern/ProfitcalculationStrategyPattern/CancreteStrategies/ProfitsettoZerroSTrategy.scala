package de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteStrategies
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.ProfitcalculationStrategyPattern.ProfitCalculationStrategy
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
// Concrete Strategy B
class ProfitsetttoZerroSTrategy extends ProfitCalculationStrategy {
  def calculateProfit(trade: TradeComponent): Double = {

    0.0
  }
}


