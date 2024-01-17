package de.htwg.se.TradingGame.model.InterpretterComponent 

import com.google.inject.Inject
import de.htwg.se.TradingGame.model.DataSave.TradeData
import de.htwg.se.TradingGame.model.DataSave.TradeData._
import de.htwg.se.TradingGame.model.DataSave.TradeData.savename
import de.htwg.se.TradingGame.model.DataSave.TradeDataclass
import de.htwg.se.TradingGame.model._

import java.nio.file.Files
import java.nio.file.Paths
import scala.io.Source
import scala.jdk.CollectionConverters._

class SelectLoadFileInterpreter @Inject() extends Interpreter {

  val tradeData = injector.getInstance(classOf[TradeDataclass])
  val fileList = TradeData.loadFileList.mkString("\n")
  override val descriptor: String =  s"Choose a file to load:\n$fileList\n"
  
  val loadFile: String = "\\w+.\\w+"
  val wrongInput: String = ".*"

  def doLoadFile(input: String): (String, Interpreter) = 
    if (TradeData.loadFileList.contains(input)) 
      val filename = input.split("\\.")(0)
      tradeData.loadData(filename)
      savename = filename
      ("File loaded successfully", BacktestInterpreter())
    else 
      ("File does not exist", this)
    
  def doWrongInput(input: String): (String, SelectLoadFileInterpreter) = ("Wrong input. Please select a valid file", this)
  override def resetState: Interpreter = this
  override val actions: Map[String, String => (String, Interpreter)] = Map((wrongInput, doWrongInput), (loadFile, doLoadFile))


}