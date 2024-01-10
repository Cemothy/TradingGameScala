package de.htwg.se.TradingGame.model.DataSave

import com.google.inject.Inject
import de.htwg.se.TradingGame.model.FileIO.TradeDataFileIO
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeDoneCalculations

import scala.collection.mutable.ArrayBuffer
import scala.xml.Elem
import scala.xml.Node
import scala.xml.XML

object TradeData {
  val trades: ArrayBuffer[TradeComponent] = ArrayBuffer.empty[TradeComponent]
  val donetrades: ArrayBuffer[TradeDoneCalculations] = ArrayBuffer.empty[TradeDoneCalculations]
  var balance: Double = 0.0
}
class TradeDataclass @Inject() (tradeDataFileIO: TradeDataFileIO) {

  def loadData(filename: String): Unit = {
    val (loadedDoneTrades, loadedBalance) = tradeDataFileIO.loadData(filename)

    TradeData.donetrades.clear()
    TradeData.donetrades ++= loadedDoneTrades
    TradeData.balance = loadedBalance
  }

  def saveData(filename: String): Unit = {
    tradeDataFileIO.saveData(TradeData.donetrades, TradeData.balance, filename)
  }
}
