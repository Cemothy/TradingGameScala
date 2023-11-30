package de.htwg.se.TradingGame.model.TradeDecoratorPattern

import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import de.htwg.se.TradingGame.model.GetMarketData._

class TradeDoneCalculations(trade: TradeComponent) extends TradeDecorator(trade) {
  val dateTradeTriggered: String = dateWhenTradeTriggered(trade)
  val tradeWinOrLose: String = didTradeWinnorLoose(trade)
  val dateTradeDone: String = datewhenTradeisdone(trade)

  val creator: ProfitCalculationStrategyCreator = tradeWinOrLose match {
    case "Trade hit take profit" => new TakeProfitCalculationStrategyCreator()
    case "Trade hit stop loss" => new StopLossCalculationStrategyCreator()
    case "Trade did not hit take profit or stop loss" => new ProfitsetttoZeroStrategyCreator()
    case _ => new ProfitsetttoZeroStrategyCreator()
}

  val strategy: ProfitCalculationStrategy = creator.createProfitCalculationStrategy(trade)
  val profit: Double = strategy.calculateProfit(trade)

  val endProfit: Double = profit
}