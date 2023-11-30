package de.htwg.se.TradingGame.util

trait Command {
 def doStep:Unit
 def undoStep:Unit
 def redoStep:Unit
}
