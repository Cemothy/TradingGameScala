package de.htwg.se.TradingGame.model.InterpretterComponent 

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.model.DataSave.TradeDataclass
import de.htwg.se.TradingGame.model._
import net.codingwell.scalaguice.InjectorExtensions.*

class LoadorNewFileInterpreter @Inject() extends Interpreter {
  override val descriptor: String = "What do you want to do?:\n  Load\n  New\n"
  val loadFile: String = "Load"
  val newFile: String = "New"
  val wrongInput: String = ".*"

  def doLoadFile(input: String): (String, Interpreter) = ("You choose Load", SelectLoadFileInterpreter())
  
  def doNewFile(input: String): (String, Interpreter) = ("You choose New", SelectNewFileInterpreter())
  
  def doWrongInput(input: String): (String, LoadorNewFileInterpreter) = ("Wrong input. Please select 'Load' or 'New'", this)
  override def resetState: Interpreter = this
  override val actions: Map[String, String => (String, Interpreter)] = Map((wrongInput, doWrongInput), (loadFile, doLoadFile), (newFile, doNewFile))
}