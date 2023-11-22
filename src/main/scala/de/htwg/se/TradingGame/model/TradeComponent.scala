package de.htwg.se.TradingGame.model

trait TradeComponent {
  def entryTrade: Double
  def stopLossTrade: Double
  def takeProfitTrade: Double
  def risk: Double
  def datestart: String
  def ticker: String
}
