package TradingGame
import scala.io.StdIn
import scala.collection.mutable.ArrayBuffer
object MainClass {

  

val trades: ArrayBuffer[TradeDoneCalculations] = ArrayBuffer.empty[TradeDoneCalculations]
var balance: Double = 10000.0

  def closeProgram: String = {
  for (trade <- trades) {
    println("__________________________________________________________")
    print(s"Entry Trade: ${trade.trade.entryTrade}  |  ")
    print(s"Stop Loss Trade: ${trade.trade.stopLossTrade}  |  ")
    print(s"Take Profit Trade: ${trade.trade.takeProfitTrade}  |  ")
    print(s"Risk Trade: ${trade.trade.riskTrade}  |  ")
    print(s"Date: ${trade.trade.date}  |  ")
    print(s"Ticker: ${trade.trade.ticker}  |  ")
    print(s"Date Trade Triggered: ${trade.dateTradeTiggered}  |  ")
    print(s"Date Trade Done: ${trade.dateTradeDone}  |  ")
    print(s"Trade Winner or Loser: ${trade.TradeWinnorLoose}  |  ")
    print(s"Trade Buy or Sell: ${if (trade.tradeBuyorSell) "Buy" else "Sell"}  |  ")
    println(s"Profit: $$${GetMarketData.calculateTradeProfit(trade, balance)}")
    print("__________________________________________________________")
    this.balance = balance + GetMarketData.calculateTradeProfit(trade, balance)
    println("__________________________________________________________")
    print(s"new Balance: $$$balance\n")
    println("__________________________________________________________")
  }
  println("__________________________________________________________")
  print(s"Final Balance: $$$balance\n")
  println("__________________________________________________________")

    println("Closing the program...")
    System.exit(0)
    "should not print"
  }

  @main def main():Unit =
    val tui = new TUI
    while(true){
      tui.readLine()
    }
}