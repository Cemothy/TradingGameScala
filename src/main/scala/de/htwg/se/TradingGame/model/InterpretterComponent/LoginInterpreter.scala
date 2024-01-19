package de.htwg.se.TradingGame.model.InterpretterComponent 

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.controller.GameStateManager
import de.htwg.se.TradingGame.model._
import net.codingwell.scalaguice.InjectorExtensions.*


class LoginInterpreter @Inject() (username: String, password: String, val gameStateManager: GameStateManager) extends Interpreter {
  var descriptor: String = "Please enter your username and password like: \n username password\n"

  val loginPattern: String = "\\w+ \\w+"
  val wrongInput: String = ".*"

  def doLogin(input: String): (String, Interpreter) = ("Login successful", DatabaseSelectorInterpreter(gameStateManager))

  def doWrongInput(input: String): (String, Interpreter) = ("Invalid input. Please type a username and password", this)

  override def resetState: Interpreter = 
    LoginInterpreter(username, password, gameStateManager)

  override val actions: Map[String, String => (String, Interpreter)] = Map((loginPattern, doLogin))
}