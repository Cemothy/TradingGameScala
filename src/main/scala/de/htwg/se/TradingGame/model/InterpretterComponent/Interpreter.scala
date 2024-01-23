package de.htwg.se.TradingGame.model.InterpretterComponent 
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model._
import net.codingwell.scalaguice.InjectorExtensions.*

trait Interpreter {
  val actions: Map[String, String => (String, Interpreter)]
  val gameStateManager: IGameStateManager
  var descriptor: String
  def doWrongInput: String => (String, Interpreter) = input => ("Wrong input. read above for right input.", this)
  final def selectRegEx(input: String): String => (String, Interpreter) = {
    val matchingAction = actions.find { case (pattern, action) => input.matches(pattern) }
    matchingAction match {
      case Some((_, action)) => action
      case None => doWrongInput 
    }
  }
  final def processInputLine(input: String): (String, Interpreter) = {
    val result = selectRegEx(input)(input)
    descriptor = result._1
    result
  }
  def resetState: Interpreter
  val injector: Injector = Guice.createInjector(new TradingGameModule)
}