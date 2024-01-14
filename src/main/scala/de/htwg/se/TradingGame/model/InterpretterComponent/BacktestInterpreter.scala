package de.htwg.se.TradingGame.model.InterpretterComponent 

import com.google.inject.Inject
import de.htwg.se.TradingGame.Main.tradeData
import de.htwg.se.TradingGame.model.DataSave.TradeData._
import de.htwg.se.TradingGame.model.DataSave.TradeDataclass
import de.htwg.se.TradingGame.model.GetMarketDataComponent.GetMarketData._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._

import java.io.File
import java.time.LocalDate

class BacktestInterpreter @Inject()  extends Interpreter {

  override val descriptor: String = "Please Enter the Tickersymbol of your choice: EURUSD Date: YYYY.MM.DD,HH:MM \n\nto Stop : Q\n\n"

  val quit: String = "Q"
  val tickersymbol: String = "EURUSD [0-9]{4}.[0-9]{2}.[0-9]{2},[0-9]{2}:[0-9]{2}"
  val invest: String = "[1-9][0-9]*.[0-9]* [1-9][0-9]*.[0-9]* [1-9][0-9]*.[0-9]* [1-9][0-9]*.[0-9]*"
  val wrongInput: String = ".*"
  val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
  val getMarketData = new GetMarketDataclass(tradeData)

  def doTickersymbol(input: String): (String, BacktestInterpreter) = {
    (showCompany(input.split(" ")(0), input.split(" ")(1), balance, getPriceForDateTimeDouble(input.split(" ")(1),  "OpenPrice")), this)
  }

  def doInvest(input: String): (String, BacktestInterpreter) = {
    val dateTime = input.split(" ")(1)
    val currentTradestore = new Trade(input.split(" ")(0).toDouble, input.split(" ")(1).toDouble, input.split(" ")(2).toDouble, input.split(" ")(3).toDouble, dateTime, tickersymbol)
    trades.addOne(new TradeisBuy(currentTradestore))
    donetrades.addOne(new TradeDoneCalculations(currentTradestore))
    (currentTrade(currentTradestore), this)
  }

  def doWrongInput(input: String): (String, BacktestInterpreter) = ("Wrong input. Please choose from Available Symbols: EURUSD\n\nto Stop : Q\n\n", this)

  def doQuit(input: String): (String, BacktestInterpreter) = (getMarketData.closeProgram, this)

  override def resetState: Interpreter = {
    // Return a new instance with the initial balance
    this
  }



  override val actions: Map[String, String => (String, Interpreter)] =
    Map((wrongInput, doWrongInput), (tickersymbol, doTickersymbol), (quit, doQuit), (invest, doInvest))
}