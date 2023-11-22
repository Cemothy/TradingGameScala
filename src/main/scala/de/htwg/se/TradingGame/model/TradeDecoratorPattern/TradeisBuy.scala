package de.htwg.se.TradingGame.model.TradeDecoratorPattern

class TradeisBuy( 
  trade: TradeComponent,
  val isBuy: Boolean 
) extends TradeDecorator(trade) {

}