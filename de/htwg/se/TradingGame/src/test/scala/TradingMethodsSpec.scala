package TradingGame
import org.scalatest._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import java.time.LocalDate
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import TradingMethods._

class TradingChampionsSpec extends AnyWordSpec {

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
      val currentTicker = "TSLA"
      val currentDate = LocalDate.parse("2023-10-26")
      val balance = 100.00
      val expectedOutput =
        s"""_____________________________________
           |Currently trading with :
           |Balance: $balance
           |Company: $currentTicker
           |Date: $currentDate
           |Current Value: $$210.80
           |_____________________________________
           |To invest type: invest
           |To browse type: browse
           |""".stripMargin

      val result = showCompany(currentTicker, currentDate, balance)
      result should equal(expectedOutput)
    }
  }

   "The currentTrade method" should {
    "return a formatted trade information string" in {
      val trade = new Trade(210.80, 200.00, 300.00, 2.0, LocalDate.parse("2023-10-26"), "TSLA")
      val expectedOutput =
        s"""_____________________________________
           |Current Trade:
           |Ticker: TSLA
           |Entry: $$210.8
           |StopLoss: $$200.0
           |TakeProfit: $$300.0
           |Risk (in percent): 2.0%
           |""".stripMargin

      val result = currentTrade(trade)
      result should equal(expectedOutput)
    }
  }
}