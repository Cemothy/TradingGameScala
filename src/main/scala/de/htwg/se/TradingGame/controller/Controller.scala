package de.htwg.se.TradingGame.controller
import de.htwg.se.TradingGame.util.Observable
import de.htwg.se.TradingGame.util.UndoManager

import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.InterpreterModule.given

class Controller(using var interpreter: Interpreter) extends Observable {
  var output: String = ""
  var balance: Double = 0.0
  private val undoManager = new UndoManager

  def computeInput(input: String): Unit =
    input match {
      case "undo" => undo()
      case "redo" => redo()
      case _ => doStep(input)
    }

  def doStep(input: String): Unit = {
    // Use the interpreter here as needed, no need to check for specific type
    undoManager.doStep(new SetCommand(input, this))
    notifyObservers
  }

  def undo(): Unit = undoManager.undoStep

  def redo(): Unit = {
    undoManager.redoStep
    notifyObservers
  }

  def printDescriptor(): Unit = {
    output = interpreter.descriptor
    notifyObservers
  }
}
