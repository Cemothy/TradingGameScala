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
        loginstage.createStage().show()
      } else if(controller.interpreter.isInstanceOf[BacktestOrLiveInterpreter]){
        backtestOrLiveTradeStage.createStage().show()
      }else if(controller.interpreter.isInstanceOf[LoadorNewFileInterpreter]){
        loadoeNewFileStage.createStage().show()
      }else if(controller.interpreter.isInstanceOf[SelectLoadFileInterpreter]){
        selectLoadFileStage.createStage().show()
      }else if(controller.interpreter.isInstanceOf[SelectNewFileInterpreter]){
        selectNewFileStage.createStage().show()
      }else if(controller.interpreter.isInstanceOf[ChoosePairAndDateInterpreter]){
        choosepairandDateStage.createStage().show()
      }else if(controller.interpreter.isInstanceOf[BalanceInterpreter]){
        balancestage.createStage().show()
      }else if(controller.interpreter.isInstanceOf[BacktestInterpreter]){
        new BacktestStage(controller).createStage().show()
      }else if(controller.interpreter.isInstanceOf[DatabaseSelectorInterpreter]){
        DatabaseSelectorStage.createStage().show()
      }
      // }else if(controller.interpreter.isInstanceOf[BacktestEvaluationInterpreter]){
      //   backtestevaluationStage.createStage().show()
      // }
    }
  }
    override def start(): Unit = {
       loginstage.createStage().show()
  }
}
