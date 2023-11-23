package de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern

import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeWithVolume

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

