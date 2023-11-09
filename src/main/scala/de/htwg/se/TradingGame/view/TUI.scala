package scala.view
import scala.io
import scala.io.StdIn
import scala.controller.Controller
import scala.util.Observer
import TradingGame.main.model.* 


class TUI (controller: Controller) extends Observer{

  controller.add(this)

  

  def processInputLine():Unit =
    controller.printDesctriptor()
    controller.computeInput(StdIn.readLine())

  override def update: Unit = println(controller.output)


}