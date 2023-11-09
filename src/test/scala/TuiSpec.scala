package TradingGame
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import TradingMethods._
import java.time.LocalDate
import GetMarketData._
import java.io.FileInputStream


class TUISpec extends AnyWordSpec with Matchers {

  val tui = new TUI
  var menuWrongInput: String = ""
  var menuCorrectInput: String = ""
  var browseWrongInput: String = ""
  var browseCorrectInput: String = ""
  var investWrongInput: String = ""
  var investCorrectInput: String = ""
  val menuInterpreter: MenuInterpreter = new MenuInterpreter

//test for processInputLine to see if the correct interpreter is set




  "the function processInputLine should" should {
    "set the correct interpreter" in {
      tui.interpreter.isInstanceOf[MenuInterpreter] should be (true)
      menuWrongInput = tui.processInputLine("something wrong")
      tui.interpreter.isInstanceOf[MenuInterpreter] should be (true)
      menuCorrectInput = tui.processInputLine("1000")
      tui.interpreter.isInstanceOf[BrowseInterpreter] should be (true)
      browseWrongInput = tui.processInputLine("something wrong")
      tui.interpreter.isInstanceOf[BrowseInterpreter] should be (true)
      browseCorrectInput = tui.processInputLine("EURUSD 2023.08.17,23:51")
      tui.interpreter.isInstanceOf[InvestInterpreter] should be (true)
      investWrongInput = tui.processInputLine("something wrong")
      tui.interpreter.isInstanceOf[InvestInterpreter] should be (true)
      investCorrectInput = tui.processInputLine("1.00 1.00 1.00 1.00")
      tui.interpreter.isInstanceOf[BrowseInterpreter] should be (true)
    }

    "return the correct String" in {
      menuWrongInput should be ("Wrong input. Please type a number!")
      menuCorrectInput should be ("")
      browseWrongInput should be ("Wrong input. Please choose from Available Symbols: EURUSD\n\nto Stop : Q\n\n")
      browseCorrectInput should be (showCompany("EURUSD", "2023.08.17,23:51", 1000, getPriceForDateTimeDouble("2023.08.17,23:51", s"src/Symbols/EURUSD.csv", 5)))
      investWrongInput should be ("Wrong input. Pleas type a numbers")
      investCorrectInput should be (currentTrade(new Trade(1.00,1.00,1.00,1.00,"2023.08.17,23:51", "EURUSD")))
    }
  }
}