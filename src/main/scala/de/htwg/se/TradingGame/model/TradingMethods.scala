package de.htwg.se.TradingGame.model 
import de.htwg.se.TradingGame.model._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._





object TradingMethods {
  def showCompany(currentTicker: String, date: String, balance: Double, currentPrice: Double): String = {
    val output =
      s"""_____________________________________
         |Currently trading with :
         |Balance: $balance
         |Company: $currentTicker
         |Date: $date
         |Current Value: $$$currentPrice
         |_____________________________________
         |""".stripMargin

    output
  }

  def currentTrade(trade: Trade): String = {
    val output =
      s"""_____________________________________
         |Current Trade:
         |Ticker: ${trade.ticker}
         |Entry: $$${trade.entryTrade}
         |StopLoss: $$${trade.stopLossTrade}
         |TakeProfit: $$${trade.takeProfitTrade}
         |Risk (in percent): ${trade.risk}%
         |""".stripMargin

    output
  }
}