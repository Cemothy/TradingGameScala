package de.htwg.se.TradingGame.controller
import de.htwg.se.TradingGame.model.{Interpreter, MenuInterpreter}
import de.htwg.se.TradingGame.util.Observable

class Controller() extends Observable{
 var interpreter: Interpreter = new MenuInterpreter
 var output:String = ""

  def computeInput(input:String):Unit =
    val result = interpreter.processInputLine(input)
    interpreter = result._2
    output = result._1
    notifyObservers

  def printDesctriptor(): Unit =
    output = interpreter.descriptor
    notifyObservers
    

}