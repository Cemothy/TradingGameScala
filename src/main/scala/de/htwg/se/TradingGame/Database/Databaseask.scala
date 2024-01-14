import java.io._
import java.sql._
import java.text.SimpleDateFormat
import scala.io.Source

object Databaseask extends App {

  val in = new BufferedReader(new InputStreamReader(System.in))

  try {

    
    val conn = DriverManager.getConnection("jdbc:sqlite:src/main/scala/de/htwg/se/TradingGame/Database/litedbCandleSticks.db")


    conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED)
    conn.setAutoCommit(false)

    val stmt = conn.createStatement()


    val candlestickInsertQuery = "INSERT INTO Candlestick(CandlestickID, MarktID, TimeframeID, Zeitstempel, OpenPrice, HighPrice, LowPrice, ClosePrice, Volume) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
    val preparedStatement = conn.prepareStatement(candlestickInsertQuery)

    val format = new SimpleDateFormat("yyyy.MM.dd,HH:mm")

    var candlestickIdCounter = 11800000 // is set right now to the last candlestick id in the database
    try {
    for (line <- Source.fromFile("C:\\Users\\Samuel\\Documents\\SoftwareEngeneering\\TradingGameScala\\src\\main\\scala\\de\\htwg\\se\\TradingGame\\view\\GUI\\EURUSD10080.csv").getLines) {
      val cols = line.split(",").map(_.trim)

      val date = new java.sql.Timestamp(format.parse(cols(0) + "," + cols(1)).getTime)
      val openPrice = cols(2).toDouble
      val highPrice = cols(3).toDouble
      val lowPrice = cols(4).toDouble
      val closePrice = cols(5).toDouble
      val volume = cols(6).toInt

      val candlestickId = candlestickIdCounter

      preparedStatement.setInt(1, candlestickId)
      preparedStatement.setInt(2, 1)
      preparedStatement.setInt(3, 10080)
      preparedStatement.setTimestamp(4, date)
      preparedStatement.setDouble(5, openPrice)
      preparedStatement.setDouble(6, highPrice)
      preparedStatement.setDouble(7, lowPrice)
      preparedStatement.setDouble(8, closePrice)
      preparedStatement.setInt(9, volume)
      preparedStatement.addBatch()

      candlestickIdCounter += 1
      if (candlestickIdCounter % 10000 == 0) {
        preparedStatement.executeBatch()
        println(candlestickIdCounter)
      }
    }
    preparedStatement.executeBatch()
    } finally {
      preparedStatement.close()
    }
    stmt.close()
    conn.commit()
    conn.close()
  } catch {
    case se: SQLException =>
      println()
      println("SQL Exception occurred while establishing connection to DB: "
        + se.getMessage)
      println("- SQL state  : " + se.getSQLState)
      println("- Message    : " + se.getMessage)
      println("- Vendor code: " + se.getErrorCode)
      println()
      println("EXITING WITH FAILURE ... !!!")
      println()
      System.exit(-1)
  }
}
