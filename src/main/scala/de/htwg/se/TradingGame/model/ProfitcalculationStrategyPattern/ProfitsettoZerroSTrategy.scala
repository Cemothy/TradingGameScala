package de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern
import de.htwg.se.TradingGame.model._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._

// Concrete Strategy B
class ProfitsetttoZerroSTrategy extends ProfitCalculationStrategy {
  def calculateProfit(trade: TradeComponent): Double = {

    0.0
  }
}


