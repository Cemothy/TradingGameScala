package de.htwg.se.TradingGame.view.GUI
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.util.Observer
import scalafx.application.JFXApp3
import scalafx.application.Platform
import de.htwg.se.TradingGame.controller.IController

class GUI(controller: IController) extends JFXApp3 with Observer {
  controller.add(this)

  var balanceStage:  Option[BalanceStage] = None
  var backtestStage: Option[BacktestStage] = None
  var previousInterpreter: Option[String] = None

  override def update: Unit = {
  Platform.runLater {
    controller.getInterpreterType match {
      case "MenuInterpreter" =>
        if (previousInterpreter.exists(_ == "BrowseInterpreter")) {
          balanceStage = Some(new BalanceStage(controller))
          balanceStage.foreach(_.createStage().show())
        }

      case "BrowseInterpreter" =>
        if (previousInterpreter.exists(_ == "MenuInterpreter")) {
          backtestStage = Some(new BacktestStage(controller))
          backtestStage.foreach(_.createStage().show())
        }

      case _ => // handle other cases if necessary
    }
    previousInterpreter = Some(controller.getInterpreterType)
  }
}

  override def start(): Unit = {
    // Initialization or other startup logic
  }
}

