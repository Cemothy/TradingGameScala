package de.htwg.se.TradingGame

import com.google.inject.Guice
import de.htwg.se.TradingGame.controller.IController
import de.htwg.se.TradingGame.model.DataSave.TradeDataclass
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.view.GUI.GUI
import de.htwg.se.TradingGame.view.TUI.TUI
import scalafx.application.JFXApp3

object Main extends JFXApp3 {
  val injector = Guice.createInjector(new TradingGameModule)
  val controller: IController = injector.getInstance(classOf[IController])
  val interpreter = injector.getInstance(classOf[Interpreter])
  val tradeData = injector.getInstance(classOf[TradeDataclass])

  tradeData.loadData("TradeData")
  if (interpreter.interpreterType == "BrowseInterpreter") {
    controller.setBalance(100)
  }

  val tui = new TUI(controller)
  val gui = new GUI(controller)

  override def start(): Unit = {
    new Thread {
      override def run(): Unit = {
        while (true) {
          tui.processInputLine()
        }
      }
    }.start()
  }
}