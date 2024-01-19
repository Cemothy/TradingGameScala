package de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.GetMarktDataforInterpreterFolder



import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ListBuffer
import scala.util.Using
import java.sql.{Connection, Statement, ResultSet}
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager

object GetMarketDataforInterpreter {

    
  def intervalasSeconds(interval: String): Int = {
    interval match {
      case "1m" => 1 * 60 
      case "5m" => 5 * 60 
      case "15m" => 15 * 60 
      case "1h" => 60 * 60 
      case "4h" => 60 * 4 * 60 
      case "1d" => 60 * 24 * 60 
      case "1w" => 60 * 24 * 7 * 60 
      case _ => throw new IllegalArgumentException("Invalid interval")
    }
  }
  val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
  val outputFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")

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

  def convertToEpochSeconds(dateString: String): Long = {
    val formatter = DateTimeFormatter.ofPattern("yyy.MM.dd,HH:mm")
    val date = LocalDateTime.parse(dateString, formatter)
    val epochSeconds = date.atZone(ZoneId.systemDefault()).toEpochSecond
    epochSeconds
  }
  def getPairNames(connectionString: String): List[String] = {
    var conn: Connection = null
    var statement: Statement = null
    var rs: ResultSet = null
    try {
      conn = DriverManager.getConnection(connectionString)
      statement = conn.createStatement()
      rs = statement.executeQuery("SELECT Marktname FROM Markt")

      val pairNames = ListBuffer[String]()
      while (rs.next()) {
        pairNames += rs.getString("Marktname")
      }
      val pairList = pairNames.toList
      pairList // Return the list
    } catch {
      case e: Exception =>
        e.printStackTrace()
        Nil
    } finally {
      if (rs != null) rs.close()
      if (statement != null) statement.close()
      if (conn != null) conn.close()
    }
  }

  def getDatesForMarktnames(marktnames: List[String], gameStateManager: IGameStateManager): Map[String, (String, String)] = {
    marktnames.map { marktname =>
        val firstDate = LocalDateTime.parse(getFirstDateofFile(marktname, gameStateManager), outputFormatter2)
        val lastDate = LocalDateTime.parse(getLastDateofFile(marktname, gameStateManager), outputFormatter2)
        val firstDateoutput = firstDate.format(formatter)
        val lastDateoutput = lastDate.format(formatter)
        gameStateManager.changeStartDate(firstDate.atZone(ZoneId.systemDefault()).toEpochSecond()) 
        gameStateManager.changeEndDate(lastDate.atZone(ZoneId.systemDefault()).toEpochSecond())
        marktname -> (firstDateoutput, lastDateoutput)
    }.toMap
    }
  def getFirstDateofFile(marktname: String, gameStateManager: IGameStateManager): String = {
    var conn: Connection = DriverManager.getConnection(gameStateManager.currentState.databaseConnectionString)
    val sql = "SELECT MIN(Candlestick.Zeitstempel) FROM Candlestick JOIN Markt ON Candlestick.MarktID = Markt.MarktID WHERE Markt.Marktname = ? AND Candlestick.TimeframeID = 1"
    val pstmt = conn.prepareStatement(sql)
    pstmt.setString(1, marktname)
    val rs: ResultSet = pstmt.executeQuery()
    rs.next()
    val firstDate = rs.getTimestamp(1)
    rs.close()
    pstmt.close()
    conn.close()
    if (firstDate != null) firstDate.toString else "No Date found"
  }

  def getLastDateofFile(marktname: String, gameStateManager: IGameStateManager): String = {
    var conn: Connection = DriverManager.getConnection(gameStateManager.currentState.databaseConnectionString)
    val sql = "SELECT MAX(Candlestick.Zeitstempel) FROM Candlestick JOIN Markt ON Candlestick.MarktID = Markt.MarktID WHERE Markt.Marktname = ? AND Candlestick.TimeframeID = 1"
    val pstmt = conn.prepareStatement(sql)
    pstmt.setString(1, marktname)
    val rs: ResultSet = pstmt.executeQuery()
    rs.next()
    val lastDate = rs.getTimestamp(1)
    rs.close()
    pstmt.close()
    conn.close()
    if (lastDate != null) lastDate.toString else "No Date found"
  }

}