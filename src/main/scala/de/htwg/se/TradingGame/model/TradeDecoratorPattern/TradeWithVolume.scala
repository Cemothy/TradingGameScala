package de.htwg.se.TradingGame.model.TradeDecoratorPattern

import de.htwg.se.TradingGame.model.GetMarketData.balance

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
