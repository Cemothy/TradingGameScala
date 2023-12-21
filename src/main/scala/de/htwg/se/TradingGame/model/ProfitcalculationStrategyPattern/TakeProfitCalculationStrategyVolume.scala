package de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern
import de.htwg.se.TradingGame.model._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._

// Concrete Strategy A
class TakeProfitCalculationStrategyVolume extends ProfitCalculationStrategy {
  def calculateProfit(trade: TradeComponent): Double = {
    val tradewithvolume = trade.asInstanceOf[TradeWithVolume]
    val profit1 = math.abs(trade.entryTrade - trade.takeProfitTrade) * tradewithvolume.volume 

    val profit = BigDecimal(profit1).setScale(5, BigDecimal.RoundingMode.HALF_UP).toDouble
    profit
  }
}