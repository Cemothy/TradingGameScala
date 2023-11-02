package TradingGame
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.io.{File, PrintWriter}
import GetMarketData._

class GetMarketDataSpec extends AnyWordSpec with Matchers {

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


  "getPriceForDateTime" should {
    "return the correct price for a given date and time" in {
      // Create a temporary test file for the test
      val testFilePath = "testFile.csv"
      val writer = new PrintWriter(new File(testFilePath))
      writer.write(
        """2022.09.08,15:00,1.00198,1.00218,1.00182,1.00202,155
          |2022.09.08,15:11,1.00176,1.00208,1.00176,1.00208,132
          |2022.09.08,15:34,0.99954,1.00015,0.99954,1.00002,308""".stripMargin)
      writer.close()

      // Test scenario
      val result = getPriceForDateTime("2022.09.08,15:34", testFilePath)

      // Assertion
      result shouldEqual 1.00002

      // Clean up: Delete the temporary file
      val file = new File(testFilePath)
      if (file.exists()) file.delete()
    }

    "return 0.0 when no matching date and time are found" in {
      val testFilePath = "testFileEmpty.csv"
      val writer = new PrintWriter(new File(testFilePath))
      writer.write(
        """2022.09.08,15:15,1.00096,1.00300,0.99998,1.00005,435
        |2022.09.08,15:28,1.00074,1.00103,1.00049,1.00059,178""".stripMargin)
      writer.close()

      // Test scenario
      val result = getPriceForDateTime("2022.08.17,23:51", testFilePath)

      // Assertion
      result shouldEqual 0.0

      // Clean up: Delete the temporary file
      val file = new File(testFilePath)
      if (file.exists()) file.delete()
    }
  }

"dateWhenTradeTriggered " should {
  "return the correct date when the trade was triggered" in {
    // Create a temporary test file for the test
    val testFilePath = "testFile.csv"
    val writer = new PrintWriter(new File(testFilePath))
    writer.write(
      """2022.09.08,15:00,1.00198,1.00218,1.00182,1.00202,155
        |2022.09.08,15:11,1.00176,1.00208,1.00176,1.00208,132
        |2022.09.08,15:34,0.99954,1.00015,0.99954,1.00002,308""".stripMargin)
    writer.close()

    // Test scenario
    val result = dateWhenTradeTriggered(Trade(1.00181 , 1.00015, 1.00250, 2.0, "2022.09.08,15:00", "EURUSD"))

    // Assertion
    result shouldEqual "2022.09.08:15:11"

    // Clean up: Delete the temporary file
    val file = new File(testFilePath)
    if (file.exists()) file.delete()
  }

  "return Trade was not triggered when no matching date and time are found" in {
    val testFilePath = "testFileEmpty.csv"
    val writer = new PrintWriter(new File(testFilePath))
    writer.write(
      """2022.09.08,15:15,1.00096,1.00300,0.99998,1.00005,435
        |2022.09.08,15:28,1.00074,1.00103,1.00049,1.00059,178""".stripMargin)
    writer.close()

    // Test scenario
    val result = dateWhenTradeTriggered(Trade(0.995 , 0.8, 1.00250, 2.0, "2022.08.17,23:51", "EURUSD"))

    // Assertion
    result shouldEqual "Trade was not triggered"

    // Clean up: Delete the temporary file
    val file = new File(testFilePath)
    if (file.exists()) file.delete()
  
}
}
}