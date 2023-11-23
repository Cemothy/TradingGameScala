package TradingGame
import scala.io.StdIn
import scala.collection.mutable.ArrayBuffer
import de.htwg.se.TradingGame.controller.Controller
import de.htwg.se.TradingGame.view.TUI.TUI

object MainClass {

val controller = new Controller
val tui = new TUI(controller)

  @main def main():Unit =
    
    while(true){
      tui.processInputLine()
    }
}