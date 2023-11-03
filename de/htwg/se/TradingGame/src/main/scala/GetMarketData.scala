package TradingGame
import scala.io.Source
import TradingMethods._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
object GetMarketData {

val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")

def isDateInFile(dateTime: String, dataFilePath: String): Boolean = {
  var dateInFile: Boolean = false
  val source = Source.fromFile(dataFilePath)
  var File: String = "No Date found"
  //look if date is in file 
  File = source.getLines()
      .collect {
        case line if line.startsWith(dateTime) => line.split(",")(0) + "," + line.split(",")(1) // Fetching the date and time
      }
      .toList
      .headOption
      .getOrElse("No Date found")
      source.close()
      if(File.equals("No Date found")){
        false
      } else {
        true
      }

}

def nextPossibleDateinFile(dateTime: String, dataFilePath: String): String = {
  var nextPossibleDate: String = ""
  val source = Source.fromFile(dataFilePath)
 //read the last line of source and give out the Date and time
  if(isDateInFile(dateTime, dataFilePath)){
    nextPossibleDate = dateTime
  } else {
    nextPossibleDate = source.getLines()
      .collect {
        case line if LocalDateTime.parse(line.split(",")(0) + "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(dateTime, formatter)) => line.split(",")(0) + "," + line.split(",")(1) // Fetching the date and time
      }
      .toList
      .headOption
      .getOrElse("No Date found") // If no matching line found, return 0.0
  }
  source.close()
  nextPossibleDate
}

def getLastDateofFile (dataFilePath: String): String = {
  var lastDate: String = ""
  val source = Source.fromFile(dataFilePath)
 //read the last line of source and give out the Date and time
  
    lastDate = source.getLines()
      .toList
      .lastOption
      .getOrElse("No Date found")
      source.close()

  lastDate.split(",")(0) + "," + lastDate.split(",")(1)

}

def getFirsDateofFile (dataFilePath: String): String = {
  var firstDate: String = ""
  val source = Source.fromFile(dataFilePath)
 //read the last line of source and give out the Date and time
  
    firstDate = source.getLines()
      .toList
      .headOption
      .getOrElse("No Date found")
      source.close()

  firstDate.split(",")(0) + "," + firstDate.split(",")(1)

}
def getPriceForDateTimeDouble(dateTime: String, dataFilePath: String, ohlc: Integer): Double = {
  val priceString = getPriceForDateTimeString(dateTime, dataFilePath, ohlc)

  if(priceString.equals("date is after last date of file") || priceString.equals("date is before first date of file")){
    0.0
  } else { 
    priceString.toDouble
  }
}

def getPriceForDateTimeString(dateTime: String, dataFilePath: String, ohlc: Integer): String = {
  var price: String = "0.0"
  val source = Source.fromFile(dataFilePath)


    price = source.getLines()
      .collect {
        case line if line.startsWith(dateTime) => line.split(",")(ohlc) // Fetching the price
      }
      .toList
      .lastOption
      .getOrElse("0.0") // If no matching line found, return 0.0
      source.close()
      if(price.equals("0.0")){
        if(isDateAfterLastDateinFile(dateTime, dataFilePath)){
          "date is after last date of file"
        } else if(isDateBeforefirstDateinFile(dateTime, dataFilePath)){
          "date is before first date of file"
        } else {
          //if the date is not in the file, get the next possible date in the file
          val nextPossibleDate = nextPossibleDateinFile(dateTime, dataFilePath)
          getPriceForDateTimeString(nextPossibleDate, dataFilePath, 2)
        }
      } else {
        price
      }
      
        
        // val dateTimeR = LocalDateTime.parse(dateTime, formatter).plusMinutes(1)
        // val dateTimeRstring = dateTimeR.format(formatter)
        // price = getPriceForDateTimeString(dateTimeRstring, dataFilePath, 2)
      }

def isDateBeforefirstDateinFile(dateTime: String, dataFilePath: String): Boolean = {
  var dateBeforeFirstDate: Boolean = false

  val firstDate = getFirsDateofFile(dataFilePath)
  val firstDateLocalDateTime = LocalDateTime.parse(firstDate, formatter)
  val dateTimeLocalDateTime = LocalDateTime.parse(dateTime, formatter)

  if(dateTimeLocalDateTime.isBefore(firstDateLocalDateTime)){
    dateBeforeFirstDate = true
  } else {
    dateBeforeFirstDate = false
  }
  dateBeforeFirstDate
}

def isDateAfterLastDateinFile(dateTime: String, dataFilePath: String): Boolean = {
  var dateAfterLastDate: Boolean = false

  val lastDate = getLastDateofFile(dataFilePath)
  val lastDateLocalDateTime = LocalDateTime.parse(lastDate, formatter)
  val dateTimeLocalDateTime = LocalDateTime.parse(dateTime, formatter)

  if(dateTimeLocalDateTime.isAfter(lastDateLocalDateTime)){
    dateAfterLastDate = true
  } else {
    dateAfterLastDate = false
  }
  dateAfterLastDate
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


  def dateWhenTradeTriggered(trade: Trade): String = {
    var date: String = "Trade was not triggered"
    val source = Source.fromFile(s"src/Symbols/${trade.ticker}.csv")
    if(isTradeBuyorSell(trade)){
    source.getLines()
      .collect {
        case line if LocalDateTime.parse(line.split(",")(0) + "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(trade.date, formatter))  && trade.entryTrade > line.split(",")(4).toDouble => line.split(",")(0) + "," + line.split(",")(1) // Fetching the date and time
      }
      .toList
      .headOption
      .getOrElse("Trade was not triggered") // If no matching line found, return 0.0
    }else{
      source.getLines()
      .collect {
        case line if LocalDateTime.parse(line.split(",")(0) + "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(trade.date, formatter))  && trade.entryTrade < line.split(",")(4).toDouble => line.split(",")(0) + "," + line.split(",")(1) // Fetching the date and time
      }
      .toList
      .headOption
      .getOrElse("Trade was not triggered") // If no matching line found, return 0.0
    }
    
}
}













