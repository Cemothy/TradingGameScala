package de.htwg.se.TradingGame.model 
import de.htwg.se.TradingGame.model._
import scala.io.Source
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ArrayBuffer
import java.io.File
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._

object GetMarketData {

val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath

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

  if(!isDateAfterLastDateinFile(dateTime, dataFilePath) && !isDateBeforefirstDateinFile(dateTime, dataFilePath)){
    price = source.getLines()
      .collect {
        case line if line.startsWith(dateTime) || LocalDateTime.parse(line.split(",")(0) + "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(dateTime, formatter)) => line.split(",")(ohlc) // Fetching the price
      }
      .toList
      .headOption
      .getOrElse("0.0") // If no matching line found, return 0.0
      source.close()
    }
      if(price.equals("0.0")){
        if(isDateAfterLastDateinFile(dateTime, dataFilePath)){
          price = "date is after last date of file"
        } else if(isDateBeforefirstDateinFile(dateTime, dataFilePath)){
          price = "date is before first date of file"
        }
        } else {
        price
      }
      
      price
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
def isTradeBuyorSell(trade : TradeComponent) : Boolean = {
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


  def dateWhenTradeTriggered(trade: TradeComponent): String = {
    var date: String = "Trade was not triggered"
    val source = Source.fromFile(new java.io.File(GetMarketData.Path).getParent + s"/Symbols/${trade.ticker}.csv")
    if(isTradeBuyorSell(trade)){
    source.getLines()
      .collect {
        case line if LocalDateTime.parse(line.split(",")(0) + "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(trade.datestart, formatter))  && trade.entryTrade > line.split(",")(4).toDouble => line.split(",")(0) + "," + line.split(",")(1) // Fetching the date and time
      }
      .toList
      .headOption
      .getOrElse("Trade was not triggered") // If no matching line found, return 0.0
    }else{
      source.getLines()
      .collect {
        case line if LocalDateTime.parse(line.split(",")(0) + "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(trade.datestart, formatter))  && trade.entryTrade < line.split(",")(3).toDouble => line.split(",")(0) + "," + line.split(",")(1) // Fetching the date and time
      }
      .toList
      .headOption
      .getOrElse("Trade was not triggered") // If no matching line found, return 0.0
    }
  
}

def dateWhenTradehitTakeProfit (trade: TradeComponent): String = {
  var date: String = " Trade did not hit take profit"
  val source = Source.fromFile(new java.io.File(GetMarketData.Path).getParent + s"/Symbols/${trade.ticker}.csv")
  val dateWhenTradeTriggered1 = dateWhenTradeTriggered(trade)
  if(dateWhenTradeTriggered1.equals("Trade was not triggered")){
    date = "Trade was not triggered"
  } else{
  if(isTradeBuyorSell(trade)){
    date = source.getLines()
    .collect {
      case line if LocalDateTime.parse(line.split(",")(0) + "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(dateWhenTradeTriggered1, formatter))  && trade.takeProfitTrade < line.split(",")(3).toDouble => line.split(",")(0) + "," + line.split(",")(1) // Fetching the date and time
    }
    .toList
    .headOption
    .getOrElse("Trade did not hit take profit") // If no matching line found, return 0.0
  }else{
    date = source.getLines()
    .collect {
      case line if LocalDateTime.parse(line.split(",")(0) + "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(dateWhenTradeTriggered1, formatter))  && trade.takeProfitTrade > line.split(",")(4).toDouble => line.split(",")(0) + "," + line.split(",")(1) // Fetching the date and time
    }
    .toList
    .headOption
    .getOrElse("Trade did not hit take profit") // If no matching line found, return 0.0
  }
  }
  date
}

def dateWhenTradehitStopLoss(trade: TradeComponent): String = {
  var date: String = " Trade did not hit stop loss"
  val source = Source.fromFile(new java.io.File(GetMarketData.Path).getParent + s"/Symbols/${trade.ticker}.csv")
  val dateWhenTradeTriggered1 = dateWhenTradeTriggered(trade)
  if(dateWhenTradeTriggered1.equals("Trade was not triggered")){
    date = "Trade was not triggered"
  } else{
  if(isTradeBuyorSell(trade)){
    date = source.getLines()
    .collect {
      case line if LocalDateTime.parse(line.split(",")(0) + "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(dateWhenTradeTriggered1, formatter))  && trade.stopLossTrade > line.split(",")(4).toDouble => line.split(",")(0) + "," + line.split(",")(1) // Fetching the date and time
    }
    .toList
    .headOption
    .getOrElse("Trade did not hit stop loss") // If no matching line found, return 0.0
  }else{
    date = source.getLines()
    .collect {
      case line if LocalDateTime.parse(line.split(",")(0) + "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(dateWhenTradeTriggered1, formatter))  && trade.stopLossTrade < line.split(",")(3).toDouble => line.split(",")(0) + "," + line.split(",")(1) // Fetching the date and time
    }
    .toList
    .headOption
    .getOrElse("Trade did not hit stop loss") // If no matching line found, return 0.0
  }
}
  date
}

def datewhenTradeisdone (trade: TradeComponent): String = {
  val dateWhenTradehitTakeProfit1 = dateWhenTradehitTakeProfit(trade)
  val dateWhenTradehitStopLoss1 = dateWhenTradehitStopLoss(trade)
  
  if (dateWhenTradeTriggered(trade).equals("Trade was not triggered")) {
    "Trade was not triggered"
  } else if (dateWhenTradehitStopLoss1.equals("Trade did not hit stop loss") && dateWhenTradehitTakeProfit1.equals("Trade did not hit take profit")) {
    "Trade did not hit take profit or stop loss"
  } else if (dateWhenTradehitStopLoss1.equals("Trade did not hit stop loss")) {
    dateWhenTradehitTakeProfit1
  } else if (dateWhenTradehitTakeProfit1.equals("Trade did not hit take profit")) {
    dateWhenTradehitStopLoss1
  } else if (LocalDateTime.parse(dateWhenTradehitTakeProfit1, formatter).isBefore(LocalDateTime.parse(dateWhenTradehitStopLoss1, formatter))) {
    dateWhenTradehitTakeProfit1
  } else {
    dateWhenTradehitStopLoss1
  }
}
  

def didTradeWinnorLoose(trade: TradeComponent): String = {
  var result: String = "Trade did not hit take profit or stop loss"
  val dateWhenTradehitTakeProfit1 = dateWhenTradehitTakeProfit(trade)
  val dateWhenTradehitStopLoss1 = dateWhenTradehitStopLoss(trade)
    
    if(dateWhenTradeTriggered(trade).equals("Trade was not triggered")){
      result = "Trade was not triggered"
    } else if(dateWhenTradehitTakeProfit1.equals("Trade did not hit take profit") && !dateWhenTradehitStopLoss1.equals("Trade did not hit stop loss")){
      result = "Trade hit stop loss"
    } else if(dateWhenTradehitStopLoss1.equals("Trade did not hit stop loss") && !dateWhenTradehitTakeProfit1.equals("Trade did not hit take profit")){
      result = "Trade hit take profit"
    }else if(dateWhenTradehitStopLoss1.equals("Trade did not hit stop loss") && dateWhenTradehitTakeProfit1.equals("Trade did not hit take profit")){
      result = "Trade did not hit take profit or stop loss"
    } else if(LocalDateTime.parse(dateWhenTradehitTakeProfit1, formatter).isBefore(LocalDateTime.parse(dateWhenTradehitStopLoss1, formatter))){
      result = "Trade hit take profit"
    } else {
      result = "Trade hit stop loss"
    }

  result

}
  val trades: ArrayBuffer[TradeDoneCalculations] = ArrayBuffer.empty[TradeDoneCalculations]
  var balance: Double = 0.0
def closeProgram: String = {

    println(doneTradeStringwithProfit)
    System.exit(0)
    "should not print"
  }

def doneTradeStringwithProfit: String = {
  var output = ""
  for (trade <- trades) {
    output += "__________________________________________________________\n"
    output += s"Entry Trade: ${trade.trade.entryTrade}  |  "
    output += s"Stop Loss Trade: ${trade.trade.stopLossTrade}  |  "
    output += s"Take Profit Trade: ${trade.trade.takeProfitTrade}  |  "
    output += s"Risk Trade: ${trade.trade.risk}  |  "
    output += s"Date: ${trade.trade.datestart}  |  "
    output += s"Ticker: ${trade.trade.ticker}  |  "
    output += s"Date Trade Triggered: ${trade.dateTradeTriggered}  |  "
    output += s"Date Trade Done: ${trade.dateTradeDone}  |  "
    output += s"Trade Winner or Loser: ${trade.tradeWinOrLose}  |  "
    output += s"Trade Buy or Sell: ${if (trade.trade.takeProfitTrade > trade.trade.stopLossTrade) "Buy" else "Sell"}  |  "
    output += s"Profit: $$${trade.endProfit * balance}\n"
    output += "__________________________________________________________\n"
    this.balance = balance + trade.endProfit * balance
    output += "__________________________________________________________\n"
    output += s"new Balance: $$$balance\n"
    output += "__________________________________________________________\n"
  }
  output += "__________________________________________________________\n"
  output += s"Final Balance: $$$balance\n"
  output += "__________________________________________________________\n"

  output += "Closing the program..."
  output
}







}









