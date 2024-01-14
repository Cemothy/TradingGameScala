package de.htwg.se.TradingGame.model.InterpretterComponent


import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.model.DataSave.TradeDataclass
import de.htwg.se.TradingGame.model._
import net.codingwell.scalaguice.InjectorExtensions.*

class BacktestOrLiveInterpreter @Inject() extends Interpreter {

  override val descriptor: String = "Please choose: Backtest or Live Trade\n"

  val backtest: String = "Backtest"
  val liveTrade: String = "Live Trade"
  val wrongInput: String = ".*"

  def doBacktest(input: String): (String, Interpreter) = ("You chose Backtest", LoadorNewFileInterpreter())
  
  def doLiveTrade(input: String): (String, Interpreter) = ("Not yet implemented.", this)
  
  def doWrongInput(input: String): (String, BacktestOrLiveInterpreter) = ("Wrong input. Please choose: Backtest or Live Trade", this)
  override def resetState: Interpreter = this
  override val actions: Map[String, String => (String, Interpreter)] = Map((wrongInput, doWrongInput), (backtest, doBacktest), (liveTrade, doLiveTrade))
}