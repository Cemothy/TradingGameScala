package TradingGame
import de.htwg.se.TradingGame.controller.Controller
import de.htwg.se.TradingGame.view.GUI.GUI
import de.htwg.se.TradingGame.view.TUI.TUI
import scalafx.application.JFXApp3
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.InterpreterModule.given
import de.htwg.se.TradingGame.model.DataSave.TradeData


object MainClass extends JFXApp3 {
  TradeData.loadData("Z:\\SoftwareEngineering\\tradedata.xml")
  val interpreter: Interpreter = menuInterpreter
  val controller: Controller = new Controller(interpreter)

  if (interpreter.interpreterType == "BrowseInterpreter") {
    controller.setBalance(100)
  }

  val tui = new TUI(controller)
  val gui = new GUI(controller)

  override def start(): Unit = {
    new Thread {
      override def run(): Unit = {
        while (true) {
          tui.processInputLine()
        }
      }
    }.start()
  }
}
