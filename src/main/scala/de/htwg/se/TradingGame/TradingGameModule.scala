package de.htwg.se.TradingGame

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import de.htwg.se.TradingGame.controller.IController
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.model.FileIO.TradeDataXMLFileIO
import de.htwg.se.TradingGame.model.FileIO.TradeDataJSONFileIO
import de.htwg.se.TradingGame.util.{Observable, UndoManager}
import de.htwg.se.TradingGame.util
import de.htwg.se.TradingGame.model.FileIO.TradeDataFileIO
import com.google.inject.TypeLiteral
import de.htwg.se.TradingGame.controller.Controller
import de.htwg.se.TradingGame.model.InterpretterComponent.MenuInterpreter

class TradingGameModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[IController](new TypeLiteral[IController] {}).to(classOf[Controller])
    bind[Interpreter](new TypeLiteral[Interpreter] {}).to(classOf[MenuInterpreter])
    //bind[TradeDataFileIO](new TypeLiteral[TradeDataFileIO] {}).to(classOf[TradeDataXMLFileIO])
    bind[TradeDataFileIO](new TypeLiteral[TradeDataFileIO] {}).to(classOf[TradeDataJSONFileIO])
  }
}
