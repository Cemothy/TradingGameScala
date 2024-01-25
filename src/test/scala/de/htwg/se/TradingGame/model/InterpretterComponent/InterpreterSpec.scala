package de.htwg.se.TradingGame.model.InterpretterComponent

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager

class InterpreterSpec extends AnyFlatSpec with Matchers {
  "An Interpreter" should "return a message and itself when doWrongInput is called" in {
    val interpreter = new Interpreter {
      override val actions: Map[String, String => (String, Interpreter)] = Map.empty
      override val gameStateManager: IGameStateManager = null
      var descriptor: String = ""
      override def resetState: Interpreter = this
    }

    val (message, returnedInterpreter) = interpreter.doWrongInput("any input")

    message should be ("Wrong input. read above for right input.")
    returnedInterpreter should be (interpreter)
  }

  it should "return a message and itself when processInputLine is called with an input that doesn't match any action" in {
    val interpreter = new Interpreter {
      override val actions: Map[String, String => (String, Interpreter)] = Map.empty
      override val gameStateManager: IGameStateManager = null
      var descriptor: String = ""
      override def resetState: Interpreter = this
    }

    val (message, returnedInterpreter) = interpreter.processInputLine("any input")

    message should be ("Wrong input. read above for right input.")
    returnedInterpreter should be (interpreter)
  }
}