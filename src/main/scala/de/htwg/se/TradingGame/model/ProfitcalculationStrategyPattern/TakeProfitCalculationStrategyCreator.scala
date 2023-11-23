package de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern

import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent

// Concrete Creator A
class TakeProfitCalculationStrategyCreator extends ProfitCalculationStrategyCreator {
  def createProfitCalculationStrategy(trade: TradeComponent): ProfitCalculationStrategy = {
    // Create and return a TakeProfitCalculationStrategy object
    new TakeProfitCalculationStrategy()
  }
}
