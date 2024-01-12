import java.io._
import java.sql._
import java.text.SimpleDateFormat
import scala.io.Source

object Databaseask extends App {

  val in = new BufferedReader(new InputStreamReader(System.in))

  try {
    // Register the PostgreSQL driver
    Class.forName("org.postgresql.Driver")

    // Update the URL for PostgreSQL
    val url = "jdbc:postgresql://localhost:5432/candlesticks"
    // Update the connection details
    val conn = DriverManager.getConnection(url, "samuel", "3464")

    conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED)
    conn.setAutoCommit(false)

    val stmt = conn.createStatement()

    // Your SQL queries remain the same if they are compatible with PostgreSQL
    val timeframeInsertQuery = "INSERT INTO Timeframe(TimeframeID, Timeframe) VALUES (?, ?)"
    val timeframePreparedStatement = conn.prepareStatement(timeframeInsertQuery)
    timeframePreparedStatement.setInt(1, 10080)
    timeframePreparedStatement.setString(2, "1w")
    timeframePreparedStatement.executeUpdate()

    val candlestickInsertQuery = "INSERT INTO Candlestick(CandlestickID, MarktID, TimeframeID, Zeitstempel, OpenPrice, HighPrice, LowPrice, ClosePrice, Volume) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
    val preparedStatement = conn.prepareStatement(candlestickInsertQuery)

    val format = new SimpleDateFormat("yyyy.MM.dd,HH:mm")

    var candlestickIdCounter = 12830000 // is set right now to the last candlestick id in the database
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
