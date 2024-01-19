package de.htwg.se.TradingGame.model.FileIO

import _root_.de.htwg.se.TradingGame.TradingGameModule
import _root_.de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeDoneCalculations
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import net.codingwell.scalaguice.InjectorExtensions.*
import _root_.de.htwg.se.TradingGame.model.GameStateFolder.GameState
import scala.collection.mutable.ArrayBuffer
import _root_.de.htwg.se.TradingGame.controller.GameStateManager

trait TradeDataFileIO {
  def saveData(gameState: GameState,  filename: String): Unit
  def loadData(filename: String , gameStateManager: GameStateManager): GameState
  val injector: Injector = Guice.createInjector(new TradingGameModule)

}
