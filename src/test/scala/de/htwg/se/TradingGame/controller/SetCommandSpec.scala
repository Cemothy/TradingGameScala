package de.htwg.se.TradingGame.controller

import de.htwg.se.TradingGame.controller.Controllerimplementation.Controller
import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder.GameState
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.Trade
import de.htwg.se.TradingGame.util.Command
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito._
import org.mockito.MockitoAnnotations
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable

class SetCommandSpec extends AnyFlatSpec with Matchers {
  @Mock var controller: IController = _
  @Mock var interpreter: Interpreter = _

  override def withFixture(test: NoArgTest) = { // Define a setup method
    MockitoAnnotations.openMocks(this) // Initialize the mocks
    super.withFixture(test) // Continue with the test
  }

  "A SetCommand" should "do step correctly" in {
    when(controller.interpreter).thenReturn(interpreter)
    when(interpreter.processInputLine(any[String])).thenReturn(("output", interpreter))

    val command = new SetCommand("input", controller)
    command.doStep

    verify(interpreter).processInputLine("input")
    verify(controller).interpreter = interpreter
    verify(controller).output = "output"
  }


  it should "redo step correctly" in {
    when(controller.interpreter).thenReturn(interpreter)
    when(interpreter.processInputLine(any[String])).thenReturn(("output", interpreter))

    val command = new SetCommand("input", controller)
    command.redoStep

    verify(interpreter).processInputLine("input")
    verify(controller).interpreter = interpreter
    verify(controller).output = "output"
  }

}