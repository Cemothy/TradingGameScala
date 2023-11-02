package TradingGame
import scala.io.StdIn._
import java.time.LocalDate
import java.time.format.DateTimeParseException
import TradingMethods._

//testtest
val balance = 100
val yourBalance = s"Your balance is: $$$balance"
val chooseTickertext = "_" * 20 + "\nPlease Enter the Tickersymbol of your choice:\nAvailable Symbols:\nTSLA : Tesla\nAAPL : Apple\nAMZN : Amazon\nMCD  : McDonalds\n\nto Stop : Q\n\n"
val currentTicker = "TSLA"
val currentDate = LocalDate.parse("2023-10-26")
val entryTrade = 210.80
val stopLossTrade = 200.00
val takeProfitTrade = 300.00
val riskTrade = 2
val welcomeText = "Welcome to TradingChampions!\n\nChoose your Starting Balance:\n"


@main def start: Unit = {
  print(welcomeText)
  print(chooseTickertext)
  print(showCompany(currentTicker, currentDate, balance))
  val currentTradeInstance = new Trade(entryTrade, stopLossTrade, takeProfitTrade, riskTrade, currentDate, currentTicker)
  print(currentTrade(currentTradeInstance))
}


