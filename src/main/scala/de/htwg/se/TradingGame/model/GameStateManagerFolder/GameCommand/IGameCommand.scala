package de.htwg.se.TradingGame.model.GaneStateManagerFolder.GameCommand

import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder.GameState

trait IGameCommand{
  def execute(state: GameState): GameState
}