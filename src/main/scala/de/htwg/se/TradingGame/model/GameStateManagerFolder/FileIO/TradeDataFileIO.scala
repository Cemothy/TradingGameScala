package de.htwg.se.TradingGame.model.FileIO

import com.google.inject.Guice
import com.google.inject.Injector
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder.GameState

trait TradeDataFileIO {
  def saveData(gameState: GameState,  filename: String): Unit
  def loadData(filename: String , gameStateManager: IGameStateManager): GameState
  val injector: Injector = Guice.createInjector(new TradingGameModule)
}
