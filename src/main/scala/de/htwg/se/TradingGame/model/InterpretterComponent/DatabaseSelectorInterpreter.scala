package de.htwg.se.TradingGame.model.InterpretterComponent 

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.model.DataSave.TradeData.databaseConnectionString
import de.htwg.se.TradingGame.model._
import de.htwg.se.TradingGame.view.GUI.GetDatabaseData
import net.codingwell.scalaguice.InjectorExtensions.*

import scala.io.Source
import de.htwg.se.TradingGame.model.DataSave.TradeData.databaseStrings
import de.htwg.se.TradingGame.model.GetMarketDataComponent.GetMarketData.getPairNames
import de.htwg.se.TradingGame.model.DataSave.TradeData.pairList
import de.htwg.se.TradingGame.model.BacktestStage.GetDatabaseData

class DatabaseSelectorInterpreter @Inject() extends Interpreter {
  val databaseOptions: String = databaseStrings.zipWithIndex.map { case (s, i) => s"${i+1}: $s" }.mkString("\n")
  override val descriptor: String = "Please select a database by entering its number:\n" + databaseOptions + "\n"

  val numberPattern: String = "\\d+"
  val wrongInput: String = ".*"

  def selectDatabase(input: String): (String, Interpreter) = 
    val index = input.toInt - 1
    if (index >= 0 && index < databaseStrings.length && GetDatabaseData.tryConnect(databaseStrings(index))) 
      databaseConnectionString = databaseStrings(index)
      pairList = getPairNames(databaseConnectionString)
      ("Database selected successfully and able to connect", BacktestOrLiveInterpreter())
    else 
      ("Did not connect", DatabaseSelectorInterpreter())
    
  

  def doWrongInput(input: String): (String, Interpreter) = ("Invalid input. Please type a valid number", this)

  override def resetState: Interpreter = DatabaseSelectorInterpreter()

  override val actions: Map[String, String => (String, Interpreter)] = Map((wrongInput, doWrongInput), (numberPattern, selectDatabase))
}