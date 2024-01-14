package de.htwg.se.TradingGame.model.InterpretterComponent 

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.model.DataSave.TradeDataclass
import de.htwg.se.TradingGame.model._
import net.codingwell.scalaguice.InjectorExtensions.*
import de.htwg.se.TradingGame.model.DataSave.TradeData

class BalanceInterpreter @Inject() extends Interpreter {

  override val descriptor: String = "What Balance do you want to start with?\n"

  val balanceInput: String = "\\d+"
  val wrongInput: String = ".*"

  def doBalance(input: String): (String, Interpreter) = 
    TradeData.balance = input.toDouble
    ("Processing balance...", ChoosePairAndDateInterpreter())

  def doWrongInput(input: String): (String, BalanceInterpreter) = ("Wrong input. Please enter a valid balance", this)

  override def resetState: Interpreter = this
  override val actions: Map[String, String => (String, Interpreter)] = Map((wrongInput, doWrongInput), (balanceInput, doBalance))
}