package de.htwg.se.TradingGame.controller

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.TradingGameModule
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.util.Observable
import de.htwg.se.TradingGame.util.UndoManager
import net.codingwell.scalaguice.InjectorExtensions.*


trait IController extends Observable {
  var output: String
  var interpreter: Interpreter
  def computeInput(input: String): Unit
  def printDescriptor(): Unit
  def setBalance(balance: Double): Unit
  def getBalance: Double 
  def getdatabaseconnectionStrings: List[String]
  def getloadFileList: List[String]
  def getPairList: List[String]
  val injector: Injector = Guice.createInjector(new TradingGameModule)
}

