package TradingGame
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class MenuInterpreterSpec extends AnyWordSpec with Matchers {
  val interpreter: Interpreter = new MenuInterpreter
  val newInterpreter: Interpreter = interpreter.processInputLine("100")._2
  val wrongInput: (String, Interpreter) = interpreter.processInputLine("something else")
  val isGameInterpreter: Boolean = newInterpreter.isInstanceOf[BrowseInterpreter]
  val wrongOutput: String = "Wrong input. Please type a number!"

  "The Interpreter should" should {
    "have the right descriptor" in {
      interpreter.descriptor should be("Welcome to TradingChampions!\n\nChoose your Starting Balance:\n")
    }
  }

  "the function processInputLine should" should {
    "return a GameInterpreter if input is 100" in {
      isGameInterpreter should be (true)
    }

    "return an Error if input is wrong" in {
      wrongInput._1 should be (wrongOutput)
      wrongInput._2 should be (interpreter)
    }
  }

}