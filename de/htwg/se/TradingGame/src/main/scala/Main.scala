package TradingGame
import scala.io.StdIn

object MainClass {

  def closeProgram: String = {
    println("Closing the program...")
    System.exit(0)
    "should not print"
  }

  @main def main():Unit =
    val tui = new TUI
    while(true){
      tui.readLine()
    }
}