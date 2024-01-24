package de.htwg.se.TradingGame.controller.Controllerimplementation

import com.google.inject._
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.controller._
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.util.UndoManager
import net.codingwell.scalaguice.InjectorExtensions._

class Controller @Inject() (var interpreter: Interpreter, private val undoManager: UndoManager) extends IController {
  var output: String = ""
  override def computeInput(input: String): Unit = 
    input match 
      case "undo" => undo()
      case "redo" => redo()
      case _ => doStep(input)

  def doStep(input: String): Unit = 
    undoManager.doStep(new SetCommand(input, this))
    output = interpreter.descriptor
    notifyObservers

  def undo(): Unit = 
    undoManager.undoStep
    notifyObservers

  def redo(): Unit = 
    undoManager.redoStep
    notifyObservers

  override def printDescriptor(): Unit = 
    output = interpreter.descriptor
    notifyObservers
}