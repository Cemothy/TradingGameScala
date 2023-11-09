package de.htwg.se.TradingGame.main.scala.controller
import de.htwg.se.TradingGame.main.scala.model.{Interpreter, MenuInterpreter}
import de.htwg.se.TradingGame.main.scala.util.Observable

class Controller(var grid:Grid) extends Observable{
 var interpreter: Interpreter = new MenuInterpreter
 var output:String = ""

  def computeInput(input:String):String =
    val result = interpreter.processInputLine(input)
    interpreter = result._2
    output = result._1
    notifyObservers()

  def printDesctriptor(): Unit =
    output = interpreter.descriptor
    notifyObservers()
    

}