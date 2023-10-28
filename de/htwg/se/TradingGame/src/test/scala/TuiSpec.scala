package TradingGame
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import TradingMethods._
import java.time.LocalDate

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


  "the function processInputLine should" should {
    "set the correct interpreter" in {
      tui.interpreter.isInstanceOf[MenuInterpreter] should be (true)
      menuWrongInput = tui.processInputLine("something wrong")
      tui.interpreter.isInstanceOf[MenuInterpreter] should be (true)
      menuCorrectInput = tui.processInputLine("1000")
      tui.interpreter.isInstanceOf[BrowseInterpreter] should be (true)
      browseWrongInput = tui.processInputLine("something wrong")
      tui.interpreter.isInstanceOf[BrowseInterpreter] should be (true)
      browseCorrectInput = tui.processInputLine("TSLA")
      tui.interpreter.isInstanceOf[InvestInterpreter] should be (true)
      investWrongInput = tui.processInputLine("something wrong")
      tui.interpreter.isInstanceOf[InvestInterpreter] should be (true)
      investCorrectInput = tui.processInputLine("200")
      tui.interpreter.isInstanceOf[BrowseInterpreter] should be (true)
    }

    "return the correct String" in {
      menuWrongInput should be ("Wrong input. Please type a number!")
      menuCorrectInput should be ("")
      browseWrongInput should be ("Wrong input. Please choose from Available Symbols:\nTSLA : Tesla\nAAPL : Apple\nAMZN : Amazon\nMCD  : McDonalds\n\nto Stop : Q\n\n")
      browseCorrectInput should be (showCompany("TSLA", LocalDate.parse("2023-10-26"), 1000))
      investWrongInput should be ("Wrong input. Pleas type a numbers")
      investCorrectInput should be (currentTrade(new Trade(200,200,300,2,LocalDate.parse("2023-10-26"), "TSLA")))
    }
  }
}