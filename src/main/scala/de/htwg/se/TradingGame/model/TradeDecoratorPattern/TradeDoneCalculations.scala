package de.htwg.se.TradingGame.model.TradeDecoratorPattern

import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import de.htwg.se.TradingGame.model.GetMarketData

class TradeDoneCalculations(trade: TradeComponent) extends TradeDecorator(trade) {
  
  val getMarketData = new GetMarketData

  val dateTradeTriggered: String = getMarketData.dateWhenTradeTriggered(trade)
  val tradeWinOrLose: String = getMarketData.didTradeWinnorLoose(trade)
  val dateTradeDone: String = getMarketData.datewhenTradeisdone(trade)
  var currentprofit: Double = 0.0

  val creator: ProfitCalculationStrategyCreator = tradeWinOrLose match {
    case "Trade hit take profit" => new TakeProfitCalculationStrategyCreator()
    case "Trade hit stop loss" => new StopLossCalculationStrategyCreator()
    case "Trade did not hit take profit or stop loss" => new ProfitsetttoZeroStrategyCreator()
    case _ => new ProfitsetttoZeroStrategyCreator()
}

  val strategy: ProfitCalculationStrategy = creator.createProfitCalculationStrategy(trade)


  val endProfit: Double = strategy.calculateProfit(trade)
}