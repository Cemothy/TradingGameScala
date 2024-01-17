package de.htwg.se.TradingGame.model.BacktestStage

import de.htwg.se.TradingGame.model.BacktestStage.CandleStick
import de.htwg.se.TradingGame.model.DataSave._

import java.sql._
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ListBuffer

object GetDatabaseData extends App {

  val conn = DriverManager.getConnection("jdbc:sqlite:src/main/scala/de/htwg/se/TradingGame/Database/litedbCandleSticks.db")

def tryConnect(connectionString: String): Boolean = {
  var conn: Connection = null
  try {
    conn = DriverManager.getConnection(connectionString)
    true
  } catch {
    case e: Exception =>
      e.printStackTrace()
      false
  } finally {
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
 def getCandleSticksdadabase(interval: String, symbol: String, endDate: LocalDateTime, size: Int): ListBuffer[CandleStick] = {
  val candleSticks = ListBuffer[CandleStick]()
  interval match {
    case "1m" => TradeData.distancecandles = 1 * 60 
    case "5m" => TradeData.distancecandles = 5 * 60 
    case "15m" => TradeData.distancecandles = 15 * 60 
    case "1h" => TradeData.distancecandles = 60 * 60 
    case "4h" => TradeData.distancecandles = 60 * 4 * 60 
    case "1d" => TradeData.distancecandles = 60 * 24 * 60 
    case "1w" => TradeData.distancecandles = 60 * 24 * 7 * 60 
    case _ => throw new IllegalArgumentException("Invalid interval")
  }

  val sql = """
  SELECT c.* FROM Candlestick c
  JOIN Markt m ON c.MarktID = m.MarktID
  JOIN Timeframe t ON c.TimeframeID = t.TimeframeID
  WHERE m.Marktname = ? AND t.Timeframe = ? AND c.Zeitstempel < ?
  ORDER BY c.Zeitstempel DESC
  LIMIT ?
  """

  val pstmt = conn.prepareStatement(sql)
    pstmt.setString(1, symbol)
    pstmt.setString(2, interval)
    pstmt.setTimestamp(3, Timestamp.valueOf(endDate))
    pstmt.setInt(4, size)
  val rs = pstmt.executeQuery()

  while (rs.next()) {
    val dateTime = rs.getTimestamp("Zeitstempel").toLocalDateTime
    val candleStick = CandleStick(
      day = dateTime.atZone(ZoneId.systemDefault()).toEpochSecond,
      open = rs.getDouble("OpenPrice"),
      close = rs.getDouble("ClosePrice"),
      high = rs.getDouble("HighPrice"),
      low = rs.getDouble("LowPrice")
    )
    candleSticks += candleStick
  }

  rs.close()
  pstmt.close()

  candleSticks
}

  def close(): Unit = {
    conn.close()
  }
def getCandleDataFuture(interval: String, symbol: String, startDate: LocalDateTime, size: Int): ListBuffer[CandleStick] = {
  val candleSticks = ListBuffer[CandleStick]()

  val sql = """
    SELECT c.* FROM Candlestick c
    JOIN Markt m ON c.MarktID = m.MarktID
    JOIN Timeframe t ON c.TimeframeID = t.TimeframeID
    WHERE m.Marktname = ? AND t.Timeframe = ? AND c.Zeitstempel > ?
    ORDER BY c.Zeitstempel ASC
    LIMIT ?
  """

  val pstmt = conn.prepareStatement(sql)
  pstmt.setString(1, symbol)
  pstmt.setString(2, interval)
  pstmt.setTimestamp(3, Timestamp.valueOf(startDate))
  pstmt.setInt(4, size)
  val rs = pstmt.executeQuery()

  while (rs.next()) {
    val dateTime = rs.getTimestamp("Zeitstempel").toLocalDateTime
    val candleStick = CandleStick(
      day = dateTime.atZone(ZoneId.systemDefault()).toEpochSecond,
      open = rs.getDouble("OpenPrice"),
      close = rs.getDouble("ClosePrice"),
      high = rs.getDouble("HighPrice"),
      low = rs.getDouble("LowPrice")
    )
    candleSticks += candleStick
  }

  rs.close()
  pstmt.close()

  candleSticks
}
  def getCandleData(interval: String, symbol: String, startDate: LocalDateTime, size: Int): ListBuffer[CandleStick] = {
  val candleSticks = ListBuffer[CandleStick]()
 

  val sql = """
  SELECT c.* FROM Candlestick c
  JOIN Markt m ON c.MarktID = m.MarktID
  JOIN Timeframe t ON c.TimeframeID = t.TimeframeID
  WHERE m.Marktname = ? AND t.Timeframe = ? AND c.Zeitstempel < ?
  ORDER BY c.Zeitstempel DESC
  LIMIT ?
  """
  val pstmt = conn.prepareStatement(sql)
    pstmt.setString(1, symbol)
    pstmt.setString(2, interval)
    pstmt.setTimestamp(3, Timestamp.valueOf(startDate))
    pstmt.setInt(4, size)
  val rs = pstmt.executeQuery()

  while (rs.next()) {
    val dateTime = rs.getTimestamp("Zeitstempel").toLocalDateTime
    val candleStick = CandleStick(
      day = dateTime.atZone(ZoneId.systemDefault()).toEpochSecond,
      open = rs.getDouble("OpenPrice"),
      close = rs.getDouble("ClosePrice"),
      high = rs.getDouble("HighPrice"),
      low = rs.getDouble("LowPrice")
    )
    candleSticks += candleStick
  }

  rs.close()
  pstmt.close()

  candleSticks
}
def getCandleDataFromTo(interval: String, symbol: String, startDate: LocalDateTime, endDate: LocalDateTime): ListBuffer[CandleStick] = {
  val candleSticks = ListBuffer[CandleStick]()

  val sql = """
    SELECT c.* FROM Candlestick c
    JOIN Markt m ON c.MarktID = m.MarktID
    JOIN Timeframe t ON c.TimeframeID = t.TimeframeID
    WHERE m.Marktname = ? AND t.Timeframe = ? AND c.Zeitstempel BETWEEN ? AND ?
    ORDER BY c.Zeitstempel ASC
  """

  val pstmt = conn.prepareStatement(sql)
  pstmt.setString(1, symbol)
  pstmt.setString(2, interval)
  pstmt.setTimestamp(3, Timestamp.valueOf(startDate))
  pstmt.setTimestamp(4, Timestamp.valueOf(endDate))
  val rs = pstmt.executeQuery()

  while (rs.next()) {
    val dateTime = rs.getTimestamp("Zeitstempel").toLocalDateTime
    val candleStick = CandleStick(
      day = dateTime.atZone(ZoneId.systemDefault()).toEpochSecond,
      open = rs.getDouble("OpenPrice"),
      close = rs.getDouble("ClosePrice"),
      high = rs.getDouble("HighPrice"),
      low = rs.getDouble("LowPrice")
    )
    candleSticks += candleStick
  }

  rs.close()
  pstmt.close()

  candleSticks
}

//      getCandleData("1m", "EURUSD", LocalDateTime.of(2023, 2, 3, 12, 12), 200).foreach(candleStick => {
//      val date = LocalDateTime.ofInstant(Instant.ofEpochSecond(candleStick.day.toLong), ZoneId.systemDefault())
//      println(s"Date: $date, Close Price: ${candleStick.close}")
//  })
//   println()
//   println()
//   println()
//       getCandleDataFuture("1m", "EURUSD", LocalDateTime.of(2023, 2, 3, 12, 12), 200).foreach(candleStick => {
//      val date = LocalDateTime.ofInstant(Instant.ofEpochSecond(candleStick.day.toLong), ZoneId.systemDefault())
//      println(s"Date: $date, Close Price: ${candleStick.close}")
//  })
}