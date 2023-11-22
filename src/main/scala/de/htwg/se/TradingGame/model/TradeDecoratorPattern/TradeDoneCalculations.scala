package de.htwg.se.TradingGame.model.TradeDecoratorPattern

import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern._

class TradeDoneCalculations(
  trade: TradeComponent,
  val dateTradeTriggered: String,
  val dateTradeDone: String,
  val tradeWinOrLose: String,
) extends TradeDecorator(trade) {

    if(trade.isInstanceOf[TradeActive]) {
      val tradeActive = trade.asInstanceOf[TradeActive]
      val strategy: ProfitCalculationStrategy = tradeWinOrLose match {
        case "Trade hit take profit" => new TakeProfitCalculationStrategy()
        case "Trade hit stop loss" => new StopLossCalculationStrategy()
      }

      val profit: Double = strategy.calculateProfit(trade)

      tradeActive.setcurrentProfitto(profit)
    }

  
}
