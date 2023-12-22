package de.htwg.se.TradingGame.view.TUI

import scala.io.StdIn
import de.htwg.se.TradingGame.controller.IController // Import the trait
import de.htwg.se.TradingGame.util.Observer

class TUI(controller: IController) extends Observer {
  controller.add(this)

  def processInputLine(): Unit = {
    controller.printDescriptor()
    controller.computeInput(StdIn.readLine())
  }

  override def update: Unit = {
    println(controller.output)
  }
}
