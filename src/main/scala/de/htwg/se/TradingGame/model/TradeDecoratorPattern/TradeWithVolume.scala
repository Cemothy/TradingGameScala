package de.htwg.se.TradingGame.model.TradeDecoratorPattern


class TradeWithVolume( 
  trade: TradeComponent,
  balance: Double,
) extends TradeDecorator(trade) {
  val volume: Double = calculateVolume
  private def calculateVolume: Double = {
    val volume = (balance * trade.risk/100) / (trade.entryTrade - trade.stopLossTrade)
    volume
  }
}
