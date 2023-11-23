package de.htwg.se.TradingGame.model
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.io.{File, PrintWriter}
import GetMarketData._
import scala.collection.mutable.ArrayBuffer
import de.htwg.se.TradingGame.model._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import de.htwg.se.TradingGame.model.ProfitcalculationStrategyPattern._
class GetMarketDataSpec extends AnyWordSpec with Matchers {



val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/GetMarketDataSpec.scala").getAbsolutePath
val testFilePath = new java.io.File(GetMarketData.Path).getParent + "/Symbols/EURUSD.csv"



"getLastDateofFile" should{
  "return the last date of the file" in {
    val result = getLastDateofFile(testFilePath)
    result shouldEqual "2023.11.02,21:32"
  }
}

"getFirsDateofFile" should{
  "return the first date of the file" in {
    val result = getFirsDateofFile(testFilePath)
    result shouldEqual "2023.01.02,07:02"
  }
}



"isTradeBuyorSell" should {
  "return true if the trade is a buy" in {
    val trade = Trade(1.00181 , 1.00015, 1.00250, 2.0, "2022.09.08,15:00", "EURUSD")
    val result = isTradeBuyorSell(trade)
    result shouldEqual true
  }
}

  "return false if the trade is a sell" in {
    val trade = Trade(1.00181 , 1.00250, 1.00015, 2.0, "2022.09.08,15:00", "EURUSD")
    val result = isTradeBuyorSell(trade)
    result shouldEqual false
  }

"getPriceForDateTimeDouble" should {
  "return the correct price for a given date and time" in {
    val result = getPriceForDateTimeDouble("2023.03.14,15:20", testFilePath, 5)
    result shouldEqual 1.07345

  }
  "return 0.0 when the date is after the last date of the file" in {
    val result = getPriceForDateTimeDouble("2023.12.03,15:20", testFilePath, 5)
    result shouldEqual 0.0
  }
  "return 0.0 when the date is before the first date of the file" in {
    val result = getPriceForDateTimeDouble("2022.12.03,15:20", testFilePath, 5)
    result shouldEqual 0.0
  }
}

"isDateBeforefirstDateinFile" should {
  "return true if the date is before the first date in the file" in {
    val result = isDateBeforefirstDateinFile("2022.12.03,15:20", testFilePath)
    result shouldEqual true
  }
  "return false if the date is not before the first date in the file" in {
    val result = isDateBeforefirstDateinFile("2023.10.22,20:12", testFilePath)
    result shouldEqual false
  }
}

"isDateAfterLastDateinFile" should {
  "return true if the date is after the last date in the file" in {
    val result = isDateAfterLastDateinFile("2023.12.03,15:20", testFilePath)
    result shouldEqual true
  }
  "return false if the date is not after the last date in the file" in {
    val result = isDateAfterLastDateinFile("2023.10.22,20:12", testFilePath)
    result shouldEqual false
  }
}

"isDateInFile" should {
  "return true if the date is in the file" in {
    val result = isDateInFile("2023.02.17,11:52", testFilePath)
    result shouldEqual true
  }
  "return false if the date is not in the file" in {
    val result = isDateInFile("2023.10.29,00:01", testFilePath)
    result shouldEqual false
  }
  
}

"doneTradeStringwithProfit" should {
  "return the correct string when the trade hit take profit" in {
    val TradeDoneCalculationsstore = TradeDoneCalculations(TradeisBuy(Trade(1.1 , 1.0, 1.2, 2.0, "2023.08.11,11:53", "EURUSD")))
    val TradeDoneCalculationsstore2 = TradeDoneCalculations(TradeisBuy(Trade(1.1 , 1.0, 1.2, 2.0, "2023.08.11,11:53", "EURUSD")))
    GetMarketData.trades.addOne(TradeDoneCalculationsstore)
    GetMarketData.trades.addOne(TradeDoneCalculationsstore2)
    GetMarketData.balance = 1000.0
    val result = doneTradeStringwithProfit
    result should include("Entry Trade: 1.1")
    result should include("Stop Loss Trade: 1.0")
    result should include("Take Profit Trade: 1.2")
    result should include("Risk Trade: 2.0")
    result should include("Date: 2023.08.11,11:53")
    result should include("Ticker: EURUSD")
    result should include("Date Trade Triggered: 2023.08.11,11:55")
    result should include("Date Trade Done: 2023.08.17,23:53")
    result should include("Profit: $-10.0")
    result should include("Balance: $990.0")
    result should include("Trade Buy or Sell: Buy")
  }

}



"nextPossibleDateinFile" should {
  "return the next possible date in the file" in {
    val result = nextPossibleDateinFile("2023.10.22,20:12", testFilePath)
    result shouldEqual "2023.10.23,00:00"
  }
  "return the given date if it is already in the file" in {
    val result = nextPossibleDateinFile("2023.03.14,15:20", testFilePath)
    result shouldEqual "2023.03.14,15:20"
  }
}



