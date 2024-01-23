package de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateManagerImplementation

import de.htwg.se.TradingGame.model.FileIO.TradeDataFileIO
import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder.GameState
import de.htwg.se.TradingGame.model.GaneStateManagerFolder.GameCommand.IGameCommand
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameStateManagerSpec extends AnyWordSpec with Matchers {
  "A GameStateManager" when {
    "executing a command" should {
      "change the current state" in {
        val fileIO = mock(classOf[TradeDataFileIO])
        val gameStateManager = new GameStateManager(fileIO)
        val command = mock(classOf[IGameCommand])
        val newState = mock(classOf[GameState])
        when(command.execute(gameStateManager.currentState)).thenReturn(newState)

        gameStateManager.executeCommand(command)

        gameStateManager.currentState shouldBe newState
      }
    }
    "changing the balance" should {
      "execute a ChangeBalanceCommand" in {
        val fileIO = mock(classOf[TradeDataFileIO])
        val gameStateManager = new GameStateManager(fileIO)
        gameStateManager.changeBalance(1.0)
        gameStateManager.currentState.balance shouldBe 1.0

      }
    }
    "changing the backtest date" should {
    "execute a ChangeBacktestDateCommand" in {
      val fileIO = mock(classOf[TradeDataFileIO])
      val gameStateManager = new GameStateManager(fileIO)
      gameStateManager.changeBacktestDate(3L)
      gameStateManager.currentState.backtestDate shouldBe 3L
    }
  }

    "changing the start balance" should {
    "execute a ChangeStartBalanceCommand" in {
      val fileIO = mock(classOf[TradeDataFileIO])
      val gameStateManager = new GameStateManager(fileIO)
      gameStateManager.changeStartBalance(3.0)
      gameStateManager.currentState.startbalance shouldBe 3.0
    }
  }
    "changing the pair" should {
    "execute a ChangePairCommand" in {
      val fileIO = mock(classOf[TradeDataFileIO])
      val gameStateManager = new GameStateManager(fileIO)
      gameStateManager.changePair("EURUSD")
      gameStateManager.currentState.pair shouldBe "EURUSD"
    }
  }

    "changing the save name" should {
    "execute a ChangeSaveNameCommand" in {
      val fileIO = mock(classOf[TradeDataFileIO])
      val gameStateManager = new GameStateManager(fileIO)
      gameStateManager.changeSaveName("save")
      gameStateManager.currentState.savename shouldBe "save"
    }
}
  
    "changing the end date" should {
    "execute a ChangeEndDateCommand" in {
      val fileIO = mock(classOf[TradeDataFileIO])
      val gameStateManager = new GameStateManager(fileIO)
      gameStateManager.changeEndDate(23L)

      gameStateManager.currentState.endDate shouldBe 23L
    }
  }

    "changing the start date" should {
    "execute a ChangeStartDateCommand" in {
      val fileIO = mock(classOf[TradeDataFileIO])
      val gameStateManager = new GameStateManager(fileIO)
      gameStateManager.changeStartDate(23L)

      gameStateManager.currentState.startDate shouldBe 23L
    }
}
    
    "changing the database connection string" should {
    "execute a ChangeDatabaseConnectionStringCommand" in {
    val fileIO = mock(classOf[TradeDataFileIO])
    val gameStateManager = new GameStateManager(fileIO)
    gameStateManager.changeDatabaseConnectionString("connection")

    gameStateManager.currentState.databaseConnectionString shouldBe "connection"
    }
}

    "changing the distance candles" should {
    "execute a ChangeDistanceCandlesCommand" in {
      val fileIO = mock(classOf[TradeDataFileIO])
      val gameStateManager = new GameStateManager(fileIO)
      gameStateManager.changeDistanceCandles(23)

      gameStateManager.currentState.distancecandles shouldBe 23
    }
}

    "changing the interval" should {
    "execute a ChangeIntervalCommand" in {
      val fileIO = mock(classOf[TradeDataFileIO])
      val gameStateManager = new GameStateManager(fileIO)
      gameStateManager.changeInterval("interval")

      gameStateManager.currentState.interval shouldBe "interval"
    }
}

    "changing the pair list" should {
    "execute a ChangePairListCommand" in {
      val fileIO = mock(classOf[TradeDataFileIO])
      val gameStateManager = new GameStateManager(fileIO)
      gameStateManager.changePairList(List("EURUSD"))

      gameStateManager.currentState.pairList shouldBe List("EURUSD")
    }
}

    "changing the load file list" should {
    "execute a ChangeLoadFileListCommand" in {
      val fileIO = mock(classOf[TradeDataFileIO])
      val gameStateManager = new GameStateManager(fileIO)
      gameStateManager.changeLoadFileList(List("EURUSD"))

      gameStateManager.currentState.loadFileList shouldBe List("EURUSD")
    }
}

    "changing the trades" should {
    "execute a ChangeTradesCommand" in {
      val fileIO = mock(classOf[TradeDataFileIO])
      val gameStateManager = new GameStateManager(fileIO)
      gameStateManager.changeTrades(scala.collection.mutable.ArrayBuffer.empty)

      gameStateManager.currentState.trades shouldBe scala.collection.mutable.ArrayBuffer.empty
    }
}

    "changing the done trades" should {
    "execute a ChangeDoneTradesCommand" in {
      val fileIO = mock(classOf[TradeDataFileIO])
      val gameStateManager = new GameStateManager(fileIO)
      gameStateManager.changeDoneTrades(scala.collection.mutable.ArrayBuffer.empty)

      gameStateManager.currentState.doneTrades shouldBe scala.collection.mutable.ArrayBuffer.empty
    }
}
    
        "getting the current state" should {
        "return the current state" in {
        val fileIO = mock(classOf[TradeDataFileIO])
        val gameStateManager = new GameStateManager(fileIO)
        gameStateManager.currentState = mock(classOf[GameState])
    
        gameStateManager.getCurrentState shouldBe gameStateManager.currentState
        }
    }

    "loading the current state" should {
    "load the current state" in {
      val fileIO = mock(classOf[TradeDataFileIO])
      val gameStateManager = new GameStateManager(fileIO)
      val gameState = mock(classOf[GameState])
      gameStateManager.currentState = gameState
      when(gameState.savename).thenReturn("save")
      when(fileIO.loadData("save", gameStateManager)).thenReturn(gameState)

      gameStateManager.loadCurrentState()

      gameStateManager.currentState shouldBe gameState
    }
}
    
        "saving the current state" should {
        "save the current state" in {
        val fileIO = mock(classOf[TradeDataFileIO])
        val gameStateManager = new GameStateManager(fileIO)
        val gameState = mock(classOf[GameState])
        gameStateManager.currentState = gameState
        when(gameState.savename).thenReturn("save")
    
        gameStateManager.saveCurrentState()
    
        verify(fileIO).saveData(gameState, "save")
        }
    }

//test
  }
}