package de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators

import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern.ConcreteFactorieCreators._
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.GetTradeData
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.GetTradeData.GetMarketDataforTradeDonecalculationsimpl._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.TradeDecorator
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.ProfitcalculationStrategyPattern._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
  
  // def calculateCurrentProfit(trade: TradeDoneCalculations, volume: Double, currentPrice: Double, currentDate: String): Unit= {
  //   trade.currentprofit = 0.0
  //   if(trade.dateTradeTriggered.equals("Trade was not triggered")){

  //   } else {
  //   val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
  //   val parsedCurrentDate = LocalDateTime.parse(currentDate, formatter)
  //   val parsedDateTradeTriggered = LocalDateTime.parse(trade.dateTradeTriggered, formatter)
  //   val parsedDateTradeDone = LocalDateTime.parse(trade.dateTradeDone, formatter)

    
  //   if(parsedCurrentDate.isAfter(parsedDateTradeTriggered)) {
  //     if(parsedCurrentDate.isBefore(parsedDateTradeDone)) {
  //       trade.currentprofit = BigDecimal((currentPrice - trade.entryTrade) * volume).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  //     } else {
  //       if(trade.tradeWinOrLose == "Trade hit take profit") {
  //         trade.currentprofit = BigDecimal(scala.math.abs((trade.takeProfitTrade - trade.entryTrade) * volume)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  //       }else if(trade.tradeWinOrLose == "Trade hit stop loss"){
  //         trade.currentprofit = BigDecimal(scala.math.abs((trade.stopLossTrade - trade.entryTrade) * volume) * -1).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  //       } else {
  //         trade.currentprofit = 0.0
  //       }
  //     }
  //     }
  //   }
  // }

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