package de.htwg.se.TradingGame.model.TradeDecoratorPattern

import de.htwg.se.TradingGame.model.GetMarketData.isTradeBuyorSell

class TradeisBuy(trade: TradeComponent) extends TradeDecorator(trade) {
  var isTradeBuy: String = ""

  if (isTradeBuyorSell(trade)) {
    isTradeBuy = "Buy"
  } else {
    isTradeBuy = "Sell"
  }
}