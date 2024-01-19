package de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl 

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.model._
import net.codingwell.scalaguice.InjectorExtensions.*
import org.checkerframework.checker.units.qual.g
import scalafx.scene.input.KeyCode.B
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter


class BalanceInterpreter @Inject() (val gameStateManager: IGameStateManager) extends Interpreter {
  var descriptor: String = "What Balance do you want to start with?\n"
  val balanceInput: String = "\\d+"
  val wrongInput: String = ".*"
  def doBalance(input: String): (String, Interpreter) = 
    gameStateManager.changeStartBalance(input.toDouble)
    gameStateManager.changeBalance(input.toDouble)
    ("Processing balance...", ChoosePairAndDateInterpreter(gameStateManager))
  def doWrongInput(input: String): (String, BalanceInterpreter) = ("Wrong input. Please enter a valid balance", this)
  override def resetState: Interpreter = BalanceInterpreter(gameStateManager)
  override val actions: Map[String, String => (String, Interpreter)] = Map( (balanceInput, doBalance))
}