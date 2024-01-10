package de.htwg.se.TradingGame.model.InterpretterComponent 
import de.htwg.se.TradingGame.model._
import com.google.inject.name.Names
import com.google.inject.{Guice, Inject, Injector}
import net.codingwell.scalaguice.InjectorExtensions.*
import de.htwg.se.TradingGame.TradingGameModule

trait Interpreter {

  val balance: String
  val actions: Map[String, String => (String,Interpreter)]

  val descriptor: String

  final def selectRegEx (input: String): String => (String, Interpreter) =
    actions.filter(k => input.matches(k._1)).last._2

  final def processInputLine (input: String): (String, Interpreter) = selectRegEx(input)(input)
  def interpreterType: String
  def resetState: Interpreter
  val injector: Injector = Guice.createInjector(new TradingGameModule)
}