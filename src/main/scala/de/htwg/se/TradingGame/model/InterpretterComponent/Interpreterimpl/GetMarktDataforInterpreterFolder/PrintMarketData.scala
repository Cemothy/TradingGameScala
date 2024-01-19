package de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.GetMarktDataforInterpreterFolder 
import com.google.inject.Inject
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model.FileIO.TradeDataFileIO
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import de.htwg.se.TradingGame.model._

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ArrayBuffer
import scala.io.Source


class PrintMarketData(gameStateManager: IGameStateManager){

  def closeProgram: String = 
    gameStateManager.saveCurrentState()
    println(doneTradeStringwithProfit)
    System.exit(0)
    "should not print"
    
  def doneTradeStringwithProfit: String = 
    var output = ""
    for (trade <- gameStateManager.currentState.doneTrades) {
      output += "__________________________________________________________\n"
      output += s"Entry Trade: ${trade.trade.entryTrade}  |  "
      output += s"Stop Loss Trade: ${trade.trade.stopLossTrade}  |  "
      output += s"Take Profit Trade: ${trade.trade.takeProfitTrade}  |  "
      output += s"Risk Trade: ${trade.trade.risk}  |  "
      output += s"Date: ${trade.trade.datestart}  |  "
      output += s"Ticker: ${trade.trade.ticker}  |  "
      output += s"Date Trade Triggered: ${trade.dateTradeTriggered}  |  "
      output += s"Date Trade Done: ${trade.dateTradeDone}  |  "
      output += s"Trade Winner or Loser: ${trade.tradeWinOrLose}  |  "
      output += s"Trade Buy or Sell: ${if (trade.trade.takeProfitTrade > trade.trade.stopLossTrade) "Buy" else "Sell"}  |  "
      output += s"Profit: $$${trade.endProfit * gameStateManager.currentState.balance}\n"
      output += "__________________________________________________________\n"
      gameStateManager.changeBalance(gameStateManager.currentState.balance + trade.endProfit * gameStateManager.currentState.balance) 
      output += "__________________________________________________________\n"
      output += s"new Balance: $$${gameStateManager.currentState.balance}\n"
      output += "__________________________________________________________\n"
    }
    output += "__________________________________________________________\n"
    output += s"Final Balance: $$${gameStateManager.currentState.balance}\n"
    output += "__________________________________________________________\n"

    output += "Closing the program..."
    output
  
}








