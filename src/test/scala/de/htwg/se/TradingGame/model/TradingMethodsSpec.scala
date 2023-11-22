package de.htwg.se.TradingGame.model
import org.scalatest._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import java.time.LocalDate
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import TradingMethods._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Trade

class TradingChampionsSpec extends AnyWordSpec {

val balance = 100
val yourBalance = s"Your balance is: $$$balance"
val chooseTickertext = "_" * 20 + "\nPlease Enter the Tickersymbol of your choice:\nAvailable Symbols:\nTSLA : Tesla\nAAPL : Apple\nAMZN : Amazon\nMCD  : McDonalds\n\nto Stop : Q\n\n"

"yourBalance" should {
  "have the String Your balance is: $100" in {
    yourBalance should be ("Your balance is: $100")
  }
}

"chooseTickertext" should {
  "have the String _" * 20 + "\nPlease Enter the Tickersymbol of your choice:\nAvailable Symbols:\nTSLA : Tesla\nAAPL : Apple\nAMZN : Amazon\nMCD  : McDonalds\n\nto Stop : Q\n\n" in {
    chooseTickertext should be("_" * 20 + "\nPlease Enter the Tickersymbol of your choice:\nAvailable Symbols:\nTSLA : Tesla\nAAPL : Apple\nAMZN : Amazon\nMCD  : McDonalds\n\nto Stop : Q\n\n")
  }
}

"The showCompany method" should {
    "return a formatted company information string" in {
      val currentTicker = "EURUSD"
      val currentDate = "2023.08.17,23:51"
      val balance = 100.00
      val price = 210.80
      val result = showCompany(currentTicker, currentDate, balance, price)
      result should include(s"Balance: $balance")
      result should include(s"Company: $currentTicker")
      result should include(s"Date: $currentDate")
      result should include(s"Current Value: $$$price")
    }
  }

   "The currentTrade method" should {
    "return a formatted trade information string" in {
      val trade = new Trade(210.80, 200.00, 300.00, 2.0, "2023.08.17,23:51", "EURUSD")
    

      val result = currentTrade(trade)
      result should include("Ticker: EURUSD")
      result should include("Entry: $210.8")
      result should include("StopLoss: $200.0")
      result should include("TakeProfit: $300.0")
      result should include("Risk (in percent): 2.0%")
    }
  }
}