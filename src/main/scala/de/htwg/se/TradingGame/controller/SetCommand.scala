package de.htwg.se.TradingGame.controller



import de.htwg.se.TradingGame.model.DataSave.TradeData
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.util.Command

class SetCommand(input: String, controller: Controller) extends Command {
  val interpreter = controller.interpreter

  override def doStep: Unit = {
    val result = interpreter.processInputLine(input)
    controller.interpreter = result._2
    controller.output = result._1
  }

  override def undoStep: Unit = {
    TradeData.trades.trimEnd(1)
    TradeData.donetrades.trimEnd(1)
    // Use the resetState method to revert the interpreter to its initial state
    controller.interpreter = interpreter.resetState
  }

  override def redoStep: Unit = {
    val result = interpreter.processInputLine(input)
    controller.interpreter = result._2
    controller.output = result._1
  }
}