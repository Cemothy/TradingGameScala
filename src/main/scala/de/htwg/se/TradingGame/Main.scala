package TradingGame
import de.htwg.se.TradingGame.controller.Controller
import de.htwg.se.TradingGame.view.GUI.BacktestStage
import de.htwg.se.TradingGame.view.GUI.GUI
import de.htwg.se.TradingGame.view.TUI.TUI
import scalafx.application.JFXApp3

import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn

object MainClass extends JFXApp3 {


 

  val controller = new Controller()
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