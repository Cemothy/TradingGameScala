package de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators

import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.TradeDecorator
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.GetTradeData.GetMarketDataforTradeDonecalculationsimpl._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.GetTradeData
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteFactorieCreators._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.ProfitcalculationStrategyPattern._

class TradeDoneCalculations(trade: TradeComponent, gameStateManager: IGameStateManager) extends TradeDecorator(trade) {
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
  def this(trade: TradeComponent, dateTradeTriggered: String, tradeWinOrLose: String, dateTradeDone: String, currentprofit: Double, endProfit: Double, gameStateManager: IGameStateManager) = {
    this(trade, gameStateManager: IGameStateManager) // Call the primary constructor
    this.dateTradeTriggered = dateTradeTriggered
    this.tradeWinOrLose = tradeWinOrLose
    this.dateTradeDone = dateTradeDone
    this.currentprofit = currentprofit
    this.endProfit = endProfit
  }
}