  "getPriceForDateTimeString" should {
    "return the correct price for a given date and time" in {
     
      // Test scenario
      val result = getPriceForDateTimeString("2023.03.14,15:20", testFilePath, 5)

      // Assertion
      result shouldEqual "1.07345"

     
      
    }

    "print date is afer last date of file when the date is after the last date of the file" in {
      val result = getPriceForDateTimeString("2023.12.03,15:20", testFilePath, 5)
      result shouldEqual "date is after last date of file"
    }

    "print date is before first date of file when the date is before the first date of the file" in {
      val result = getPriceForDateTimeString("2022.12.03,15:20", testFilePath, 5)
      result shouldEqual "date is before first date of file"
    }
    
}







"dateWhenTradeTriggered " should {
  "return the correct date when the trade was triggered for a buy" in {

 
    // Test scenario
    val result = dateWhenTradeTriggered(Trade(1.09999 , 1.0650, 2.0, 2.0, "2023.08.11,11:53", "EURUSD"))

    // Assertion
    result shouldEqual "2023.08.11,11:55"
  }

  "return Trade was not triggered when no matching date and time are found for a buy" in {
  
    // Test scenario
    val result = dateWhenTradeTriggered(Trade(0.05 , 0.3, 1.00250, 2.0, "2023.08.17,23:51", "EURUSD"))

    // Assertio
    result shouldEqual "Trade was not triggered"
}
  "return the correct date when the trade was triggered for a sell" in {


  // Test scenario
  val result = dateWhenTradeTriggered(Trade(1.1004 , 2.0, 1.0, 2.0, "2023.08.11,11:53", "EURUSD"))

  // Assertion
  result shouldEqual "2023.08.11,15:09"
}
  //test for dateWhenTradeTriggered stream
  
}

"dateWhenTradehitTakeProfit" should {
  "return the correct date when the trade hit the take profit after the trade was triggered for a buy" in {
    val result = dateWhenTradehitTakeProfit(Trade(1.09999 , 1.001, 1.10044, 2.0, "2023.08.11,11:53", "EURUSD"))
    result shouldEqual "2023.08.11,15:09"
  }
  "return Target not hit when no matching date and time are found for a buy" in {
  
    // Test scenario
    val result = dateWhenTradehitTakeProfit(Trade(1.09999 , 0.3, 3.0, 2.0, "2023.08.11,11:53", "EURUSD"))

    // Assertio
    result shouldEqual "Trade did not hit take profit"
}
  "return the correct date when the trade hit the take profit after the trade was triggered for a sell" in {
    val result = dateWhenTradehitTakeProfit(Trade(1.1004 , 2.0, 1.09999, 2.0, "2023.08.11,11:53", "EURUSD"))
    result shouldEqual "2023.08.11,15:14"
  }


}

"dateWhenTradehitStopLoss" should {
  "return the correct date when the trade hit the stop loss after the trade was triggered for a buy" in {
    val result = dateWhenTradehitStopLoss(Trade(1.09999 , 1.09950, 1.10044, 2.0, "2023.08.11,11:53", "EURUSD"))
    result shouldEqual "2023.08.11,12:15"
  }
  "return Target not hit when no matching date and time are found for a buy" in {

  
    // Test scenario
    val result = dateWhenTradehitStopLoss(Trade(1.09999 , 0.3, 3.0, 2.0, "2023.08.11,11:53", "EURUSD"))

    // Assertio
    result shouldEqual "Trade did not hit stop loss"
}
  "return the correct date when the trade hit the stop loss after the trade was triggered for a sell" in {
    val result = dateWhenTradehitStopLoss(Trade(1.1004 , 1.10048, 1.09999, 2.0, "2023.08.11,11:53", "EURUSD"))
    result shouldEqual "2023.08.11,15:26"
  }
}




"didTradeWinnorLoose" should {
  "return Trade hit stop loss when stop loss is hit before take profit" in {
    val trade = Trade(1.09999 , 1.09909, 1.10044, 2.0, "2023.08.11,11:53", "EURUSD")
    val result = didTradeWinnorLoose(trade)
    result shouldEqual "Trade hit stop loss"
  }
  "return Trade hit take profit when take profit is hit before stop loss" in {
    val trade = Trade(1.09999 , 1.09, 1.10044, 2.0, "2023.08.11,11:53", "EURUSD")
    val result = didTradeWinnorLoose(trade)
    result shouldEqual "Trade hit take profit"
  }
  "return Trade did not hit take profit or stop loss when neither is hit" in {
    val trade = Trade(1.09999 , 1.0, 2.0, 2.0, "2023.08.11,11:53", "EURUSD")
    val result = didTradeWinnorLoose(trade)
    result shouldEqual "Trade did not hit take profit or stop loss"
  }

  "return Trade was not triggered when the trade was not triggered" in {
    val trade = Trade(1.00 , 1.0, 2.0, 2.0, "2023.08.11,11:53", "EURUSD")
    val result = didTradeWinnorLoose(trade)
    result shouldEqual "Trade was not triggered"
  }
  "return Trade hit take Profit and Stop Loss was never triggered when take profit is hit before stop loss and stop loss is never hit" in {
    val trade = Trade(1.09999 , 1.000, 1.10044, 2.0, "2023.08.11,11:53", "EURUSD")
    val result = didTradeWinnorLoose(trade)
    result shouldEqual "Trade hit take profit"
  }
  "return Trade hit stop loss and take profit was never triggered when stop loss is hit before take profit and take profit is never hit" in {
    val trade = Trade(1.09999 , 1.09909, 2.0, 2.0, "2023.08.11,11:53", "EURUSD")
    val result = didTradeWinnorLoose(trade)
    result shouldEqual "Trade hit stop loss"
  }

}




  
}
