package TradingGame
import de.htwg.se.TradingGame.controller.Controller
import de.htwg.se.TradingGame.view.TUI.TUI

import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn
import de.htwg.se.TradingGame.view.GUI.GUI
import de.htwg.se.TradingGame.view.GUI.BalanceStage

object MainClass {

val controller = new Controller
val tui = new TUI(controller)
 val gui = new BalanceStage(controller)

  @main def main():Unit =
    
    while(true){
      tui.processInputLine()
    }
}