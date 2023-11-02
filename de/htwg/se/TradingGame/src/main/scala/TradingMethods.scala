package TradingGame
import java.time.LocalDate

class Trade(
  val entryTrade: Double,
  val stopLossTrade: Double,
  val takeProfitTrade: Double,
  val riskTrade: Double,
  val date: String,
  val ticker: String
)

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
         |Risk (in percent): ${trade.riskTrade}%
         |""".stripMargin

    output
  }
}