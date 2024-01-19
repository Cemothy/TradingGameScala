package de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder.DefaultGameStateimpl

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Names
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators._
import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder.GameState
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer

class DefaultGameState  @Inject() extends GameState {
  override def balance: Double = 0.0
  override def backtestDate: Long = 0L
  override def trades: ArrayBuffer[TradeComponent] = ArrayBuffer.empty
  override def doneTrades: ArrayBuffer[TradeDoneCalculations] = ArrayBuffer.empty
  override def startbalance: Double = 0.0
  override def pair: String = ""
  override def savename: String = ""
  override def endDate: Long = 0L
  override def startDate: Long = 0L
  override def databaseConnectionString: String = ""
  override def distancecandles: Int = 0
  override def interval: String = "1h"
  override def pairList: List[String] = List.empty[String]
  override def loadFileList: List[String] =  List.empty[String]
}