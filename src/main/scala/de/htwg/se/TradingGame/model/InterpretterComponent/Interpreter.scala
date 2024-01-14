package de.htwg.se.TradingGame.model.InterpretterComponent 
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.model._
import net.codingwell.scalaguice.InjectorExtensions.*

trait Interpreter {
  val actions: Map[String, String => (String,Interpreter)]
  val descriptor: String
  final def selectRegEx (input: String): String => (String, Interpreter) = actions.filter(k => input.matches(k._1)).last._2
  final def processInputLine (input: String): (String, Interpreter) = selectRegEx(input)(input)
  def resetState: Interpreter
  val injector: Injector = Guice.createInjector(new TradingGameModule)
}