package de.htwg.se.TradingGame.model.InterpretterComponent 

import com.google.inject.Inject
import de.htwg.se.TradingGame.model.DataSave.TradeData._
import de.htwg.se.TradingGame.model._
import de.htwg.se.TradingGame.view.GUI.Stages.BalanceStage

import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths

class SelectNewFileInterpreter @Inject() extends Interpreter {
  override val descriptor: String = "Creating new file:\n"

  val createFile: String = "\\w+"
  val wrongInput: String = ".*"

  def doCreateNewFile(input: String): (String, Interpreter) = 
    val filePath = Paths.get("src/main/scala/de/htwg/se/TradingGame/Data", input)
    if (!Files.exists(filePath)) 
      savename = input
      ("Name to save data saved", BalanceInterpreter())
    else 
      ("File already exists", this)
    
  def doWrongInput(input: String): (String, SelectNewFileInterpreter) = ("Wrong input. Please enter a valid file name", this)
  override def resetState: Interpreter = this
  
  override val actions: Map[String, String => (String, Interpreter)] = Map((wrongInput, doWrongInput), (createFile, doCreateNewFile))
}