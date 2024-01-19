package de.htwg.se.TradingGame.model.InterpretterComponent.Interpreterimpl 

import com.google.inject.Inject

import de.htwg.se.TradingGame.model._
import org.checkerframework.checker.units.qual.g
import scalafx.scene.input.KeyCode.S

import java.nio.file.Files
import java.nio.file.Paths
import scala.io.Source
import scala.jdk.CollectionConverters._
import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter

class SelectLoadFileInterpreter @Inject() (val gameStateManager: IGameStateManager) extends Interpreter {
  gameStateManager.changeLoadFileList( Files.list(Paths.get(getClass.getResource("/de/htwg/se/TradingGame/model/Data/").toURI)).iterator().asScala.map(_.getFileName.toString).toList)
  var descriptor: String = s"Choose a file to load:\n${gameStateManager.currentState.loadFileList}\n"

  val loadFile: String = "\\w+.\\w+"
  val wrongInput: String = ".*"

  def doLoadFile(input: String): (String, Interpreter) = {
    val filename = input.split("\\.")(0)
    gameStateManager.changeSaveName(filename)
    gameStateManager.loadCurrentState()
    ("File loaded successfully", BacktestInterpreter(gameStateManager))
  }

  def doWrongInput(input: String): (String, SelectLoadFileInterpreter) = ("Wrong input. Please select a valid file", SelectLoadFileInterpreter(gameStateManager))

  override def resetState: Interpreter = SelectLoadFileInterpreter(gameStateManager)

  override val actions: Map[String, String => (String, Interpreter)] = Map((loadFile, doLoadFile))
}