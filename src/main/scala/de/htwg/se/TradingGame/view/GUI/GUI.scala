package de.htwg.se.TradingGame.view.GUI
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.controller.Controller
import de.htwg.se.TradingGame.util.Observer
import scalafx.application.JFXApp3
import scalafx.application.Platform

class GUI(controller: Controller) extends JFXApp3 with Observer {
  controller.add(this)

  val balanceStage = new BalanceStage(controller)
  var backtestStage: Option[BacktestStage] = None
  var previousInterpreter: Option[Interpreter] = None  // Changed type to Option[Interpreter]

  override def update: Unit = {
    Platform.runLater {
      controller.interpreter.interpreterType match {
        case "MenuInterpreter" =>
          balanceStage.createStage().show()

        case "BrowseInterpreter" if previousInterpreter.exists(_.interpreterType == "MenuInterpreter") =>
          backtestStage = Some(new BacktestStage(controller))
          backtestStage.foreach(_.createStage().show())

        case _ => // handle other cases if necessary
      }
      previousInterpreter = Some(controller.interpreter)
    }
  }

  override def start(): Unit = {
    // Initialization or other startup logic
  }
}
