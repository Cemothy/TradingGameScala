package de.htwg.se.TradingGame.model

class TradeisBuy( 
  trade: TradeComponent,
  val isBuy: Boolean 
) extends TradeDecorator(trade) {
    
}