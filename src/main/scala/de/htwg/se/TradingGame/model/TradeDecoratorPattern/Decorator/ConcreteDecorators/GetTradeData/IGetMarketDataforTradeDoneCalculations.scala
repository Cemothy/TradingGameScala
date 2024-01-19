package de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.GetTradeData

trait IGetMarketDataforTradeDoneCalculations {
  var didTradeWinorLoose: String 
  var dateTradeTriggered: String 
  var dateTradeDone: String
  var dateTradehitTakeProfit: String 
  var dateTradehitStopLoss: String 
  def didTradeWinnorLoose: Unit
  def dateWhenTradeTriggered: Unit
  def datewhenTradeisdone: Unit
  def dateWhenTradehitTakeProfit: String
  def dateWhenTradehitStopLoss: String
}