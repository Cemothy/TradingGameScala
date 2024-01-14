package de.htwg.se.TradingGame.model.FileIO

import _root_.de.htwg.se.TradingGame.model.TradeDecoratorPattern.Trade
import _root_.de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import _root_.de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeDoneCalculations

import scala.collection.mutable.ArrayBuffer
import scala.xml._

class TradeDataXMLFileIO extends TradeDataFileIO{
   override def saveData(donetrades: ArrayBuffer[TradeDoneCalculations], balance: Double, pair: String, backtestDate: Long, filename: String): Unit = {
    val filnamexml = "C:\\Users\\Samuel\\Documents\\SoftwareEngeneering\\TradingGameScala\\src\\main\\scala\\de\\htwg\\se\\TradingGame\\Data\\" + filename + ".xml"
    val file = new java.io.File(filnamexml)
    println("Absolute path: " + file.getAbsolutePath)
    println("Exists: " + file.exists)
    println("Readable: " + file.canRead)
    if (!file.getParentFile.exists()) {
      val directoriesCreated = file.getParentFile.mkdirs()
      println("Directories created: " + directoriesCreated)
    }
    val doneTradesElements = donetrades.map { trade =>
      <DoneTrades>
        <entryTrade>{trade.trade.entryTrade}</entryTrade>
        <stopLossTrade>{trade.trade.stopLossTrade}</stopLossTrade>
        <takeProfitTrade>{trade.trade.takeProfitTrade}</takeProfitTrade>
        <risk>{trade.trade.risk}</risk>
        <datestart>{trade.trade.datestart}</datestart>
        <ticker>{trade.trade.ticker}</ticker>
        <dateTradeTriggered>{trade.dateTradeTriggered}</dateTradeTriggered>
        <tradeWinOrLose>{trade.tradeWinOrLose}</tradeWinOrLose>
        <dateTradeDone>{trade.dateTradeDone}</dateTradeDone>
        <currentprofit>{trade.currentprofit}</currentprofit>
        <endProfit>{trade.endProfit}</endProfit>
      </DoneTrades>
    }

   val rootElement = <TradeData>
      {doneTradesElements}
      <Balance>{balance}</Balance>
      <Pair>{pair}</Pair>
      <BacktestDate>{backtestDate}</BacktestDate>
    </TradeData>

    XML.save(filnamexml, rootElement, "UTF-8", true)
  }

  override def loadData(filename: String): (ArrayBuffer[TradeDoneCalculations], Double, String, Long) = {
    val filnamexml = "src\\main\\scala\\de\\htwg\\se\\TradingGame\\Data\\" + filename + ".xml"
    val file = XML.loadFile(filnamexml)

    val doneTrades = (file \ "DoneTrades").map { trade =>
      val entryTrade = (trade \ "entryTrade").text.toDouble
      val stopLossTrade = (trade \ "stopLossTrade").text.toDouble
      val takeProfitTrade = (trade \ "takeProfitTrade").text.toDouble
      val risk = (trade \ "risk").text.toDouble
      val datestart = (trade \ "datestart").text
      val ticker = (trade \ "ticker").text
      val dateTradeTriggered = (trade \ "dateTradeTriggered").text
      val tradeWinOrLose = (trade \ "tradeWinOrLose").text
      val dateTradeDone = (trade \ "dateTradeDone").text
      val currentprofit = (trade \ "currentprofit").text.toDouble
      val endProfit = (trade \ "endProfit").text.toDouble
      
      val tradeComponent = new Trade(entryTrade, stopLossTrade, takeProfitTrade, risk, datestart, ticker)
      val tradeDoneCalculations = new TradeDoneCalculations(tradeComponent, dateTradeTriggered, tradeWinOrLose, dateTradeDone, currentprofit, endProfit)
      tradeDoneCalculations
    }

    val balance = (file \ "Balance").text.toDouble
    val pair = (file \ "Pair").text
    val backtestDate = (file \ "BacktestDate").text.toLong

    (ArrayBuffer(doneTrades: _*), balance, pair, backtestDate)
  }
}