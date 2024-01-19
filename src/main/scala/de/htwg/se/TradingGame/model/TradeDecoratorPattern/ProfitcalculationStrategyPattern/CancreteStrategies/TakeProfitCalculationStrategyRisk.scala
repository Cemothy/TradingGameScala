package de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteStrategies

import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.ProfitcalculationStrategyPattern.ProfitCalculationStrategy
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
// Concrete Strategy A
class TakeProfitCalculationStrategyRisk extends ProfitCalculationStrategy {
  def calculateProfit(trade: TradeComponent): Double = {
    val entryPrice = trade.entryTrade
    val stopLossPrice = trade.stopLossTrade
    val takeProfitPrice = trade.takeProfitTrade
    val distanceFromEntryToStopLoss = math.abs(entryPrice - stopLossPrice)
    val distanceFromEntryToTakeProfit = math.abs(entryPrice - takeProfitPrice)
    val factor = distanceFromEntryToTakeProfit / distanceFromEntryToStopLoss
    val profit1 = trade.risk * 0.01 * factor
    val profit = BigDecimal(profit1).setScale(5, BigDecimal.RoundingMode.HALF_UP).toDouble
    profit
  }
}