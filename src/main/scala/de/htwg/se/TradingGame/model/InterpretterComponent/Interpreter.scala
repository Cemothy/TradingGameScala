package de.htwg.se.TradingGame.model.InterpretterComponent 
import de.htwg.se.TradingGame.model._


trait Interpreter {

  val balance: String
  val actions: Map[String, String => (String,Interpreter)]

  val descriptor: String

  final def selectRegEx (input: String): String => (String, Interpreter) =
    actions.filter(k => input.matches(k._1)).last._2

  final def processInputLine (input: String): (String, Interpreter) = selectRegEx(input)(input)
  def interpreterType: String
  def resetState: Interpreter
}