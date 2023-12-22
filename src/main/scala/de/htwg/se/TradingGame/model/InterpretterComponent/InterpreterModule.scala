package de.htwg.se.TradingGame.model.InterpretterComponent

object InterpreterModule {
  given menuInterpreter: Interpreter = MenuInterpreter()
  given browseInterpreter: Interpreter = BrowseInterpreter("100")
  given investInterpreter: Interpreter = InvestInterpreter("EURUSD", "2023.05.05,12:12", "100")
}
