package de.htwg.se.TradingGame.controller

import de.htwg.se.TradingGame.util.Observable
import de.htwg.se.TradingGame.util.UndoManager

import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.InterpreterModule.given


trait IController extends Observable {
  var output: String
  var balance: Double
  def computeInput(input: String): Unit
  def printDescriptor(): Unit
  def getInterpreterType: String 
  def setBalance(balance: Double): Unit
}

