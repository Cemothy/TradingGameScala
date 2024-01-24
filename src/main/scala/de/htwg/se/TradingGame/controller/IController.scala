package de.htwg.se.TradingGame.controller

import com.google.inject.Guice
import com.google.inject.Injector
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.util.Observable
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager

trait IController extends Observable {
  var output: String
  var interpreter: Interpreter
  def computeInput(input: String): Unit
  def printDescriptor(): Unit
  val injector: Injector = Guice.createInjector(new TradingGameModule)
}