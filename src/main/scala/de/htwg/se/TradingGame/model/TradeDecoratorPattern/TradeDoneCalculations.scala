package de.htwg.se.TradingGame.model.TradeDecoratorPattern

import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import de.htwg.se.TradingGame.controller.GameStateManager
import de.htwg.se.TradingGame.model.GetMarketDataComponent.GetMarketDataforTradeDoneCalculations


class TradeDoneCalculations(trade: TradeComponent, gameStateManager: GameStateManager) extends TradeDecorator(trade) {
  val getMarketData = new GetMarketDataforTradeDoneCalculations(trade, gameStateManager)
  var dateTradeTriggered: String = getMarketData.dateTradeTriggered
  var tradeWinOrLose: String = getMarketData.didTradeWinorLoose
  var dateTradeDone: String = getMarketData.dateTradeDone
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
  def this(trade: TradeComponent, dateTradeTriggered: String, tradeWinOrLose: String, dateTradeDone: String, currentprofit: Double, endProfit: Double, gameStateManager: GameStateManager) = {
    this(trade, gameStateManager: GameStateManager) // Call the primary constructor
    this.dateTradeTriggered = dateTradeTriggered
    this.tradeWinOrLose = tradeWinOrLose
    this.dateTradeDone = dateTradeDone
    this.currentprofit = currentprofit
    this.endProfit = endProfit
  }
}