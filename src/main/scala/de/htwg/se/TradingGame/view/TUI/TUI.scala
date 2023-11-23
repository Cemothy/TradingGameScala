package de.htwg.se.TradingGame.view.TUI
import scala.io
import scala.io.StdIn
import de.htwg.se.TradingGame.controller.Controller
import de.htwg.se.TradingGame.util.Observer
import de.htwg.se.TradingGame.model.* 


class TUI (controller: Controller) extends Observer{

  controller.add(this)

  

  def processInputLine():Unit =
    controller.printDesctriptor()
    controller.computeInput(StdIn.readLine())

  override def update: Unit = println(controller.output)


}