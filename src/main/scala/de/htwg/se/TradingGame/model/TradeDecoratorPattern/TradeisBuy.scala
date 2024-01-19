package de.htwg.se.TradingGame.model.TradeDecoratorPattern


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