package de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteStrategies
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.ProfitcalculationStrategyPattern.ProfitCalculationStrategy
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent

class StopLossCalculationStrategyRisk extends ProfitCalculationStrategy {
  def calculateProfit(trade: TradeComponent): Double = {
    val profit1 = trade.risk * 0.01 * -1
    val profit = BigDecimal(profit1).setScale(5, BigDecimal.RoundingMode.HALF_UP).toDouble
    profit
  }
}
