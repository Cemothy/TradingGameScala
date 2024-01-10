package de.htwg.se.TradingGame.model.FileIO

import _root_.de.htwg.se.TradingGame.TradingGameModule
import scala.collection.mutable.ArrayBuffer
import com.google.inject.name.Names
import com.google.inject.{Guice, Inject, Injector}
import net.codingwell.scalaguice.InjectorExtensions.*
import _root_.de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeDoneCalculations

trait TradeDataFileIO {
  def saveData(donetrades: ArrayBuffer[TradeDoneCalculations], balance: Double, filename: String): Unit
  def loadData(filename: String): (ArrayBuffer[TradeDoneCalculations], Double)
    val injector: Injector = Guice.createInjector(new TradingGameModule)

}
