package TradingGame.main
import scala.io.StdIn
import scala.collection.mutable.ArrayBuffer
import scala.Controller.Controller
import TradingGame.main.scala.view.TUI
object MainClass {

  



  

val controller = new Controller
val tui = new TUI(controller)

  @main def main():Unit =
    
    while(true){
      tui.processInputLine()
    }
}