package de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl 

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.TradingGameModule

import de.htwg.se.TradingGame.model._
import net.codingwell.scalaguice.InjectorExtensions.*
import org.checkerframework.checker.units.qual.g
import scalafx.scene.input.KeyCode.D
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.GetMarktDataforInterpreterFolder.PrintMarketData
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl.GetMarktDataforInterpreterFolder.GetMarketDataforInterpreter._
import scala.io.Source
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl._

class DatabaseSelectorInterpreter @Inject() (val gameStateManager: IGameStateManager)extends Interpreter {
  val databaseStrings: List[String] = Source.fromFile("./src/main/scala/de/htwg/se/TradingGame/Database/DatabaseconnectionStrings.txt").getLines.toList
  val databaseOptions: String = databaseStrings.zipWithIndex.map { case (s, i) => s"${i+1}: $s" }.mkString("\n")
  var descriptor: String = "Please select a database by entering its number:\n" + databaseOptions + "\n"

  val numberPattern: String = "\\d+"
  val wrongInput: String = ".*"

  def selectDatabase(input: String): (String, Interpreter) = 
    val index = input.toInt - 1
    if (index >= 0 && index < databaseStrings.length && tryConnect(databaseStrings(index))) 
      gameStateManager.changeDatabaseConnectionString(databaseStrings(index))
      ("Database selected successfully and able to connect", BacktestOrLiveInterpreter(gameStateManager))
    else 
      ("Did not connect", DatabaseSelectorInterpreter(gameStateManager))

  def doWrongInput(input: String): (String, Interpreter) = ("Invalid input. Please type a valid number", this)
  override def resetState: Interpreter = DatabaseSelectorInterpreter(gameStateManager)
  override val actions: Map[String, String => (String, Interpreter)] = Map( (numberPattern, selectDatabase))
}