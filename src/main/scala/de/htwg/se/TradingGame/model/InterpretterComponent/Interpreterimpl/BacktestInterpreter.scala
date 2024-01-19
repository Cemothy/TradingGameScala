package de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl 

import com.google.inject.Inject
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import scalafx.scene.input.KeyCode.B
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import scala.util.Success
import scala.util.Failure
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.GetMarktDataforInterpreterFolder.PrintMarketData
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.GetMarktDataforInterpreterFolder.GetMarketDataforInterpreter._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.TradeDoneCalculations
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.Trade

class BacktestInterpreter @Inject() (val gameStateManager: IGameStateManager) extends Interpreter {
  val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm").withZone(ZoneId.systemDefault())
  val printmarketdata = new PrintMarketData(gameStateManager)
  var descriptor: String = 
  """
  |Please Enter one of the following commands:
  |
  |1. To change the backtest date: changeBacktestDateto YYYY.MM.DD,HH:MM
  |2. To change the pair: changePairto <pair>
  |3. To change the start balance: changeStartBalanceto <balance>
  |4. To change the save name: changeSaveNameto <name>
  |5. To change the distance of candles: changeDistanceCandlesTo <distance>
  |6. To change the interval: changeintervalto <interval>
  |7. To invest: invest <entry> <stoploss> <takeprofit> <riskpercent>
  |8. To change the balance: changebalanceto <balance>
  |9. To quit: Q
  |
  |
  """.stripMargin

  val quit: String = "Q"
  val changeBacktestDateto: String = "changeBacktestDateto \\d{4}\\.\\d{2}\\.\\d{2},\\d{2}:\\d{2}"
  val changepairto: String = "changePairto \\w+"
  val changeStartBalanceto: String = "changeStartBalanceto \\d+"
  val changebalanceto: String = "changebalanceto \\d+"
  val changeSaveNameto: String = "changeSaveNameto \\w+"
  val changeDistanceCandlesTo: String = "changeDistanceCandlesTo \\d+"
  val changeintervalto: String = "changeintervalto (1m|5m|15m|1h|4h|1d|1w)"
  val invest: String = "invest (\\d+\\.\\d+ ){3}\\d+\\.\\d+"
  val wrongInput: String = ".*"

  def dochanebalanceto (input: String): (String, BacktestInterpreter) = 
    val splitInput = input.split(" ")
    gameStateManager.changeBalance(splitInput(1).toDouble)
    (s"changed balance to ${gameStateManager.currentState.balance}", this)

  def doChangeBacktestDateto(input: String): (String, BacktestInterpreter) = 
    val splitInput = input.split(" ")
    gameStateManager.changeBacktestDate(convertToEpochSeconds(splitInput(1)))
    (s"changed backtestdate to ${gameStateManager.currentState.backtestDate}", this)

  def dochangepairto(input: String): (String, BacktestInterpreter) = 
    val splitInput = input.split(" ")
    gameStateManager.changePair(splitInput(1))
    (s"changed pair to ${gameStateManager.currentState.pair}",this)

  def dochangeStartBalanceto(input: String): (String, BacktestInterpreter) =
    val splitInput = input.split(" ")
    gameStateManager.changeStartBalance(splitInput(1).toDouble)
    (s"changed startbalance to ${gameStateManager.currentState.startbalance}",this)
  
  def dochangeSaveNameto(input: String): (String, BacktestInterpreter) =
    val splitInput = input.split(" ")
    gameStateManager.changeSaveName(splitInput(1))
    (s"changed savename to ${gameStateManager.currentState.savename}", this)

  def dochangeDistanceCandlesTo(input: String): (String, BacktestInterpreter) =
    val splitInput = input.split(" ")
    gameStateManager.changeDistanceCandles(splitInput(1).toInt)
    (s"changed distanceCandles to ${gameStateManager.currentState.distancecandles}", this)

  def dochangeintervalto(input: String): (String, BacktestInterpreter) =
    val splitInput = input.split(" ")
    gameStateManager.changeInterval(splitInput(1))
    gameStateManager.changeDistanceCandles(intervalasSeconds(gameStateManager.currentState.interval))
    (s"changed interval to ${gameStateManager.currentState.interval}", this)
  
  def doinvest(input: String): (String, BacktestInterpreter) = 
    val splitInput = input.split(" ")
    val entry = splitInput(1).toDouble
    val stoploss = splitInput(2).toDouble
    val takeprofit = splitInput(3).toDouble
    val riskpercent = splitInput(4).toDouble
    val datestart = formatter.format(Instant.ofEpochSecond(gameStateManager.currentState.backtestDate))
    val ticker = gameStateManager.currentState.pair
    val trade = new Trade(entry, stoploss, takeprofit, riskpercent, datestart, ticker)
    val tradebuffer = gameStateManager.currentState.trades += trade
    gameStateManager.changeTrades(tradebuffer)
    val tradeDoneCalculationsFuture = Future { TradeDoneCalculations(trade, gameStateManager)}
    tradeDoneCalculationsFuture.onComplete {
      case Success(tradeDoneCalculations) =>
        val tradeDoneCalculationsBuffer = gameStateManager.currentState.doneTrades += tradeDoneCalculations
        gameStateManager.changeDoneTrades(tradeDoneCalculationsBuffer)
      case Failure(e) =>
    }
    (s"Ticker: ${ticker}\nDate: ${datestart}\nAdded trade with entry $entry, stoploss $stoploss, takeprofit $takeprofit, riskpercent $riskpercent, TradeBuffer ${gameStateManager.currentState.doneTrades}", this)
  
  def doQuit(input: String): (String, BacktestInterpreter) = (printmarketdata.closeProgram, BacktestInterpreter(gameStateManager))
  override def resetState: Interpreter =  BacktestInterpreter(gameStateManager)
  
  override val actions: Map[String, String => (String, Interpreter)] =
    Map(
      (quit, doQuit), 
      (changeBacktestDateto, doChangeBacktestDateto), 
      (changepairto, dochangepairto), 
      (changeStartBalanceto, dochangeStartBalanceto), 
      (changeSaveNameto, dochangeSaveNameto), 
      (changeDistanceCandlesTo, dochangeDistanceCandlesTo), 
      (changeintervalto, dochangeintervalto), 
      (invest, doinvest),
      (changebalanceto, dochanebalanceto)
    )
}