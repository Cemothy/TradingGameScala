package de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteStrategies
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.ProfitcalculationStrategyPattern.ProfitCalculationStrategy
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
// Concrete Strategy B
class StopLossCalculationStrategyVolume extends ProfitCalculationStrategy {
  def calculateProfit(trade: TradeComponent): Double = {

    val tradewithvolume = trade.asInstanceOf[TradeWithVolume]
    val profit1 = math.abs(trade.entryTrade - trade.stopLossTrade) * tradewithvolume.volume * -1

    val profit = BigDecimal(profit1).setScale(5, BigDecimal.RoundingMode.HALF_UP).toDouble
    profit
  }
}


