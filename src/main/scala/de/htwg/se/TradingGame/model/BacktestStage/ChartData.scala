package de.htwg.se.TradingGame.model.BacktestStage


import de.htwg.se.TradingGame.model.DataSave.TradeData

import java.sql._
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ChartData extends App {

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
// TradeData.interval = "1h"
val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
// val chartdata= new ChartData("jdbc:sqlite:src/main/scala/de/htwg/se/TradingGame/Database/litedbCandleSticks.db", "EURUSD", 100)
// chartdata.getCandleDataBuffer(TradeData.convertToEpochSeconds("2021.02.02,00:00"))
// chartdata.candleSticks.getAll.foreach(candleStick => println(s"Day: ${Instant.ofEpochSecond(candleStick.day).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter)}, Close: ${candleStick.close}"))
// val candlesticksdata = chartdata.initialize(TradeData.convertToEpochSeconds("2021.02.02,00:00"))


// candlesticksdata.foreach { candleStick =>
//   val date = Instant.ofEpochSecond(candleStick.day).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter)
//   println(s"Date: $date, Close: ${candleStick.close}")
// }

  class ChartData(connectionString: String, symbol: String, bufferSize: Int) {
  val conn = DriverManager.getConnection(connectionString)
  val candleSticks = new CircularBuffer[CandleStick](bufferSize)
  
  def initialize(date: Long): ListBuffer[CandleStick] = {

    getCandleDataBuffer(date)
    val middlethird = getMiddleThird
    TradeData.lowestLoadedDate = middlethird.headOption.map(_.day).getOrElse(0L)
    TradeData.alwayslowestLoadedDate = TradeData.lowestLoadedDate
    TradeData.highestLoadedDate = middlethird.lastOption.map(_.day).getOrElse(0L)
    TradeData.alwayshighestLoadedDate = TradeData.highestLoadedDate
    middlethird
}
  def getMiddleThird: ListBuffer[CandleStick] = {

    candleSticks.getMiddleThird
  }


  def moveBufferRight = {
for (_ <- 1 to bufferSize / 3) {
    candleSticks.removeOldest()
  }
  Future{
  getCandleDataBufferRight(TradeData.alwayshighestLoadedDate)
}
  }
  def moveBufferLeft = {
for (_ <- 1 to bufferSize / 3) {
    candleSticks.removeNewest()
  }
  Future{
  getCandleDataBufferLeft(TradeData.alwayslowestLoadedDate)
}
  }
def getUpperThird: ListBuffer[CandleStick] = {
  val upperThird = candleSticks.getNewestThird
  for (_ <- 1 to bufferSize / 3) {
    candleSticks.removeOldest()
  }

  println()
 println("getUpperThird before alwayslowestLoadedDate: " + Instant.ofEpochSecond(TradeData.alwayslowestLoadedDate).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter))
  println("getUpperThird before alwayshighestLoadedDate: " + Instant.ofEpochSecond(TradeData.alwayshighestLoadedDate).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter))
  val highestDataPointInUpperThird = upperThird.maxBy(dataPoint => dataPoint.day)
  TradeData.alwayshighestLoadedDate = highestDataPointInUpperThird.day
  println("getUpperThird after alwayslowestLoadedDate: " + Instant.ofEpochSecond(TradeData.alwayslowestLoadedDate).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter))
  println("getUpperThird after alwayshighestLoadedDate: " + Instant.ofEpochSecond(TradeData.alwayshighestLoadedDate).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter))
  Future{
  getCandleDataBufferRight(TradeData.alwayshighestLoadedDate)
}
  upperThird
}
def getLowerThird: ListBuffer[CandleStick] = {
  val lowerThird = candleSticks.getOldestThird
  for (_ <- 1 to bufferSize / 3) {
    candleSticks.removeNewest()
  }
  println()
  println("getLowerThird before alwayslowestLoadedDate: " + Instant.ofEpochSecond(TradeData.alwayslowestLoadedDate).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter))
  println("getLowerThird before alwayshighestLoadedDate: " + Instant.ofEpochSecond(TradeData.alwayshighestLoadedDate).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter))
  val lowestDataPointInLowerThird = lowerThird.minBy(dataPoint => dataPoint.day)
  TradeData.alwayslowestLoadedDate = lowestDataPointInLowerThird.day
  println("getLowerThird after alwayslowestLoadedDate: " + Instant.ofEpochSecond(TradeData.alwayslowestLoadedDate).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter))
  println("getLowerThird after alwayshighestLoadedDate: " + Instant.ofEpochSecond(TradeData.alwayshighestLoadedDate).atZone(ZoneId.systemDefault()).toLocalDateTime().format(formatter))
  Future{
  getCandleDataBufferLeft(TradeData.alwayslowestLoadedDate)
  }
  lowerThird
}

  def close(): Unit = {
    conn.close()
  }
