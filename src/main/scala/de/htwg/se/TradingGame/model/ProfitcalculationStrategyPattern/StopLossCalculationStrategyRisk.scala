package de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern
import de.htwg.se.TradingGame.model._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
// Concrete Strategy B
class StopLossCalculationStrategyRisk extends ProfitCalculationStrategy {
  def calculateProfit(trade: TradeComponent): Double = {
    val profit1 = trade.risk * 0.01 * -1
    val profit = BigDecimal(profit1).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
    profit
  }
}
