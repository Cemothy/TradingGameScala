package de.htwg.se.TradingGame.model.TradeDecoratorPattern

import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._

class TradeDoneCalculations(
  trade: TradeComponent,
  val dateTradeTriggered: String,
  val dateTradeDone: String,
  val tradeWinOrLose: String,
  val endProfit: Double
) extends TradeDecorator(trade) {

  val creator: ProfitCalculationStrategyCreator = trade match {
    case _: TradeWithVolume =>
      new VolumeProfitCalculationStrategyCreator()
    case _ =>
      new RiskProfitCalculationStrategyCreator()
  }

  val strategy: ProfitCalculationStrategy = creator.createProfitCalculationStrategy(trade)

  val profit: Double = strategy.calculateProfit(trade)

  trade match {
    case activeTrade: TradeActive => {
      activeTrade.isActive = false
      if(!activeTrade.isActive){
      activeTrade.setcurrentProfitto(0.0)
      endProfit = profit
      }
    }
    case _ =>
      // Handle other cases if needed
  }
}