def getCandleDataBuffer(date: Long): Unit = {
  val rightSize = (bufferSize * 4) / 9
  val leftSize = bufferSize - rightSize

  val rightSql = """
    SELECT c.* FROM Candlestick c
    JOIN Markt m ON c.MarktID = m.MarktID
    JOIN Timeframe t ON c.TimeframeID = t.TimeframeID
    WHERE m.Marktname = ? AND t.Timeframe = ? AND c.Zeitstempel >= ?
    ORDER BY c.Zeitstempel ASC
    LIMIT ?
  """

  val leftSql = """
    SELECT c.* FROM Candlestick c
    JOIN Markt m ON c.MarktID = m.MarktID
    JOIN Timeframe t ON c.TimeframeID = t.TimeframeID
    WHERE m.Marktname = ? AND t.Timeframe = ? AND c.Zeitstempel < ?
    ORDER BY c.Zeitstempel DESC
    LIMIT ?
  """

 val leftPstmt = conn.prepareStatement(leftSql)
  leftPstmt.setString(1, symbol)
  leftPstmt.setString(2, TradeData.interval)
  leftPstmt.setTimestamp(3, new Timestamp(date * 1000))
  leftPstmt.setInt(4, leftSize)
  val leftRs = leftPstmt.executeQuery()

  val leftCandleSticks = new ListBuffer[CandleStick]

while (leftRs.next()) {
    val candleStick = createCandleStickFromResultSet(leftRs)
    candleSticks.addOldest(candleStick) 
  }


  leftRs.close()
  leftPstmt.close()

  val rightPstmt = conn.prepareStatement(rightSql)
  rightPstmt.setString(1, symbol)
  rightPstmt.setString(2, TradeData.interval)
  rightPstmt.setTimestamp(3, new Timestamp(date * 1000))
  rightPstmt.setInt(4, rightSize)
  val rightRs = rightPstmt.executeQuery()

while (rightRs.next()) {
    val candleStick = createCandleStickFromResultSet(rightRs)
    candleSticks.addNewest(candleStick) // changed this line
  }

  rightRs.close()
  rightPstmt.close()
}
def getCandleDataBufferRight(date: Long): Unit = {
  val rightSize = bufferSize / 3

  val rightSql = """
    SELECT c.* FROM Candlestick c
    JOIN Markt m ON c.MarktID = m.MarktID
    JOIN Timeframe t ON c.TimeframeID = t.TimeframeID
    WHERE m.Marktname = ? AND t.Timeframe = ? AND c.Zeitstempel > ?
    ORDER BY c.Zeitstempel ASC
    LIMIT ?
  """

  val rightPstmt = conn.prepareStatement(rightSql)
  rightPstmt.setString(1, symbol)
  rightPstmt.setString(2, TradeData.interval)
  rightPstmt.setTimestamp(3, new Timestamp(date * 1000))
  rightPstmt.setInt(4, rightSize)
  val rightRs = rightPstmt.executeQuery()

  while (rightRs.next()) {
    val candleStick = createCandleStickFromResultSet(rightRs)
    candleSticks.addNewest(candleStick) // changed this line
  }

  rightRs.close()
  rightPstmt.close()
}

def getCandleDataBufferLeft(date: Long): Unit = {
  val leftSize = bufferSize / 3

  val leftSql = """
    SELECT c.* FROM Candlestick c
    JOIN Markt m ON c.MarktID = m.MarktID
    JOIN Timeframe t ON c.TimeframeID = t.TimeframeID
    WHERE m.Marktname = ? AND t.Timeframe = ? AND c.Zeitstempel < ?
    ORDER BY c.Zeitstempel DESC
    LIMIT ?
  """

  val leftPstmt = conn.prepareStatement(leftSql)
  leftPstmt.setString(1, symbol)
  leftPstmt.setString(2, TradeData.interval)
  leftPstmt.setTimestamp(3, new Timestamp(date * 1000))
  leftPstmt.setInt(4, leftSize)
  val leftRs = leftPstmt.executeQuery()

  val leftCandleSticks = new ListBuffer[CandleStick]

  while (leftRs.next()) {
    val candleStick = createCandleStickFromResultSet(leftRs)
    candleSticks.addOldest(candleStick) // changed this line
  }

  leftRs.close()
  leftPstmt.close()
}
def createCandleStickFromResultSet(rs: ResultSet): CandleStick = {
  val dateTime = rs.getTimestamp("Zeitstempel").toLocalDateTime
  CandleStick(
    day = dateTime.atZone(ZoneId.systemDefault()).toEpochSecond,
    open = rs.getDouble("OpenPrice"),
    close = rs.getDouble("ClosePrice"),
    high = rs.getDouble("HighPrice"),
    low = rs.getDouble("LowPrice")
  )
}


  }
}
