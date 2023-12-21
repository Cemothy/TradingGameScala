package de.htwg.se.TradingGame.view.GUI

import de.htwg.se.TradingGame.view.GUI.AdvCandleStickChartSample.CandleStick

import java.sql._
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import scala.collection.mutable.ListBuffer

object GetDatabaseData extends App {
  val url = "jdbc:oracle:thin:@oracle19c.in.htwg-konstanz.de:1521:ora19c"
  val conn = DriverManager.getConnection(url, "dbsys31", "dbsys31")

 def getCandleSticksdadabase(interval: String, symbol: String, endDate: LocalDateTime, size: Int): ListBuffer[CandleStick] = {
  val candleSticks = ListBuffer[CandleStick]()
  interval match {
    case "1m" => AdvCandleStickChartSample.distancecandles = 1 * 60 
    case "5m" => AdvCandleStickChartSample.distancecandles = 5 * 60 
    case "15m" => AdvCandleStickChartSample.distancecandles = 15 * 60 
    case "1h" => AdvCandleStickChartSample.distancecandles = 60 * 60 
    case "4h" => AdvCandleStickChartSample.distancecandles = 60 * 4 * 60 
    case "1d" => AdvCandleStickChartSample.distancecandles = 60 * 24 * 60 
    case "1w" => AdvCandleStickChartSample.distancecandles = 60 * 24 * 7 * 60 
    case _ => throw new IllegalArgumentException("Invalid interval")
  }

  val sql = """
    SELECT * FROM (
        SELECT c.* FROM Candlestick c
        JOIN Markt m ON c.MarktID = m.MarktID
        JOIN Timeframe t ON c.TimeframeID = t.TimeframeID
        WHERE m.Marktname = ? AND t.Timeframe = ? AND c.Zeitstempel < ?
        ORDER BY c.Zeitstempel DESC
    ) WHERE ROWNUM <= ?
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

  def getCandleData(interval: String, symbol: String, startDate: LocalDateTime, size: Int): ListBuffer[CandleStick] = {
  val candleSticks = ListBuffer[CandleStick]()
 

  val sql = """
    SELECT * FROM (
        SELECT c.* FROM Candlestick c
        JOIN Markt m ON c.MarktID = m.MarktID
        JOIN Timeframe t ON c.TimeframeID = t.TimeframeID
        WHERE m.Marktname = ? AND t.Timeframe = ? AND c.Zeitstempel > ?
        ORDER BY c.Zeitstempel ASC
    ) WHERE ROWNUM <= ?
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

//     getCandleData("1m", "EURUSD", LocalDateTime.of(2023, 2, 3, 12, 12), 200).foreach(candleStick => {
//     val date = LocalDateTime.ofInstant(Instant.ofEpochSecond(candleStick.day.toLong), ZoneId.systemDefault())
//     println(s"Date: $date, Close Price: ${candleStick.close}")
// })
}