package de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl

import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BacktestOrLiveInterpreterSpec extends AnyFlatSpec with Matchers {
  "A BacktestOrLiveInterpreter" should "return a message and LoadorNewFileInterpreter when doBacktest is called" in {
    val gameStateManager: IGameStateManager = null // replace with a mock or a stub
    val interpreter = new BacktestOrLiveInterpreter(gameStateManager)

    val (message, returnedInterpreter) = interpreter.doBacktest("Backtest")

    message should be ("You chose Backtest")
    returnedInterpreter shouldBe a [LoadorNewFileInterpreter]
  }

  it should "return a message and itself when doLiveTrade is called" in {
    val gameStateManager: IGameStateManager = null // replace with a mock or a stub
    val interpreter = new BacktestOrLiveInterpreter(gameStateManager)

    val (message, returnedInterpreter) = interpreter.doLiveTrade("Live Trade")

    message should be ("Not yet implemented.")
    returnedInterpreter shouldBe a [BacktestOrLiveInterpreter]
  }
}