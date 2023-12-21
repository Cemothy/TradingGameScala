package de.htwg.se.TradingGame.model.DataSave

import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeDoneCalculations

import scala.collection.mutable.ArrayBuffer

object TradeData {
    val trades: ArrayBuffer[TradeComponent] = ArrayBuffer.empty[TradeComponent]
    val donetrades: ArrayBuffer[TradeDoneCalculations] = ArrayBuffer.empty[TradeDoneCalculations]
    var balance: Double = 0.0
}