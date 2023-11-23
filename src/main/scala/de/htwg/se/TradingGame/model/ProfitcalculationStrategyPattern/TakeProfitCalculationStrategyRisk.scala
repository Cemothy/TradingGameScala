package de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern
import de.htwg.se.TradingGame.model._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import de.htwg.se.TradingGame.model.FileChooser.getSymbolPath
// Concrete Strategy A
class TakeProfitCalculationStrategyRisk extends ProfitCalculationStrategy {
  def calculateProfit(trade: TradeComponent): Double = {
            val tradewithvolume = trade.asInstanceOf[TradeWithVolume]
            val profit1 = math.abs(trade.entryTrade - trade.stopLossTrade) * tradewithvolume.volume

            val profit = BigDecimal(profit1).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
            profit
  }
}