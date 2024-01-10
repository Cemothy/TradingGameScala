package de.htwg.se.TradingGame.model.FileIO

import de.htwg.se.TradingGame.model.TradeDecoratorPattern.{TradeComponent, TradeDoneCalculations}
import scala.collection.mutable.ArrayBuffer
import scala.xml._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Trade

class TradeDataXMLFileIO(filename: String) {
  def saveData(donetrades: ArrayBuffer[TradeDoneCalculations], balance: Double): Unit = {
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
    </TradeData>

    XML.save(filename, rootElement, "UTF-8", true)
  }

  def loadData(): (ArrayBuffer[TradeDoneCalculations], Double) = {
    val file = XML.loadFile(filename)

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

    (ArrayBuffer(doneTrades: _*), balance)
  }
}