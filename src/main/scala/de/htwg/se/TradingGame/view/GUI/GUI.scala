package de.htwg.se.TradingGame.view.GUI

import de.htwg.se.TradingGame.controller.Controller
import de.htwg.se.TradingGame.model.{MenuInterpreter, BrowseInterpreter}
import de.htwg.se.TradingGame.util.Observer
import scalafx.application.JFXApp3
import scalafx.application.Platform

class GUI (controller: Controller) extends JFXApp3 with Observer {
  controller.add(this)

  val balanceStage = new BalanceStage(controller)
  var backtestStage: Option[BacktestStage] = None
  var previousInterpreter: Option[Any] = None

  override def update: Unit = {
    Platform.runLater {
      if(controller.interpreter.isInstanceOf[MenuInterpreter]){
        balanceStage.createStage().show()
      }
      else if(controller.interpreter.isInstanceOf[BrowseInterpreter] && previousInterpreter.exists(_.isInstanceOf[MenuInterpreter])){
        backtestStage = Some(new BacktestStage(controller))
        backtestStage.foreach(_.createStage().show())
      }
      previousInterpreter = Some(controller.interpreter)
    }
  }

  override def start(): Unit = {
  }
}