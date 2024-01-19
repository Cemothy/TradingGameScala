package de.htwg.se.TradingGame.model.InterpretterComponent 

import com.google.inject.Inject
import de.htwg.se.TradingGame.model._
import de.htwg.se.TradingGame.view.GUI.Stages.BalanceStage

import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths
import de.htwg.se.TradingGame.controller.GameStateManager
import org.checkerframework.checker.units.qual.g
import scalafx.scene.input.KeyCode.S

class SelectNewFileInterpreter @Inject()(val gameStateManager: GameStateManager) extends Interpreter {
  var descriptor: String = "Creating new file:\n"

  val createFile: String = "\\w+"
  val wrongInput: String = ".*"

  def doCreateNewFile(input: String): (String, Interpreter) = 
    val filePath = Paths.get("src/main/scala/de/htwg/se/TradingGame/Data", input)
    if (!Files.exists(filePath)) 
      gameStateManager.changeSaveName(input)
      ("Name to save data saved", BalanceInterpreter(gameStateManager))
    else 
      ("File already exists", this)
    
  def doWrongInput(input: String): (String, SelectNewFileInterpreter) = ("Wrong input. Please enter a valid file name", this)
  override def resetState: Interpreter = SelectNewFileInterpreter(gameStateManager)
  
  override val actions: Map[String, String => (String, Interpreter)] = Map((createFile, doCreateNewFile))
}