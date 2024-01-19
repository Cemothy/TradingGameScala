package de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl 

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model._
import net.codingwell.scalaguice.InjectorExtensions.*
import org.checkerframework.checker.units.qual.C
import org.checkerframework.checker.units.qual.g
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.GetMarktDataforInterpreterFolder.PrintMarketData
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.GetMarktDataforInterpreterFolder.GetMarketDataforInterpreter._
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter

class ChoosePairAndDateInterpreter @Inject() (val gameStateManager: IGameStateManager) extends Interpreter {
  gameStateManager.changePairList(getPairNames(gameStateManager.currentState.databaseConnectionString))
  val datesForMarktnames = getDatesForMarktnames(gameStateManager.currentState.pairList, gameStateManager)
  val pairList: String = datesForMarktnames.map { case (marktname, (startDate, endDate)) =>
    s"Pair: $marktname, Start Date: $startDate, End Date: $endDate"
  }.mkString("\n")
  var descriptor: String = s"Please select a pair and a date like:\n pair yyyy.MM.DD,HH:mm\nAvailable pairs:\n$pairList\n"
  val pairAndDateInput: String = "\\w+ \\d{4}\\.\\d{2}\\.\\d{2},\\d{2}:\\d{2}"
  val wrongInput: String = ".*"

  def doPairAndDate(input: String): (String, Interpreter) = 
    val splitInput = input.split(" ")
    val pair = splitInput(0)
    datesForMarktnames.get(pair) match {
      case Some((startDate, endDate)) =>
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
        val startDateTime = LocalDateTime.parse(startDate, formatter)
        val endDateTime = LocalDateTime.parse(endDate, formatter)
        val startEpochSecond = startDateTime.atZone(ZoneId.systemDefault()).toEpochSecond
        val endEpochSecond = endDateTime.atZone(ZoneId.systemDefault()).toEpochSecond
        gameStateManager.changeEndDate(endEpochSecond)
        gameStateManager.changeStartDate(startEpochSecond)
      case None =>
    }
    gameStateManager.changePair(splitInput(0)) 
    gameStateManager.changeBacktestDate(convertToEpochSeconds(splitInput(1)))
    gameStateManager.changeDistanceCandles(intervalasSeconds(gameStateManager.currentState.interval))
    ("Processing pair and date...", BacktestInterpreter(gameStateManager)) 

  def doWrongInput(input: String): (String, ChoosePairAndDateInterpreter) = ("Wrong input. Please select a pair and a date", this)
  override def resetState: Interpreter = ChoosePairAndDateInterpreter(gameStateManager)
  override val actions: Map[String, String => (String, Interpreter)] = Map( (pairAndDateInput, doPairAndDate))
}