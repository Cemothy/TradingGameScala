package de.htwg.se.TradingGame.model.InterpretterComponent 

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.model.DataSave.TradeData._
import de.htwg.se.TradingGame.model._
import net.codingwell.scalaguice.InjectorExtensions.*
import de.htwg.se.TradingGame.model.DataSave.TradeData
import de.htwg.se.TradingGame.model.GetMarketDataComponent.GetMarketData.getDatesForMarktnames

class ChoosePairAndDateInterpreter @Inject() extends Interpreter {
  val datesForMarktnames = getDatesForMarktnames(TradeData.pairList)
  val pairList: String = datesForMarktnames.map { case (marktname, (startDate, endDate)) =>
    s"Pair: $marktname, Start Date: $startDate, End Date: $endDate"
  }.mkString("\n")
  
  override val descriptor: String = s"Please select a pair and a date like:\n pair yyyy.MM.DD,HH:mm\nAvailable pairs:\n$pairList\n"

  val pairAndDateInput: String = "\\w+ \\d{4}\\.\\d{2}\\.\\d{2},\\d{2}:\\d{2}"
  val wrongInput: String = ".*"

  def doPairAndDate(input: String): (String, Interpreter) = 
    val splitInput = input.split(" ")
    pair = splitInput(0)
    backtestDate = convertToEpochSeconds(splitInput(1))
    printTradeData()
    ("Processing pair and date...", BacktestInterpreter()) // Return to BacktestInterpreter
  

  def doWrongInput(input: String): (String, ChoosePairAndDateInterpreter) = ("Wrong input. Please select a pair and a date", this)
  override def resetState: Interpreter = this
  override val actions: Map[String, String => (String, Interpreter)] = Map((wrongInput, doWrongInput), (pairAndDateInput, doPairAndDate))
}