package de.htwg.se.TradingGame

import com.google.inject.AbstractModule
import com.google.inject.TypeLiteral
import de.htwg.se.TradingGame.controller.Controller
import de.htwg.se.TradingGame.controller.GameStateManager
import de.htwg.se.TradingGame.controller.IController
import de.htwg.se.TradingGame.model.FileIO.TradeDataFileIO
import de.htwg.se.TradingGame.model.FileIO.TradeDataXMLFileIO
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.LoginInterpreter
import de.htwg.se.TradingGame.util
import de.htwg.se.TradingGame.util.Observable
import de.htwg.se.TradingGame.util.UndoManager
import net.codingwell.scalaguice.ScalaModule
import com.google.inject.Provider
import com.google.inject.Inject

class GameStateManagerProvider @Inject() (fileIO: TradeDataFileIO) extends Provider[GameStateManager] {
  override def get(): GameStateManager = {
    new GameStateManager(fileIO)
  }
}

class TradingGameModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[IController](new TypeLiteral[IController] {}).to(classOf[Controller])
    bind[Interpreter](new TypeLiteral[Interpreter] {}).to(classOf[LoginInterpreter])
    bind[TradeDataFileIO](new TypeLiteral[TradeDataFileIO] {}).to(classOf[TradeDataXMLFileIO])
    bind[GameStateManager].toProvider(classOf[GameStateManagerProvider])
  }
}