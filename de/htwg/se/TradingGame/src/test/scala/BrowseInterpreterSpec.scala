package TradingGame
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class BrowseInterpreterSpec extends AnyWordSpec with Matchers {
  val interpreter: Interpreter = new BrowseInterpreter("1000")
  val newInterpreter: Interpreter = interpreter.processInputLine("TSLA")._2
  val wrongInput: (String, Interpreter) = interpreter.processInputLine("something else")
  val isGameInterpreter: Boolean = newInterpreter.isInstanceOf[InvestInterpreter]
  val wrongOutput: String = "Wrong input. Please choose from Available Symbols:\nTSLA : Tesla\nAAPL : Apple\nAMZN : Amazon\nMCD  : McDonalds\n\nto Stop : Q\n\n"

  "The Interpreter should" should {
    "have the right descriptor" in {
        
      interpreter.descriptor should be("Please Enter the Tickersymbol of your choice:\nAvailable Symbols:\nTSLA : Tesla\nAAPL : Apple\nAMZN : Amazon\nMCD  : McDonalds\n\nto Stop : Q\n\n")
    }
  }

  "the function processInputLine should" should {
    "return a GameInterpreter if input is TSLA" in {
      isGameInterpreter should be (true)
    }

    "return an Error if input is wrong" in {
      wrongInput._1 should be (wrongOutput)
      wrongInput._2 should be (interpreter)
    }
  }

}