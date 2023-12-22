package de.htwg.se.TradingGame.controller

import de.htwg.se.TradingGame.util.{Observable, UndoManager}
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.InterpreterModule.given
import com.google.inject.name.Names
import com.google.inject.{Guice, Inject, Injector}
import net.codingwell.scalaguice.InjectorExtensions.*

class Controller @Inject() (var interpreter: Interpreter) extends IController {
  var output: String = ""
  var balance: Double = 0.0
  private val undoManager = new UndoManager

  override def computeInput(input: String): Unit = {
    input match {
      case "undo" => undo()
      case "redo" => redo()
      case _ => doStep(input)
    }
  }

  def doStep(input: String): Unit = {
    undoManager.doStep(new SetCommand(input, this))
    notifyObservers
  }

  def undo(): Unit = undoManager.undoStep

  def redo(): Unit = {
    undoManager.redoStep
    notifyObservers
  }

  override def printDescriptor(): Unit = {
    output = interpreter.descriptor
    notifyObservers
  }
  override def getInterpreterType: String = interpreter.interpreterType
  
  override def setBalance(newBalance: Double): Unit = {
    this.balance = newBalance
  }
}