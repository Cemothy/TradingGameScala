package de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern
import de.htwg.se.TradingGame.model._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import de.htwg.se.TradingGame.model.FileChooser.getSymbolPath
// Concrete Strategy A
class TakeProfitCalculationStrategyVolume extends ProfitCalculationStrategy {
  def calculateProfit(trade: TradeComponent): Double = {
            val entryPrice = trade.entryTrade
            val stopLossPrice = trade.stopLossTrade
            val takeProfitPrice = trade.takeProfitTrade
            val distanceFromEntryToStopLoss = math.abs(entryPrice - stopLossPrice)
            val distanceFromEntryToTakeProfit = math.abs(entryPrice - takeProfitPrice)
            val factor = distanceFromEntryToTakeProfit / distanceFromEntryToStopLoss
            val profit1 = trade.risk * 0.01 * factor
            val profit = BigDecimal(profit1).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
            profit
  }
}