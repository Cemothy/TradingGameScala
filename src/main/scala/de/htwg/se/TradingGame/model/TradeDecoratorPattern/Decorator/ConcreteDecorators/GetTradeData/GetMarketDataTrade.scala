package de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.GetTradeData


import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.TradeDoneCalculations
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager


class GetMarketDataTrade(gameStateManager: IGameStateManager) {
  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
  def getPriceForDateTimeString(dateTime: LocalDateTime, ohlc: String): Double = {
    var conn: Connection = DriverManager.getConnection(gameStateManager.currentState.databaseConnectionString)

    // Prepare the SQL statement
    val sql = s"SELECT $ohlc FROM Candlestick WHERE Zeitstempel = datetime(strftime('%Y-%m-%d %H:%M', ?)) AND TimeframeID = 1"
    val pstmt = conn.prepareStatement(sql)
    pstmt.setString(1, dateTime.format(formatter))

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
}