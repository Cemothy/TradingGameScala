package de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl 

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.model._
import net.codingwell.scalaguice.InjectorExtensions.*
import org.checkerframework.checker.units.qual.g
import scalafx.scene.input.KeyCode.L
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl._
class LoadorNewFileInterpreter @Inject() (val gameStateManager: IGameStateManager) extends Interpreter {
  var descriptor: String = "What do you want to do?:\n  Load\n  New\n"
  val loadFile: String = "Load"
  val newFile: String = "New"
  val wrongInput: String = ".*"

  def doLoadFile(input: String): (String, Interpreter) = ("You choose Load", SelectLoadFileInterpreter(gameStateManager))
  
  def doNewFile(input: String): (String, Interpreter) = ("You choose New", SelectNewFileInterpreter(gameStateManager))
  
  def doWrongInput(input: String): (String, LoadorNewFileInterpreter) = ("Wrong input. Please select 'Load' or 'New'", this)
  override def resetState: Interpreter = LoadorNewFileInterpreter(gameStateManager)
  override val actions: Map[String, String => (String, Interpreter)] = Map( (loadFile, doLoadFile), (newFile, doNewFile))
}