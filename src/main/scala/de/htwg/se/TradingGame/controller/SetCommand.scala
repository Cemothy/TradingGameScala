package de.htwg.se.TradingGame.controller

import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.util.Command

import Controllerimplementation.Controller

class SetCommand(input: String, controller: IController) extends Command {
  val interpreter = controller.interpreter
  override def doStep: Unit = 
    val result = interpreter.processInputLine(input)
    controller.interpreter = result._2
    controller.output = result._1
  override def undoStep: Unit = 
    controller.interpreter.gameStateManager.currentState.trades.trimEnd(1)
    controller.interpreter.gameStateManager.currentState.doneTrades.trimEnd(1)
    controller.interpreter = interpreter.resetState
  override def redoStep: Unit = 
    val result = interpreter.processInputLine(input)
    controller.interpreter = result._2
    controller.output = result._1
}