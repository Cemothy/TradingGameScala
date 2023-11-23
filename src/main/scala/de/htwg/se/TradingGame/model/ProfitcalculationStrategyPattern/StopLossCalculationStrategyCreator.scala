package de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern

import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent

// Concrete Creator B
class StopLossCalculationStrategyCreator extends ProfitCalculationStrategyCreator {
  def createProfitCalculationStrategy(trade: TradeComponent): ProfitCalculationStrategy = {
    // Create and return a StopLossCalculationStrategy object
    new StopLossCalculationStrategy()
  }
}
