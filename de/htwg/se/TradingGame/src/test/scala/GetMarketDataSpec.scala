package TradingGame
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.io.{File, PrintWriter}
import GetMarketData._

class GetMarketDataSpec extends AnyWordSpec with Matchers {

"getLastDateofFile" should{
  "return the last date of the file" in {
    val testFilePath = "src/Symbols/EURUSD.csv"
    val result = getLastDateofFile(testFilePath)
    result shouldEqual "2023.11.02,21:32"
  }
}

"getFirsDateofFile" should{
  "return the first date of the file" in {
    val testFilePath = "src/Symbols/EURUSD.csv"
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
   
    val testFilePath = "src/Symbols/EURUSD.csv"
    val result = getPriceForDateTimeDouble("2023.03.14,15:20", testFilePath, 5)
    result shouldEqual 1.07345

  }
  "return 0.0 when the date is after the last date of the file" in {
    val testFilePath = "src/Symbols/EURUSD.csv"
    val result = getPriceForDateTimeDouble("2023.12.03,15:20", testFilePath, 5)
    result shouldEqual 0.0
  }
  "return 0.0 when the date is before the first date of the file" in {
    val testFilePath = "src/Symbols/EURUSD.csv"
    val result = getPriceForDateTimeDouble("2022.12.03,15:20", testFilePath, 5)
    result shouldEqual 0.0
  }
}

"isDateBeforefirstDateinFile" should {
  "return true if the date is before the first date in the file" in {
    val testFilePath = "src/Symbols/EURUSD.csv"
    val result = isDateBeforefirstDateinFile("2022.12.03,15:20", testFilePath)
    result shouldEqual true
  }
  "return false if the date is not before the first date in the file" in {
    val testFilePath = "src/Symbols/EURUSD.csv"
    val result = isDateBeforefirstDateinFile("2023.10.22,20:12", testFilePath)
    result shouldEqual false
  }
}

"isDateAfterLastDateinFile" should {
  "return true if the date is after the last date in the file" in {
    val testFilePath = "src/Symbols/EURUSD.csv"
    val result = isDateAfterLastDateinFile("2023.12.03,15:20", testFilePath)
    result shouldEqual true
  }
  "return false if the date is not after the last date in the file" in {
    val testFilePath = "src/Symbols/EURUSD.csv"
    val result = isDateAfterLastDateinFile("2023.10.22,20:12", testFilePath)
    result shouldEqual false
  }
}

" isDateInFile" should {
  "return true if the date is in the file" in {
    val testFilePath = "src/Symbols/EURUSD.csv"
    val result = isDateInFile("2023.02.17,11:52", testFilePath)
    result shouldEqual true
  }
  "return false if the date is not in the file" in {
    val testFilePath = "src/Symbols/EURUSD.csv"
    val result = isDateInFile("2023.10.29,00:01", testFilePath)
    result shouldEqual false
  }
}

"nextPossibleDateinFile" should {
  "return the next possible date in the file" in {
    val testFilePath = "src/Symbols/EURUSD.csv"
    val result = nextPossibleDateinFile("2023.10.22,20:12", testFilePath)
    result shouldEqual "2023.10.23,00:00"
  }
}


  "getPriceForDateTimeString" should {
    "return the correct price for a given date and time" in {
     
      val testFilePath = "src/Symbols/EURUSD.csv"


      // Test scenario
      val result = getPriceForDateTimeString("2023.03.14,15:20", testFilePath, 5)

      // Assertion
      result shouldEqual "1.07345"

     
      
    }

    "print date is afer last date of file when the date is after the last date of the file" in {
      val testFilePath = "src/Symbols/EURUSD.csv"
      val result = getPriceForDateTimeString("2023.12.03,15:20", testFilePath, 5)
      result shouldEqual "date is after last date of file"
    }

    "print date is before first date of file when the date is before the first date of the file" in {
      val testFilePath = "src/Symbols/EURUSD.csv"
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
    val testFilePath = "testFileEmpty.csv"
    val writer = new PrintWriter(new File(testFilePath))
  
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
    val testFilePath = "src/Symbols/EURUSD.csv"
    val result = dateWhenTradehitTakeProfit(Trade(1.09999 , 1.001, 1.10044, 2.0, "2023.08.11,11:53", "EURUSD"))
    result shouldEqual "2023.08.11,15:09"
  }
  "return Target not hit when no matching date and time are found for a buy" in {
    val testFilePath = "testFileEmpty.csv"
    val writer = new PrintWriter(new File(testFilePath))
  
    // Test scenario
    val result = dateWhenTradehitTakeProfit(Trade(1.09999 , 0.3, 3.0, 2.0, "2023.08.11,11:53", "EURUSD"))

    // Assertio
    result shouldEqual "Trade did not hit take profit buy"
}
  "return the correct date when the trade hit the take profit after the trade was triggered for a sell" in {
    val testFilePath = "src/Symbols/EURUSD.csv"
    val result = dateWhenTradehitTakeProfit(Trade(1.1004 , 2.0, 1.09999, 2.0, "2023.08.11,11:53", "EURUSD"))
    result shouldEqual "2023.08.11,15:14"
  }


}

"dateWhenTradehitStopLoss" should {
  "return the correct date when the trade hit the stop loss after the trade was triggered for a buy" in {
    val testFilePath = "src/Symbols/EURUSD.csv"
    val result = dateWhenTradehitStopLoss(Trade(1.09999 , 1.09950, 1.10044, 2.0, "2023.08.11,11:53", "EURUSD"))
    result shouldEqual "2023.08.11,12:15"
  }
  "return Target not hit when no matching date and time are found for a buy" in {
    val testFilePath = "testFileEmpty.csv"
    val writer = new PrintWriter(new File(testFilePath))
  
    // Test scenario
    val result = dateWhenTradehitStopLoss(Trade(1.09999 , 0.3, 3.0, 2.0, "2023.08.11,11:53", "EURUSD"))

    // Assertio
    result shouldEqual "Trade did not hit stop loss buy"
}
  "return the correct date when the trade hit the stop loss after the trade was triggered for a sell" in {
    val testFilePath = "src/Symbols/EURUSD.csv"
    val result = dateWhenTradehitStopLoss(Trade(1.1004 , 1.10048, 1.09999, 2.0, "2023.08.11,11:53", "EURUSD"))
    result shouldEqual "2023.08.11,15:26"
  }
}


}