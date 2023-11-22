package de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern
import de.htwg.se.TradingGame.model._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
// The Strategy interface
trait ProfitCalculationStrategy {
  def calculateProfit(trade: TradeComponent): Double
}
