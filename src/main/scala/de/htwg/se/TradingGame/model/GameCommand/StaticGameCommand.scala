package de.htwg.se.TradingGame.model.GameCommand

import _root_.de.htwg.se.TradingGame.model.GameStateFolder._

trait GameCommand{
  def execute(state: GameState): GameState
}