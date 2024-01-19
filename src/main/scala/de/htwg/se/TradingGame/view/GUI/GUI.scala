package de.htwg.se.TradingGame.view.GUI
import de.htwg.se.TradingGame.controller.IController
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.model.InterpretterComponent._
import de.htwg.se.TradingGame.util.Observer
import de.htwg.se.TradingGame.view.GUI.Stages._
import scalafx.application.JFXApp3
import scalafx.application.Platform

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
  val loginstage = new LoginStage(controller)
  val backtestOrLiveTradeStage = new BacktestOrLiveTradeStage(controller)
  val loadoeNewFileStage = new LoadorNewFileStage(controller)
  val selectLoadFileStage = new SelectLoadFileStage(controller)
  val selectNewFileStage = new SelectNewFileStage(controller)
  val choosepairandDateStage = new ChoosePairAndDateStage(controller)
  val balancestage = new BalanceStage(controller)
  //val backtestStage = new BacktestStage(controller)
  val DatabaseSelectorStage = new DatabaseSelectorStage(controller)
  val backtestevaluationStage = new BacktestEvaluationStage(controller)
 override def update: Unit = {
  Platform.runLater {
    if(controller.interpreter.isInstanceOf[LoginInterpreter]){
      new LoginStage(controller).createStage().show()
    } else if(controller.interpreter.isInstanceOf[BacktestOrLiveInterpreter]){
      new BacktestOrLiveTradeStage(controller).createStage().show()
    }else if(controller.interpreter.isInstanceOf[LoadorNewFileInterpreter]){
      new LoadorNewFileStage(controller).createStage().show()
    }else if(controller.interpreter.isInstanceOf[SelectLoadFileInterpreter]){
      new SelectLoadFileStage(controller).createStage().show()
    }else if(controller.interpreter.isInstanceOf[SelectNewFileInterpreter]){
      new SelectNewFileStage(controller).createStage().show()
    }else if(controller.interpreter.isInstanceOf[ChoosePairAndDateInterpreter]){
      new ChoosePairAndDateStage(controller).createStage().show()
    }else if(controller.interpreter.isInstanceOf[BalanceInterpreter]){
      new BalanceStage(controller).createStage().show()
    }else if(controller.interpreter.isInstanceOf[BacktestInterpreter]){
      new BacktestStage(controller).createStage().show()
    }else if(controller.interpreter.isInstanceOf[DatabaseSelectorInterpreter]){
      new DatabaseSelectorStage(controller).createStage().show()
    }
    // }else if(controller.interpreter.isInstanceOf[BacktestEvaluationInterpreter]){
    //   new BacktestEvaluationStage(controller).createStage().show()
    // }
  }
}
    override def start(): Unit = {
       loginstage.createStage().show()
  }
}
