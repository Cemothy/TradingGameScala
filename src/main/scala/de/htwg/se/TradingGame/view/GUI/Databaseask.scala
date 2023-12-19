import java.io._
import java.sql._
import java.text.SimpleDateFormat
import scala.io.Source

object Databaseask extends App {

  val in = new BufferedReader(new InputStreamReader(System.in))

  try {
    DriverManager.registerDriver(new oracle.jdbc.OracleDriver())
    val url = "jdbc:oracle:thin:@oracle19c.in.htwg-konstanz.de:1521:ora19c"
    val conn = DriverManager.getConnection(url, "dbsys31", "dbsys31")

    conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED)
    conn.setAutoCommit(false)

    val stmt = conn.createStatement()

    val timeframeInsertQuery = "INSERT INTO Timeframe(TimeframeID, Timeframe) VALUES (?, ?)"
    val timeframePreparedStatement = conn.prepareStatement(timeframeInsertQuery)
    timeframePreparedStatement.setInt(1, 1440)
    timeframePreparedStatement.setString(2, "1d")
    timeframePreparedStatement.executeUpdate()

    // val marktInsertQuery = "INSERT INTO Markt(MarktID, Marktname) VALUES (?, ?)"
    // val marktPreparedStatement = conn.prepareStatement(marktInsertQuery)
    // marktPreparedStatement.setInt(1, 1)
    // marktPreparedStatement.setString(2, "EURUSD")
    // marktPreparedStatement.executeUpdate()

    val candlestickInsertQuery = "INSERT INTO Candlestick(CandlestickID, MarktID, TimeframeID, Zeitstempel, OpenPrice, HighPrice, LowPrice, ClosePrice, Volume) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
    val preparedStatement = conn.prepareStatement(candlestickInsertQuery)

    val format = new SimpleDateFormat("yyyy.MM.dd,HH:mm")


    var candlestickIdCounter = 12768000
    for (line <- Source.fromFile("C:\\Users\\Samuel\\Documents\\SoftwareEngeneering\\TradingGameScala\\src\\main\\scala\\de\\htwg\\se\\TradingGame\\view\\GUI\\EURUSD1440.csv").getLines) {
      
      val cols = line.split(",").map(_.trim)

      val date = new java.sql.Timestamp(format.parse(cols(0) + "," + cols(1)).getTime)
      val openPrice = new java.math.BigDecimal(cols(2))
      val highPrice = new java.math.BigDecimal(cols(3))
      val lowPrice = new java.math.BigDecimal(cols(4))
      val closePrice = new java.math.BigDecimal(cols(5))
      val volume = Integer.parseInt(cols(6))

      val candlestickId = candlestickIdCounter

      preparedStatement.setInt(1, candlestickId) 
      preparedStatement.setInt(2, 1) 
      preparedStatement.setInt(3, 1440) 
      preparedStatement.setTimestamp(4, date)
      preparedStatement.setBigDecimal(5, openPrice)
      preparedStatement.setBigDecimal(6, highPrice)
      preparedStatement.setBigDecimal(7, lowPrice)
      preparedStatement.setBigDecimal(8, closePrice)
      preparedStatement.setInt(9, volume)
        preparedStatement.addBatch()

      candlestickIdCounter += 1
      if(candlestickIdCounter % 1000 == 0) {
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
      println("SQL Exception occurred while establishing connection to DBS: "
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