package de.htwg.se.TradingGame.model.TradeDecoratorPattern

import de.htwg.se.TradingGame.model.GetMarketData

class TradeisBuy(trade: TradeComponent) extends TradeDecorator(trade) {
  val getMarketData = new GetMarketData
  var isTradeBuy: String = ""

  if (getMarketData.isTradeBuyorSell(trade)) {
    isTradeBuy = "Buy"
  } else {
    isTradeBuy = "Sell"
  }
}