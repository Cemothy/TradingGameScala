package de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator

import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent

class Trade(
  val entryTrade: Double,
  val stopLossTrade: Double,
  val takeProfitTrade: Double,
  val risk: Double,
  val datestart: String,
  val ticker: String
) extends TradeComponent