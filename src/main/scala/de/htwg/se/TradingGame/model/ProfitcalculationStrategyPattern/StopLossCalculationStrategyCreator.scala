package de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern

import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeWithVolume

// Concrete Creator B
class StopLossCalculationStrategyCreator extends ProfitCalculationStrategyCreator {
  def createProfitCalculationStrategy(trade: TradeComponent): ProfitCalculationStrategy = {
    trade match {
      case volumeTrade: TradeWithVolume =>
        new StopLossCalculationStrategyVolume()
      case _ =>
      // Create and return a StopLossCalculationStrategy object
      new StopLossCalculationStrategyRisk()  
    }
  }
}
