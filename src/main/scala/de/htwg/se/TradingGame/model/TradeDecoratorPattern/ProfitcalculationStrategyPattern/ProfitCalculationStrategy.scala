package de.htwg.se.TradingGame.model.TradeDecoratorPattern.ProfitcalculationStrategyPattern
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import de.htwg.se.TradingGame.model._

trait ProfitCalculationStrategy {
  def calculateProfit(trade: TradeComponent): Double
}
