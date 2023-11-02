package TradingGame
import scala.io.Source
import TradingMethods._
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
      if (price.equals(0.0)){
        println("Date not found")
      }
  } catch {
    case e: Exception =>
      println("An error occurred: " + e.getMessage)
  } finally {
    source.close()
  }

  price
}

//isTradeBuyorSell: true = buy, false = sell
def isTradeBuyorSell(trade : Trade) : Boolean = {
  if(trade.takeProfitTrade > trade.stopLossTrade){
    true
  } else {
    false
  }
  }

  
  //dateWhenTradeTriggered: returns the date when the trade was triggered
  //dateWhenTradeTriggered gets the Trade as an imput and goes through the data with a stream to check when the trade was triggered
  //if the trade was triggered it returns the date when the trade was triggered
  //if the trade was not triggered it returns "Trade was not triggered"
  //the data is stored in a file like this: 2020.01.01,00:00,1.12345,1.12345,1.12345,1.12345,0 its Date,Time,Open,High,Low,Close,Volume
  //get the file into a stream and check if the trade was triggered
  //* @param trade: Trade
  //* @return String
  def dateWhenTradeTriggered(trade : Trade) : String = {
    var dateWhenTradeTriggered = ""
    val source = Source.fromFile(s"src/Symbols/${trade.ticker}.csv")
    //put source into a stream starting from trade.date and look if trade is a buy that Low is lower or equal to trade.entryTrade or if trade is a sell that High is higher or equal to trade.entryTrade
  
      dateWhenTradeTriggered = source.getLines()
        .collect {
          case line if line.startsWith(trade.date) && isTradeBuyorSell(trade) && line.split(",")(4).toDouble <= trade.entryTrade => line.split(",")(0) // Fetching the date when the trade was triggered
          case line if line.startsWith(trade.date) && !isTradeBuyorSell(trade) && line.split(",")(3).toDouble >= trade.entryTrade => line.split(",")(0) // Fetching the date when the trade was triggered
        }
        .toList
        .lastOption
        .getOrElse("Trade was not triggered") // If no matching line found, return "Trade was not triggered"
        source.close()
        dateWhenTradeTriggered
    }

    
  }
  













