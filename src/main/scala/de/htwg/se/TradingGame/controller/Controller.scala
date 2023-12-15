package de.htwg.se.TradingGame.controller
import de.htwg.se.TradingGame.model.InterpreterComponent.Interpreter._
import de.htwg.se.TradingGame.util.Observable
import de.htwg.se.TradingGame.util.UndoManager


class Controller() extends Observable{
 var interpreter: Interpreter = createMenuInterpreter()
 var output:String = ""
 var balance:Double = 0.0
 private val undoManager = new UndoManager

  def computeInput(input:String):Unit =
    input match 
      case "undo" => undo()
      case "redo" => redo()
      case _ => doStep(input)
    
  def doStep(input:String):Unit =
    if(interpreter.isInstanceOf[MenuInterpreter]){
      balance = input.toDouble
    }
    undoManager.doStep(new SetCommand(input, this))
    notifyObservers

  def undo():Unit =
    undoManager.undoStep
  
  def redo():Unit =
    undoManager.redoStep
    notifyObservers


  def printDesctriptor(): Unit =
    output = interpreter.descriptor
    notifyObservers
    

}