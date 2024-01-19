package de.htwg.se.TradingGame.view.GUI.Stages.BacktestStageFolder



import java.sql._
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import de.htwg.se.TradingGame.view.GUI.Stages.BacktestStageFolder._
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager


object ChartData extends App {
  var conn: Connection = null
  def tryConnect(connectionString: String): Boolean = 
    try 
      conn = DriverManager.getConnection(connectionString)
      true
    catch 
      case e: Exception =>
        e.printStackTrace()
        false
        
  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  class ChartData(bufferSize: Int, gameStateManager: IGameStateManager) {
    var lowestLoadedDate: Long = 0L
    var highestLoadedDate: Long = 0L
    var alwayslowestLoadedDate: Long = 0L
    var alwayshighestLoadedDate: Long = 0L
    if (conn == null || conn.isClosed) tryConnect(gameStateManager.currentState.databaseConnectionString)
    val candleSticks = new CircularBuffer[CandleStick](bufferSize)
    
    def initialize(date: Long): ListBuffer[CandleStick] = 
      getCandleDataBuffer(date)
      val middlethird = getMiddleThird
      lowestLoadedDate = middlethird.headOption.map(_.day).getOrElse(0L)
      alwayslowestLoadedDate = lowestLoadedDate
      highestLoadedDate = middlethird.lastOption.map(_.day).getOrElse(0L)
      alwayshighestLoadedDate = highestLoadedDate
      middlethird

    def getMiddleThird: ListBuffer[CandleStick] = candleSticks.getMiddleThird
    
    def moveBufferRight = 
      for (_ <- 1 to bufferSize / 3) candleSticks.removeOldest()
      Future{getCandleDataBufferRight(alwayshighestLoadedDate)}
      
    def moveBufferLeft = 
      for (_ <- 1 to bufferSize / 3) candleSticks.removeNewest()
      Future{getCandleDataBufferLeft(alwayslowestLoadedDate)}
        

    def getUpperThird: ListBuffer[CandleStick] = 
      val upperThird = candleSticks.getNewestThird
      for (_ <- 1 to bufferSize / 3) candleSticks.removeOldest()
      val highestDataPointInUpperThird = upperThird.maxBy(dataPoint => dataPoint.day)
      alwayshighestLoadedDate = highestDataPointInUpperThird.day
      Future{getCandleDataBufferRight(alwayshighestLoadedDate)}
      upperThird
    

    def getLowerThird: ListBuffer[CandleStick] = 
      val lowerThird = candleSticks.getOldestThird
      for (_ <- 1 to bufferSize / 3) candleSticks.removeNewest()
      val lowestDataPointInLowerThird = lowerThird.minBy(dataPoint => dataPoint.day)
      alwayslowestLoadedDate = lowestDataPointInLowerThird.day
      Future{getCandleDataBufferLeft(alwayslowestLoadedDate)}
      lowerThird


    
    def getCandleDataBuffer(date: Long): Unit = 
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
      leftPstmt.setString(1, gameStateManager.currentState.pair)
      leftPstmt.setString(2, gameStateManager.currentState.interval)
      leftPstmt.setTimestamp(3, new Timestamp(date * 1000))
      leftPstmt.setInt(4, leftSize)
      val leftRs = leftPstmt.executeQuery()
      val leftCandleSticks = new ListBuffer[CandleStick]
      while (leftRs.next()) 
        val candleStick = createCandleStickFromResultSet(leftRs)
        candleSticks.addOldest(candleStick) 
      leftRs.close()
      leftPstmt.close()
      val rightPstmt = conn.prepareStatement(rightSql)
      rightPstmt.setString(1, gameStateManager.currentState.pair)
      rightPstmt.setString(2, gameStateManager.currentState.interval)
      rightPstmt.setTimestamp(3, new Timestamp(date * 1000))
      rightPstmt.setInt(4, rightSize)
      val rightRs = rightPstmt.executeQuery()
      while (rightRs.next()) 
        val candleStick = createCandleStickFromResultSet(rightRs)
        candleSticks.addNewest(candleStick)
      rightRs.close()
      rightPstmt.close()
    
    def getCandleDataBufferRight(date: Long): Unit = 
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
      rightPstmt.setString(1, gameStateManager.currentState.pair)
      rightPstmt.setString(2, gameStateManager.currentState.interval)
      rightPstmt.setTimestamp(3, new Timestamp(date * 1000))
      rightPstmt.setInt(4, rightSize)
      val rightRs = rightPstmt.executeQuery()
      while (rightRs.next()) 
        val candleStick = createCandleStickFromResultSet(rightRs)
        candleSticks.addNewest(candleStick)
      rightRs.close()
      rightPstmt.close()
    
    def getCandleDataBufferLeft(date: Long): Unit = 
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
      leftPstmt.setString(1, gameStateManager.currentState.pair)
      leftPstmt.setString(2, gameStateManager.currentState.interval)
      leftPstmt.setTimestamp(3, new Timestamp(date * 1000))
      leftPstmt.setInt(4, leftSize)
      val leftRs = leftPstmt.executeQuery()
      val leftCandleSticks = new ListBuffer[CandleStick]
      while (leftRs.next()) 
        val candleStick = createCandleStickFromResultSet(leftRs)
        candleSticks.addOldest(candleStick)
      leftRs.close()
      leftPstmt.close()
    
    def createCandleStickFromResultSet(rs: ResultSet): CandleStick = 
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
