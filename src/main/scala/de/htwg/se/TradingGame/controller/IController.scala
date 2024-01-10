package de.htwg.se.TradingGame.controller

import de.htwg.se.TradingGame.util.Observable
import de.htwg.se.TradingGame.util.UndoManager

import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import com.google.inject.name.Names
import com.google.inject.{Guice, Inject, Injector}
import net.codingwell.scalaguice.InjectorExtensions.*
import de.htwg.se.TradingGame.TradingGameModule


trait IController extends Observable {
  var output: String
  var balance: Double
  def computeInput(input: String): Unit
  def printDescriptor(): Unit
  def getInterpreterType: String 
  def setBalance(balance: Double): Unit
  val injector: Injector = Guice.createInjector(new TradingGameModule)
}

