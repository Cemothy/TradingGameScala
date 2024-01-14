package de.htwg.se.TradingGame.model.GetMarketDataComponent 
import com.google.inject.Inject
import de.htwg.se.TradingGame.model.DataSave.TradeData
import de.htwg.se.TradingGame.model.DataSave.TradeDataclass
import de.htwg.se.TradingGame.model.FileIO.TradeDataFileIO
import de.htwg.se.TradingGame.model.FileIO.TradeDataXMLFileIO
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import de.htwg.se.TradingGame.model._

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import de.htwg.se.TradingGame.model.DataSave.TradeData.startDate
import de.htwg.se.TradingGame.model.DataSave.TradeData.endDate
import java.time.ZoneId

object GetMarketData{

val url = "jdbc:sqlite:src/main/scala/de/htwg/se/TradingGame/Database/litedbCandleSticks.db"
val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
val formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
val outputFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
def getDatesForMarktnames(marktnames: List[String]): Map[String, (String, String)] = {
  marktnames.map { marktname =>
    val firstDate = LocalDateTime.parse(getFirstDateofFile(marktname), outputFormatter2)
    val lastDate = LocalDateTime.parse(getLastDateofFile(marktname), outputFormatter2)
    val firstDateoutput = firstDate.format(formatter)
    val lastDateoutput = lastDate.format(formatter)
    startDate = firstDate.atZone(ZoneId.systemDefault()).toEpochSecond()
    endDate = lastDate.atZone(ZoneId.systemDefault()).toEpochSecond()
    marktname -> (firstDateoutput, lastDateoutput)
  }.toMap
}

def isDateInFile(dateTime: String): Boolean = {
  var conn: Connection = DriverManager.getConnection(url)

  // Prepare the SQL statement
  val sql = "SELECT COUNT(*) FROM Candlestick WHERE strftime('%Y.%m.%d,%H:%M', Zeitstempel) = ? AND TimeframeID = 1"
  val pstmt = conn.prepareStatement(sql)
  pstmt.setString(1, dateTime)

  // Execute the query and get the result
  val rs: ResultSet = pstmt.executeQuery()
  rs.next()
  val count = rs.getInt(1)

  // Close the connection
  rs.close()
  pstmt.close()
  conn.close()

  // Return true if the count is greater than 0, false otherwise
  count > 0
}

def nextPossibleDateinFile(dateTime: String): String = {
  var conn: Connection = DriverManager.getConnection(url)

  // Prepare the SQL statement
  val sql = "SELECT MIN(Zeitstempel) FROM Candlestick WHERE Zeitstempel > datetime(strftime('%Y-%m-%d %H:%M', ?)) AND TimeframeID = 1"
  val pstmt = conn.prepareStatement(sql)
  pstmt.setString(1, dateTime)

  // Execute the query and get the result
  val rs: ResultSet = pstmt.executeQuery()
  rs.next()
  val nextPossibleDate = rs.getTimestamp(1)

  // Close the connection
  rs.close()
  pstmt.close()
  conn.close()

  // Return the next possible date, or "No Date found" if there's no such date
  if (nextPossibleDate != null) nextPossibleDate.toString else "No Date found"
}


def getPairNames(connectionString: String): List[String] = {
  var conn: Connection = null
  var rs: ResultSet = null
  try {
    conn = DriverManager.getConnection(connectionString)
    val statement = conn.createStatement()
    rs = statement.executeQuery("SELECT Marktname FROM Markt")

    var pairNames = List[String]()
    while (rs.next()) {
      pairNames = rs.getString("Marktname") :: pairNames
    }
    pairNames
  } catch {
    case e: Exception =>
      e.printStackTrace()
      List()
  } finally {
    if (rs != null) {
      try {
        rs.close()
      } catch {
        case e: Exception =>
          e.printStackTrace()
      }
    }
    if (conn != null) {
      try {
        conn.close()
      } catch {
        case e: Exception =>
          e.printStackTrace()
      }
    }
  }
}
def getLastDateofFile (): String = {

  var conn: Connection = DriverManager.getConnection(url)

  // Prepare the SQL statement
  val sql = "SELECT MAX(Zeitstempel) FROM Candlestick WHERE TimeframeID = 1"
  val pstmt = conn.prepareStatement(sql)

  // Execute the query and get the result
  val rs: ResultSet = pstmt.executeQuery()
  rs.next()
  val lastDate = rs.getTimestamp(1)

  // Close the connection
  rs.close()
  pstmt.close()
  conn.close()

  // Return the last date, or "No Date found" if there's no such date
  if (lastDate != null) lastDate.toString else "No Date found"
}

def getLastDateofFile(marktname: String): String = {
  var conn: Connection = DriverManager.getConnection(url)

  // Prepare the SQL statement
  val sql = "SELECT MAX(Candlestick.Zeitstempel) FROM Candlestick JOIN Markt ON Candlestick.MarktID = Markt.MarktID WHERE Markt.Marktname = ? AND Candlestick.TimeframeID = 1"
  val pstmt = conn.prepareStatement(sql)
  pstmt.setString(1, marktname)

  // Execute the query and get the result
  val rs: ResultSet = pstmt.executeQuery()
  rs.next()
  val lastDate = rs.getTimestamp(1)

  // Close the connection
  rs.close()
  pstmt.close()
  conn.close()

  // Return the last date, or "No Date found" if there's no such date
  if (lastDate != null) lastDate.toString else "No Date found"
}

def getFirstDateofFile(marktname: String): String = {
  var conn: Connection = DriverManager.getConnection(url)

  // Prepare the SQL statement
  val sql = "SELECT MIN(Candlestick.Zeitstempel) FROM Candlestick JOIN Markt ON Candlestick.MarktID = Markt.MarktID WHERE Markt.Marktname = ? AND Candlestick.TimeframeID = 1"
  val pstmt = conn.prepareStatement(sql)
  pstmt.setString(1, marktname)

  // Execute the query and get the result
  val rs: ResultSet = pstmt.executeQuery()
  rs.next()
  val firstDate = rs.getTimestamp(1)

  // Close the connection
  rs.close()
  pstmt.close()
  conn.close()

  // Return the first date, or "No Date found" if there's no such date
  if (firstDate != null) firstDate.toString else "No Date found"
}


def getFirsDateofFile (): String = {
  var conn: Connection = DriverManager.getConnection(url)

  // Prepare the SQL statement
  val sql = "SELECT MIN(Zeitstempel) FROM Candlestick WHERE TimeframeID = 1"
  val pstmt = conn.prepareStatement(sql)

  // Execute the query and get the result
  val rs: ResultSet = pstmt.executeQuery()
  rs.next()
  val firstDate = rs.getTimestamp(1)

  // Close the connection
  rs.close()
  pstmt.close()
  conn.close()

  // Return the first date, or "No Date found" if there's no such date
  if (firstDate != null) firstDate.toString else "No Date found"
}
def getPriceForDateTimeDouble(dateTime: String, ohlc: String): Double = {
  var conn: Connection = DriverManager.getConnection(url)

  // Prepare the SQL statement
  val sql = s"SELECT $ohlc FROM Candlestick WHERE Zeitstempel = datetime(strftime('%Y-%m-%d %H:%M', ?)) AND TimeframeID = 1"
  val pstmt = conn.prepareStatement(sql)
  pstmt.setString(1, dateTime)

  // Execute the query and get the result
  val rs: ResultSet = pstmt.executeQuery()
  val price = if (rs.next()) rs.getDouble(1) else 0.0

  // Close the connection
  rs.close()
  pstmt.close()
  conn.close()

  // Return the price
  price
}

def getPriceForDateTimeString(dateTime: String, ohlc: String): String = {
  var conn: Connection = DriverManager.getConnection(url)

  // Prepare the SQL statement
  val sql = s"SELECT $ohlc FROM Candlestick WHERE Zeitstempel = datetime(strftime('%Y-%m-%d %H:%M', ?)) AND TimeframeID = 1"
  val pstmt = conn.prepareStatement(sql)
  pstmt.setString(1, dateTime)

  // Execute the query and get the result
  val rs: ResultSet = pstmt.executeQuery()
  val price = if (rs.next()) rs.getString(1) else "0.0"

  // Close the connection
  rs.close()
  pstmt.close()
  conn.close()

  // Return the price
  price
}

def isDateBeforefirstDateinFile(dateTime: String): Boolean = {
  var conn: Connection = DriverManager.getConnection(url)

  // Prepare the SQL statement
  val sql = "SELECT MIN(Zeitstempel) FROM Candlestick WHERE TimeframeID = 1"
  val pstmt = conn.prepareStatement(sql)

  // Execute the query and get the result
  val rs: ResultSet = pstmt.executeQuery()
  rs.next()
  val firstDate = rs.getTimestamp(1).toLocalDateTime

  // Close the connection
  rs.close()
  pstmt.close()
  conn.close()

  // Compare the provided date with the first date
  val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
  val dateTimeLocalDateTime = LocalDateTime.parse(dateTime, formatter)

  dateTimeLocalDateTime.isBefore(firstDate)
}

def isDateAfterLastDateinFile(dateTime: String): Boolean = {
  var dateAfterLastDate: Boolean = false

  val lastDate = TradeData.endDate.toString()
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
  var conn: Connection = null
  var pstmt: PreparedStatement = null
  var rs: ResultSet = null
  var date: String = "Trade was not triggered"

  try {
    conn = DriverManager.getConnection(url)
    val datestart = LocalDateTime.parse(trade.datestart, formatter)
    val formattedDatestart = datestart.format(outputFormatter)

    // Prepare the SQL statement
    val sql = s"""SELECT c.Zeitstempel, c.HighPrice, c.LowPrice FROM Candlestick c
                  JOIN Markt m ON c.MarktID = m.MarktID
                  WHERE m.Marktname = '${trade.ticker}' AND c.TimeframeID = 1 AND c.Zeitstempel >= datetime(strftime('%Y-%m-%d %H:%M:%S', '$formattedDatestart'))
                  ORDER BY c.Zeitstempel"""

    pstmt = conn.prepareStatement(sql)

    // Execute the query and get the result
    rs = pstmt.executeQuery()
    
    // Process the result
    var found = false
    while (rs.next() && !found) {
      val line = rs.getTimestamp("Zeitstempel").toLocalDateTime.format(formatter) + "," + rs.getDouble("HighPrice") + "," + rs.getDouble("LowPrice")
      if (isTradeBuyorSell(trade)) {
        
        if (LocalDateTime.parse(line.split(",")(0)+ "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(trade.datestart, formatter)) && trade.entryTrade > line.split(",")(3).toDouble) {
          date = line.split(",")(0)+ "," + line.split(",")(1)
          found = true
        }
      } else {
        if (LocalDateTime.parse(line.split(",")(0)+ "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(trade.datestart, formatter)) && trade.entryTrade < line.split(",")(2).toDouble) {
          date = line.split(",")(0)+ "," + line.split(",")(1)
          found = true
        }
      }
    }
  } finally {
    if (rs != null) rs.close()
    if (pstmt != null) pstmt.close()
    if (conn != null) conn.close()
  }

  date
}

def dateWhenTradehitTakeProfit(trade: TradeComponent): String = {
  var conn: Connection = null
  var pstmt: PreparedStatement = null
  var rs: ResultSet = null
  var date: String = "Trade did not hit take profit"

  try {
    conn = DriverManager.getConnection(url)
    val datestart = LocalDateTime.parse(trade.datestart, formatter)
    val formattedDatestart = datestart.format(outputFormatter)
    // Prepare the SQL statement
    val sql = s"""SELECT c.Zeitstempel, c.HighPrice, c.LowPrice FROM Candlestick c
              JOIN Markt m ON c.MarktID = m.MarktID
              WHERE m.Marktname = '${trade.ticker}' AND c.TimeframeID = 1 AND c.Zeitstempel >= datetime(strftime('%Y-%m-%d %H:%M:%S', '$formattedDatestart'))
              ORDER BY c.Zeitstempel"""

    pstmt = conn.prepareStatement(sql)

    // Execute the query and get the result
    rs = pstmt.executeQuery()

    val dateWhenTradeTriggered1 = dateWhenTradeTriggered(trade)
    if(dateWhenTradeTriggered1.equals("Trade was not triggered")){
      date = "Trade was not triggered"
    } else {
      var found = false
      while (rs.next() && !found) {
        val line = rs.getTimestamp("Zeitstempel").toLocalDateTime.format(formatter) + ","  + rs.getDouble("HighPrice") + "," + rs.getDouble("LowPrice")
        if (isTradeBuyorSell(trade)) {
          if (LocalDateTime.parse(line.split(",")(0)+ "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(dateWhenTradeTriggered1, formatter)) && trade.takeProfitTrade < line.split(",")(2).toDouble) {
            date = line.split(",")(0)+ "," + line.split(",")(1)
            found = true
          }
        } else {
          if (LocalDateTime.parse(line.split(",")(0)+ "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(dateWhenTradeTriggered1, formatter)) && trade.takeProfitTrade > line.split(",")(3).toDouble) {
            date = line.split(",")(0)+ "," + line.split(",")(1)
            found = true
          }
        }
      }
    }
  } finally {
    if (rs != null) rs.close()
    if (pstmt != null) pstmt.close()
    if (conn != null) conn.close()
  }

  date
}

def dateWhenTradehitStopLoss(trade: TradeComponent): String = {
  var conn: Connection = null
  var pstmt: PreparedStatement = null
  var rs: ResultSet = null
  var date: String = "Trade did not hit stop loss"

  try {
    conn = DriverManager.getConnection(url)
    val datestart = LocalDateTime.parse(trade.datestart, formatter)
    val formattedDatestart = datestart.format(outputFormatter)
    // Prepare the SQL statement
    val sql = s"""SELECT c.Zeitstempel, c.HighPrice, c.LowPrice FROM Candlestick c
              JOIN Markt m ON c.MarktID = m.MarktID
              WHERE m.Marktname = '${trade.ticker}' AND c.TimeframeID = 1 AND c.Zeitstempel >= datetime(strftime('%Y-%m-%d %H:%M:%S', '$formattedDatestart'))
              ORDER BY c.Zeitstempel"""
    pstmt = conn.prepareStatement(sql)

    // Execute the query and get the result
    rs = pstmt.executeQuery()

    val dateWhenTradeTriggered1 = dateWhenTradeTriggered(trade)
    if(dateWhenTradeTriggered1.equals("Trade was not triggered")){
      date = "Trade was not triggered"
    } else {
      var found = false
      while (rs.next() && !found) {
        val line = rs.getTimestamp("Zeitstempel").toLocalDateTime.format(formatter) + ","  + rs.getDouble("HighPrice") + "," + rs.getDouble("LowPrice")
        if (isTradeBuyorSell(trade)) {
          if (LocalDateTime.parse(line.split(",")(0)+ "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(dateWhenTradeTriggered1, formatter)) && trade.stopLossTrade > line.split(",")(3).toDouble) {
            date = line.split(",")(0)+ "," + line.split(",")(1)
            found = true
          }
        } else {
          if (LocalDateTime.parse(line.split(",")(0)+ "," + line.split(",")(1), formatter).isAfter(LocalDateTime.parse(dateWhenTradeTriggered1, formatter)) && trade.stopLossTrade < line.split(",")(2).toDouble) {
            date = line.split(",")(0)+ "," + line.split(",")(1)
            found = true
          }
        }
      }
    }
  } finally {
    if (rs != null) rs.close()
    if (pstmt != null) pstmt.close()
    if (conn != null) conn.close()
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

def calculateCurrentProfit(trade: TradeDoneCalculations, volume: Double, currentPrice: Double, currentDate: String): Unit= {
  trade.currentprofit = 0.0
  if(trade.dateTradeTriggered.equals("Trade was not triggered")){

  } else {
  val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
  val parsedCurrentDate = LocalDateTime.parse(currentDate, formatter)
  val parsedDateTradeTriggered = LocalDateTime.parse(trade.dateTradeTriggered, formatter)
  val parsedDateTradeDone = LocalDateTime.parse(trade.dateTradeDone, formatter)

  
  if(parsedCurrentDate.isAfter(parsedDateTradeTriggered)) {
    if(parsedCurrentDate.isBefore(parsedDateTradeDone)) {
      trade.currentprofit = BigDecimal((currentPrice - trade.entryTrade) * volume).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
    } else {
      if(trade.tradeWinOrLose == "Trade hit take profit") {
        trade.currentprofit = BigDecimal(scala.math.abs((trade.takeProfitTrade - trade.entryTrade) * volume)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
      }else if(trade.tradeWinOrLose == "Trade hit stop loss"){
        trade.currentprofit = BigDecimal(scala.math.abs((trade.stopLossTrade - trade.entryTrade) * volume) * -1).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
      } else {
        trade.currentprofit = 0.0
      }
    }
    }
  }
}



class GetMarketDataclass @Inject() (tradeData: TradeDataclass) {
  def closeProgram: String = {
      tradeData.saveData(TradeData.savename)
      println(doneTradeStringwithProfit)
      System.exit(0)
      "should not print"
    }
}

def doneTradeStringwithProfit: String = {
  var output = ""
  for (trade <- TradeData.donetrades) {
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
    output += s"Profit: $$${trade.endProfit * TradeData.balance}\n"
    output += "__________________________________________________________\n"
    TradeData.balance = TradeData.balance + trade.endProfit * TradeData.balance
    output += "__________________________________________________________\n"
    output += s"new Balance: $$${TradeData.balance}\n"
    output += "__________________________________________________________\n"
  }
  output += "__________________________________________________________\n"
  output += s"Final Balance: $$${TradeData.balance}\n"
  output += "__________________________________________________________\n"

  output += "Closing the program..."
  output
}

def showCompany(currentTicker: String, date: String, balance: Double, currentPrice: Double): String = {
    val output =
      s"""_____________________________________
         |Currently trading with :
         |Balance: $balance
         |Company: $currentTicker
         |Date: $date
         |Current Value: $$$currentPrice
         |_____________________________________
         |""".stripMargin

    output
  }

  def currentTrade(trade: Trade): String = {
    val output =
      s"""_____________________________________
         |Current Trade:
         |Ticker: ${trade.ticker}
         |Entry: $$${trade.entryTrade}
         |StopLoss: $$${trade.stopLossTrade}
         |TakeProfit: $$${trade.takeProfitTrade}
         |Risk (in percent): ${trade.risk}%
         |""".stripMargin

    output
  }



}








