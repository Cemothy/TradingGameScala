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
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl._

class BacktestOrLiveInterpreter @Inject() (val gameStateManager: IGameStateManager)extends Interpreter {
  var descriptor: String = "Please choose: Backtest or Live Trade\n"
  val backtest: String = "Backtest"
  val liveTrade: String = "Live Trade"
  val wrongInput: String = ".*"
  def doBacktest(input: String): (String, Interpreter) = ("You chose Backtest", LoadorNewFileInterpreter(gameStateManager))
  def doLiveTrade(input: String): (String, Interpreter) = ("Not yet implemented.", BacktestOrLiveInterpreter(gameStateManager))
  override def resetState: Interpreter = BacktestOrLiveInterpreter(gameStateManager)
  override val actions: Map[String, String => (String, Interpreter)] = Map( (backtest, doBacktest), (liveTrade, doLiveTrade))
}