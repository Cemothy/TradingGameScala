package de.htwg.se.TradingGame.model.TradeDecoratorPattern.ProfitcalculationStrategyPattern

import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent

abstract class ProfitCalculationStrategyCreator {
  def createProfitCalculationStrategy(trade: TradeComponent): ProfitCalculationStrategy
}