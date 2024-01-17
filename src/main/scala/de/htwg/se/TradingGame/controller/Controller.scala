package de.htwg.se.TradingGame.controller

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.model.DataSave.TradeData
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.util.Observable
import de.htwg.se.TradingGame.util.UndoManager
import net.codingwell.scalaguice.InjectorExtensions.*

class Controller @Inject() extends IController {
  var output: String = ""

  override val injector: Injector = Guice.createInjector(new TradingGameModule)
  var interpreter: Interpreter = injector.getInstance(classOf[Interpreter])
  private val undoManager = new UndoManager

  override def computeInput(input: String): Unit = 
    input match {
      case "undo" => undo()
      case "redo" => redo()
      case _ => doStep(input)
    }
    
  def doStep(input: String): Unit = 
    undoManager.doStep(new SetCommand(input, this))
    output = interpreter.descriptor
    notifyObservers
  

  def undo(): Unit = undoManager.undoStep

  def redo(): Unit = 
    undoManager.redoStep
    notifyObservers
  

  override def printDescriptor(): Unit = 
    output = interpreter.descriptor
    notifyObservers
  
  
  override def setBalance(newBalance: Double): Unit = TradeData.balance = newBalance
  override def getBalance: Double =TradeData.balance
  override def getdatabaseconnectionStrings: List[String] = TradeData.databaseStrings
  override def getloadFileList: List[String] = TradeData.loadFileList
  override def getPairList: List[String] = TradeData.pairList

}