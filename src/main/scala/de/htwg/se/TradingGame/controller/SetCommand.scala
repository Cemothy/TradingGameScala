package de.htwg.se.TradingGame.controller



import de.htwg.se.TradingGame.util.Command
import de.htwg.se.TradingGame.model.GetMarketData
import de.htwg.se.TradingGame.model.BrowseInterpreter

class SetCommand(input: String, controller: Controller) extends Command {

  val interpreter = controller.interpreter

  override def doStep: Unit =  
    val result = interpreter.processInputLine(input)
    controller.interpreter = result._2
    controller.output = result._1

  override def undoStep: Unit = 
    GetMarketData.trades.trimEnd(1)
    GetMarketData.donetrades.trimEnd(1)
    controller.interpreter = new BrowseInterpreter(interpreter.balance)

  override def redoStep: Unit = 
    val result = interpreter.processInputLine(input)
    controller.interpreter = result._2
    controller.output = result._1
}

