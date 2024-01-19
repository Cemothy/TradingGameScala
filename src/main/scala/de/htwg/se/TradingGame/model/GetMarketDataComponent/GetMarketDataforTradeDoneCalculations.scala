package de.htwg.se.TradingGame.model.GetMarketDataComponent

import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import de.htwg.se.TradingGame.controller.GameStateManager
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.DriverManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GetMarketDataforTradeDoneCalculations(trade: TradeComponent, gameStateManager: GameStateManager){
  val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
  val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  val url = gameStateManager.currentState.databaseConnectionString
  var conn: Connection = null
  val isTradeBuyorSell: Boolean = if(trade.takeProfitTrade > trade.stopLossTrade){
    true
  } else {
    false
  }
  var didTradeWinorLoose: String = ""
  var dateTradeTriggered: String = ""
  var dateTradeDone: String = ""
  var dateTradehitTakeProfit: String = ""
  var dateTradehitStopLoss: String = ""

  try {
    conn = DriverManager.getConnection(url)
    didTradeWinnorLoose
  } finally {
    if (conn != null) conn.close()
  }

  def didTradeWinnorLoose = {
    var result: String = "Trade did not hit take profit or stop loss"
    val dateWhenTradehitTakeProfit1 = dateWhenTradehitTakeProfit
    val dateWhenTradehitStopLoss1 = dateWhenTradehitStopLoss
      dateWhenTradeTriggered
      datewhenTradeisdone
      if(dateTradeTriggered.equals("Trade was not triggered")){
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

    didTradeWinorLoose = result

  }
  def dateWhenTradeTriggered = {
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    var date: String = "Trade was not triggered"

  
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
        if (isTradeBuyorSell) {
          
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

      if (rs != null) rs.close()
      if (pstmt != null) pstmt.close()
    dateTradeTriggered = date
    date
  }
  def datewhenTradeisdone = {
    val dateWhenTradehitTakeProfit1 = dateTradehitTakeProfit
    val dateWhenTradehitStopLoss1 = dateTradehitStopLoss
    var result: String = "Trade did not hit take profit or stop loss"
    if (dateTradeTriggered.equals("Trade was not triggered")) {
      result = "Trade was not triggered"
    } else if (dateWhenTradehitStopLoss1.equals("Trade did not hit stop loss") && dateWhenTradehitTakeProfit1.equals("Trade did not hit take profit")) {
      result = "Trade did not hit take profit or stop loss"
    } else if (dateWhenTradehitStopLoss1.equals("Trade did not hit stop loss")) {
      result = dateWhenTradehitTakeProfit1
    } else if (dateWhenTradehitTakeProfit1.equals("Trade did not hit take profit")) {
      result = dateWhenTradehitStopLoss1
    } else if (LocalDateTime.parse(dateWhenTradehitTakeProfit1, formatter).isBefore(LocalDateTime.parse(dateWhenTradehitStopLoss1, formatter))) {
      result = dateWhenTradehitTakeProfit1
    } else {
      result = dateWhenTradehitStopLoss1
    }
      dateTradeDone = result
  }

  def dateWhenTradehitTakeProfit: String = {

    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    var date: String = "Trade did not hit take profit"


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

      val dateWhenTradeTriggered1 = dateTradeTriggered
      if(dateWhenTradeTriggered1.equals("Trade was not triggered")){
        date = "Trade was not triggered"
      } else {
        var found = false
        while (rs.next() && !found) {
          val line = rs.getTimestamp("Zeitstempel").toLocalDateTime.format(formatter) + ","  + rs.getDouble("HighPrice") + "," + rs.getDouble("LowPrice")
          if (isTradeBuyorSell) {
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
  
      if (rs != null) rs.close()
      if (pstmt != null) pstmt.close()
      dateTradehitTakeProfit = date
    

    date
  }
  def dateWhenTradehitStopLoss: String = {
    var pstmt: PreparedStatement = null
    var rs: ResultSet = null
    var date: String = "Trade did not hit stop loss"
      val datestart = LocalDateTime.parse(trade.datestart, formatter)
      val formattedDatestart = datestart.format(outputFormatter)
      val sql = s"""SELECT c.Zeitstempel, c.HighPrice, c.LowPrice FROM Candlestick c
                JOIN Markt m ON c.MarktID = m.MarktID
                WHERE m.Marktname = '${trade.ticker}' AND c.TimeframeID = 1 AND c.Zeitstempel >= datetime(strftime('%Y-%m-%d %H:%M:%S', '$formattedDatestart'))
                ORDER BY c.Zeitstempel"""
      pstmt = conn.prepareStatement(sql)
      rs = pstmt.executeQuery()
      val dateWhenTradeTriggered1 = dateTradeTriggered
      if(dateWhenTradeTriggered1.equals("Trade was not triggered")){
        date = "Trade was not triggered"
      } else {
        var found = false
        while (rs.next() && !found) {
          val line = rs.getTimestamp("Zeitstempel").toLocalDateTime.format(formatter) + ","  + rs.getDouble("HighPrice") + "," + rs.getDouble("LowPrice")
          if (isTradeBuyorSell) {
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
      if (rs != null) rs.close()
      if (pstmt != null) pstmt.close()
    dateTradehitStopLoss = date
    date
  }
      
}