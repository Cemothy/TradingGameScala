package de.htwg.se.TradingGame.view.GUI
import de.htwg.se.TradingGame.controller.IController
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.BacktestInterpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.BacktestOrLiveInterpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.BalanceInterpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.ChoosePairAndDateInterpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.DatabaseSelectorInterpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.LoadorNewFileInterpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.LoginInterpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.SelectLoadFileInterpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.SelectNewFileInterpreter
import de.htwg.se.TradingGame.model.InterpretterComponent._
import de.htwg.se.TradingGame.util.Observer
import de.htwg.se.TradingGame.view.GUI.Stages._
import scalafx.application.JFXApp3
import scalafx.application.Platform
import scalafx.scene.input.KeyCode.L

import Stages.BacktestEvaluationStage
import Stages.BacktestOrLiveTradeStage
import Stages.ChoosePairAndDateStage
import Stages.BalanceStage
import Stages.BacktestStage
import Stages.LoadorNewFileStage
import Stages.LoginStage
import Stages.SelectLoadFileStage
import Stages.SelectNewFileStage

class GUI(controller: IController) extends JFXApp3 with Observer {
  controller.add(this)
  var previousInterpreter: Option[Interpreter] = None
  override def update: Unit = {
    Platform.runLater {
      val currentInterpreter = controller.interpreter

      if (previousInterpreter.isEmpty || currentInterpreter.getClass != previousInterpreter.get.getClass) {
        previousInterpreter = Some(currentInterpreter)


        if (currentInterpreter.isInstanceOf[LoginInterpreter]) {
          new LoginStage(controller).createStage().show()
          previousInterpreter = Some(currentInterpreter)
        } else if (currentInterpreter.isInstanceOf[BacktestOrLiveInterpreter]) {
          new BacktestOrLiveTradeStage(controller).createStage().show()
          previousInterpreter = Some(currentInterpreter)
        } else if (currentInterpreter.isInstanceOf[LoadorNewFileInterpreter]) {
          new LoadorNewFileStage(controller).createStage().show()
          previousInterpreter = Some(currentInterpreter)
        } else if (currentInterpreter.isInstanceOf[SelectLoadFileInterpreter]) {
          new SelectLoadFileStage(controller).createStage().show()
          previousInterpreter = Some(currentInterpreter)
        } else if (currentInterpreter.isInstanceOf[SelectNewFileInterpreter]) {
          new SelectNewFileStage(controller).createStage().show()
          previousInterpreter = Some(currentInterpreter)
        } else if (currentInterpreter.isInstanceOf[ChoosePairAndDateInterpreter]) {
          new ChoosePairAndDateStage(controller).createStage().show()
          previousInterpreter = Some(currentInterpreter)
        } else if (currentInterpreter.isInstanceOf[BalanceInterpreter]) {
          new BalanceStage(controller).createStage().show()
          previousInterpreter = Some(currentInterpreter)
        } else if (currentInterpreter.isInstanceOf[BacktestInterpreter]) {
          new  BacktestStage(controller).createStage().show()
          previousInterpreter = Some(currentInterpreter)
        } else if (currentInterpreter.isInstanceOf[DatabaseSelectorInterpreter]) {
          new DatabaseSelectorStage(controller).createStage().show()
          previousInterpreter = Some(currentInterpreter)
        }
      }
    }
  }
    override def start(): Unit = {
    
  }
}
