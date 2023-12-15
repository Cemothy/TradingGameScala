package de.htwg.se.TradingGame.model

trait FileChooserTrait {
  def getSymbolPath(ticker: String): String
}