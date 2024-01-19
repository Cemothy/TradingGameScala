package de.htwg.se.TradingGame.controller

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.model.FileIO.TradeDataFileIO
import de.htwg.se.TradingGame.model.GameCommand.ChangeBalanceCommand
import de.htwg.se.TradingGame.model.GameCommand._
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.util.Observable
import de.htwg.se.TradingGame.util.UndoManager
import net.codingwell.scalaguice.InjectorExtensions.*

class Controller @Inject() extends IController {
  var output: String = ""
  override val injector: Injector = Guice.createInjector(new TradingGameModule)
  var interpreter: Interpreter = injector.getInstance(classOf[Interpreter])
  val gameStateManager: GameStateManager = interpreter.gameStateManager
 
  private val undoManager = new UndoManager

  override def computeInput(input: String): Unit = 
    input match 
      case "undo" => undo()
      case "redo" => redo()
      case _ => doStep(input)

  def doStep(input: String): Unit = 
    undoManager.doStep(new SetCommand(input, this))
    output = interpreter.descriptor
    notifyObservers

  def undo(): Unit = 
    undoManager.undoStep
    notifyObservers

  def redo(): Unit = 
    undoManager.redoStep
    notifyObservers

  override def printDescriptor(): Unit = 
    output = interpreter.descriptor
    notifyObservers



}