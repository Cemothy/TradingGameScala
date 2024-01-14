package de.htwg.se.TradingGame.model.FileIO

import _root_.de.htwg.se.TradingGame.TradingGameModule
import _root_.de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeDoneCalculations
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import net.codingwell.scalaguice.InjectorExtensions.*

import scala.collection.mutable.ArrayBuffer

trait TradeDataFileIO {
  def saveData(donetrades: ArrayBuffer[TradeDoneCalculations], balance: Double, pair: String, backtestDate: Long, filename: String): Unit
  def loadData(filename: String): (ArrayBuffer[TradeDoneCalculations], Double, String, Long)
    val injector: Injector = Guice.createInjector(new TradingGameModule)

}
