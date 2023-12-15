package de.htwg.se.TradingGame.model

trait GetMarketDataTrait {
  def isDateInFile(dateTime: String, dataFilePath: String): Boolean
  def nextPossibleDateinFile(dateTime: String, dataFilePath: String): String
  def getLastDateofFile(dataFilePath: String): String
  def getPriceForDateTimeDouble(dateTime: String, dataFilePath: String, column: Int): Option[Double]
  def doneTradeStringwithProfit: String
  def closeProgram: String
}
