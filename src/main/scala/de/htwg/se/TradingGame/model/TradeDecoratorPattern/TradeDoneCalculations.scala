package de.htwg.se.TradingGame.model.TradeDecoratorPattern

import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import de.htwg.se.TradingGame.model.GetMarketDataComponent.GetMarketData._
import scala.xml.Node

class TradeDoneCalculations(trade: TradeComponent) extends TradeDecorator(trade) {
  var dateTradeTriggered: String = dateWhenTradeTriggered(trade)
  var tradeWinOrLose: String = didTradeWinnorLoose(trade)
  var dateTradeDone: String = datewhenTradeisdone(trade)
  var currentprofit: Double = 0.0

  val creator: ProfitCalculationStrategyCreator = tradeWinOrLose match {
    case "Trade hit take profit" => new TakeProfitCalculationStrategyCreator()
    case "Trade hit stop loss" => new StopLossCalculationStrategyCreator()
    case "Trade did not hit take profit or stop loss" => new ProfitsetttoZeroStrategyCreator()
    case _ => new ProfitsetttoZeroStrategyCreator()
}

  val strategy: ProfitCalculationStrategy = creator.createProfitCalculationStrategy(trade)


  var endProfit: Double = strategy.calculateProfit(trade)


  // Second constructor that takes all values as inputs
  def this(trade: TradeComponent, dateTradeTriggered: String, tradeWinOrLose: String, dateTradeDone: String, currentprofit: Double, endProfit: Double) = {
    this(trade) // Call the primary constructor
    this.dateTradeTriggered = dateTradeTriggered
    this.tradeWinOrLose = tradeWinOrLose
    this.dateTradeDone = dateTradeDone
    this.currentprofit = currentprofit
    this.endProfit = endProfit
  }
}