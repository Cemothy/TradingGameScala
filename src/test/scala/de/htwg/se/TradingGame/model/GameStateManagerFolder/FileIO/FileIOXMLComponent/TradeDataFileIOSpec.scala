package de.htwg.se.TradingGame.model.GameStateManagerFolder.FileIO.FileIOXMLComponent

import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder.DefaultGameStateimpl.DefaultGameState
import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder.GameState
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TradeDataXMLFileIOTest extends AnyWordSpec with Matchers {
  "A TradeDataXMLFileIO" when {
    "new" should {
      val fileIO = new TradeDataXMLFileIO
      val gameState = new DefaultGameState

      "save and load a GameState correctly" in {
        fileIO.saveData(gameState, "testFile")
        val loadedGameState = fileIO.loadData("testFile", null)
        loadedGameState shouldEqual gameState
      }
    }
  }
}