package de.htwg.se.TradingGame.model.FileIO

import _root_.de.htwg.se.TradingGame.model.TradeDecoratorPattern.Trade
import _root_.de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import _root_.de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeDoneCalculations
import play.api.libs.json._
import java.nio.file.Files
import java.io._
import scala.collection.mutable.ArrayBuffer
import java.nio.file.Paths

// Custom JSON Writes and Reads for TradeComponent
implicit val tradeComponentWrites: Writes[TradeComponent] = new Writes[TradeComponent] {
  def writes(trade: TradeComponent): JsValue = Json.obj(
    "entryTrade" -> trade.entryTrade,
    "stopLossTrade" -> trade.stopLossTrade,
    "takeProfitTrade" -> trade.takeProfitTrade,
    "risk" -> trade.risk,
    "datestart" -> trade.datestart,
    "ticker" -> trade.ticker
  )
}

// As an example, assuming a default implementation for Reads
implicit val tradeComponentReads: Reads[TradeComponent] = new Reads[TradeComponent] {
    def reads(json: JsValue): JsResult[TradeComponent] = {
        val entryTrade = (json \ "entryTrade").as[Double]
        val stopLossTrade = (json \ "stopLossTrade").as[Double]
        val takeProfitTrade = (json \ "takeProfitTrade").as[Double]
        val risk = (json \ "risk").as[Double]
        val datestart = (json \ "datestart").as[String]
        val ticker = (json \ "ticker").as[String]
    
        JsSuccess(new Trade(entryTrade, stopLossTrade, takeProfitTrade, risk, datestart, ticker))
    }
}

// Custom JSON Writes and Reads for TradeDoneCalculations
implicit val tradeDoneCalculationsWrites: Writes[TradeDoneCalculations] = new Writes[TradeDoneCalculations] {
  def writes(tradeDone: TradeDoneCalculations): JsValue = Json.obj(
    "trade" -> Json.toJson(tradeDone.trade)(tradeComponentWrites),
    "dateTradeTriggered" -> tradeDone.dateTradeTriggered,
    "tradeWinOrLose" -> tradeDone.tradeWinOrLose,
    "dateTradeDone" -> tradeDone.dateTradeDone,
    "currentprofit" -> tradeDone.currentprofit,
    "endProfit" -> tradeDone.endProfit 
  )
}

// Similar custom Reads for TradeDoneCalculations
implicit val tradeDoneCalculationsReads: Reads[TradeDoneCalculations] = new Reads[TradeDoneCalculations] {
  def reads(json: JsValue): JsResult[TradeDoneCalculations] = {
    val trade = (json \ "trade").validate[TradeComponent](tradeComponentReads).get
    val dateTradeTriggered = (json \ "dateTradeTriggered").as[String]
    val tradeWinOrLose = (json \ "tradeWinOrLose").as[String]
    val dateTradeDone = (json \ "dateTradeDone").as[String]
    val currentprofit = (json \ "currentprofit").as[Double]
    val endProfit = (json \ "endProfit").as[Double]

    JsSuccess(new TradeDoneCalculations(trade, dateTradeTriggered, tradeWinOrLose, dateTradeDone, currentprofit, endProfit))
  }
}
class TradeDataJSONFileIO extends TradeDataFileIO {
  def saveData(donetrades: ArrayBuffer[TradeDoneCalculations], balance: Double, filename: String): Unit = {
    val filenameJason = "src\\main\\scala\\de\\htwg\\se\\TradingGame\\Data\\" + filename + ".json"
    val json = Json.obj(
      "DoneTrades" -> Json.toJson(donetrades.toSeq)(Writes.seq(tradeDoneCalculationsWrites)),
      "Balance" -> balance
    )
    
    val jsonString = Json.prettyPrint(json)
    Files.write(Paths.get(filenameJason), jsonString.getBytes)
  }

  def loadData(filename: String): (ArrayBuffer[TradeDoneCalculations], Double) = {
    val filenameJason = "src/main/scala/de/htwg/se/TradingGame/Data/" + filename + ".json"
    val source: String = new String(Files.readAllBytes(Paths.get(filenameJason)))
    val json: JsValue = Json.parse(source)

    val donetrades = (json \ "DoneTrades").as[ArrayBuffer[TradeDoneCalculations]]
    val balance = (json \ "Balance").as[Double]

    (donetrades, balance)
  }
}