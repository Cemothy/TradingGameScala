package de.htwg.se.TradingGame.model.TradeDecoratorPattern

class TradeWithVolume( 
  trade: TradeComponent,
  val volume: Double 
) extends TradeDecorator(trade) {

}
