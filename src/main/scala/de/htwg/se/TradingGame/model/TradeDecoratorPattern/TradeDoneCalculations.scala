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
    // Add more cases if needed
  }

  val strategy: ProfitCalculationStrategy = creator.createProfitCalculationStrategy(trade)
  val profit: Double = strategy.calculateProfit(trade)

  val endProfit: Double = profit
}