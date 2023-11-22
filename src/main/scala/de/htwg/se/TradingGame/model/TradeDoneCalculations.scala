package de.htwg.se.TradingGame.model

class TradeDoneCalculations(
  trade: TradeComponent,
  val dateTradeTriggered: String,
  val dateTradeDone: String,
  val tradeWinOrLose: String,
) extends TradeDecorator(trade) {

    if(trade.isInstanceOf[TradeActive]) {
      val tradeActive = trade.asInstanceOf[TradeActive]
      var profit: Double = 0.0
        if(tradeWinOrLose.equals("Trade hit take profit")){
            val entryPrice = trade.entryTrade
            val stopLossPrice = trade.stopLossTrade
            val takeProfitPrice = trade.takeProfitTrade
            val distanceFromEntryToStopLoss = math.abs(entryPrice - stopLossPrice)
            val distanceFromEntryToTakeProfit = math.abs(entryPrice - takeProfitPrice)
            val factor = distanceFromEntryToTakeProfit / distanceFromEntryToStopLoss
            profit = trade.risk * 0.01 * factor
            profit = BigDecimal(profit).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
        
        } else if(tradeWinOrLose.equals("Trade hit stop loss")){
        profit = trade.risk * 0.01 * -1
        profit = BigDecimal(profit).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
        }

      tradeActive.setcurrentProfitto(profit)
    }

  
}
