package de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators

import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.TradeDecorator



class TradeisBuy(trade: TradeComponent) extends TradeDecorator(trade) {
  var isTradeBuy: String = ""

  if (if(trade.takeProfitTrade > trade.stopLossTrade){
      true
    } else {
      false
    }) {
    isTradeBuy = "Buy"
  } else {
    isTradeBuy = "Sell"
  }
}