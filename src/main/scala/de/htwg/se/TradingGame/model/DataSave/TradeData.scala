package de.htwg.se.TradingGame.model.DataSave

import de.htwg.se.TradingGame.model.TradeDecoratorPattern.{TradeComponent, TradeDoneCalculations}
import scala.xml.{Elem, Node, XML}
import de.htwg.se.TradingGame.model.FileIO.TradeDataXMLFileIO
import scala.collection.mutable.ArrayBuffer

object TradeData {
  val trades: ArrayBuffer[TradeComponent] = ArrayBuffer.empty[TradeComponent]
  val donetrades: ArrayBuffer[TradeDoneCalculations] = ArrayBuffer.empty[TradeDoneCalculations]
  var balance: Double = 0.0

  def loadData(filename: String): Unit = {
    val tradeDataXMLFileIO = new TradeDataXMLFileIO(filename)
    val (loadedDoneTrades, loadedBalance) = tradeDataXMLFileIO.loadData()

    donetrades.clear()
    donetrades ++= loadedDoneTrades
    balance = loadedBalance
  }
}