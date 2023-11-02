package TradingGame
import scala.io.Source

object GetMarketData {

def getPriceForDateTime(dateTime: String, dataFilePath: String): Double = {
  var price: Double = 0.0
  val source = Source.fromFile(dataFilePath)

  try {
    price = source.getLines()
      .collect {
        case line if line.startsWith(dateTime) => line.split(",")(5).toDouble // Fetching the price
      }
      .toList
      .lastOption
      .getOrElse(0.0) // If no matching line found, return 0.0
  } catch {
    case e: Exception =>
      println("An error occurred: " + e.getMessage)
  } finally {
    source.close()
  }

  price
}



}