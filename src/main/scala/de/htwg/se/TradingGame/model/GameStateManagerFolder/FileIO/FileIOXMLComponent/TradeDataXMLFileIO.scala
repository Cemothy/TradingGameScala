package de.htwg.se.TradingGame.model.GameStateManagerFolder.FileIO.FileIOXMLComponent

import de.htwg.se.TradingGame.model.GameStateManagerFolder.IGameStateManager
import de.htwg.se.TradingGame.model.GameStateManagerFolder.GameStateFolder.GameState
import scala.collection.mutable.ArrayBuffer
import scala.xml._
import de.htwg.se.TradingGame.model.FileIO.TradeDataFileIO
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.TradeDoneCalculations
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.Trade
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent

class TradeDataXMLFileIO extends TradeDataFileIO{
  override def saveData(gameState: GameState,  filename: String): Unit = 
    val filnamexml = "./src/main/scala/de/htwg/se/TradingGame/model/Data/" + filename + ".xml"
    val file = new java.io.File(filnamexml)
    if (!file.getParentFile.exists()) 
      val directoriesCreated = file.getParentFile.mkdirs()
      println("Directories created: " + directoriesCreated)
    val doneTradesElements = gameState.doneTrades.map { trade =>
      <DoneTrades>
        <entryTrade>{trade.trade.entryTrade}</entryTrade>
        <stopLossTrade>{trade.trade.stopLossTrade}</stopLossTrade>
        <takeProfitTrade>{trade.trade.takeProfitTrade}</takeProfitTrade>
        <risk>{trade.trade.risk}</risk>
        <datestart>{trade.trade.datestart}</datestart>
        <ticker>{trade.trade.ticker}</ticker>
        <dateTradeTriggered>{trade.dateTradeTriggered}</dateTradeTriggered>
        <tradeWinOrLose>{trade.tradeWinOrLose}</tradeWinOrLose>
        <dateTradeDone>{trade.dateTradeDone}</dateTradeDone>
        <currentprofit>{trade.currentprofit}</currentprofit>
        <endProfit>{trade.endProfit}</endProfit>
      </DoneTrades>
    }
    val rootElement = 
      <GameState>
        <Balance>{gameState.balance}</Balance>
        <BacktestDate>{gameState.backtestDate}</BacktestDate>
        <StartBalance>{gameState.startbalance}</StartBalance>
        <Pair>{gameState.pair}</Pair>
        <SaveName>{gameState.savename}</SaveName>
        <EndDate>{gameState.endDate}</EndDate>
        <StartDate>{gameState.startDate}</StartDate>
        <DatabaseConnectionString>{gameState.databaseConnectionString}</DatabaseConnectionString>
        <DistanceCandles>{gameState.distancecandles}</DistanceCandles>
        <Interval>{gameState.interval}</Interval>
        <DoneTrades>
          {doneTradesElements}
        </DoneTrades>
      </GameState>
    XML.save(filnamexml, rootElement, "UTF-8", true)
    
  override def loadData(filename: String, gameStateManager: IGameStateManager): GameState = 
    val filnamexml = "./src/main/scala/de/htwg/se/TradingGame/model/Data/" + filename + ".xml"
    val file = XML.loadFile(filnamexml)
    val doneTradesl = (file \ "DoneTrades" \ "DoneTrades").map { trade =>
      val entryTrade = (trade \ "entryTrade").text.toDouble
      val stopLossTrade = (trade \ "stopLossTrade").text.toDouble
      val takeProfitTrade = (trade \ "takeProfitTrade").text.toDouble
      val risk = (trade \ "risk").text.toDouble
      val datestart = (trade \ "datestart").text
      val ticker = (trade \ "ticker").text
      val dateTradeTriggered = (trade \ "dateTradeTriggered").text
      val tradeWinOrLose = (trade \ "tradeWinOrLose").text
      val dateTradeDone = (trade \ "dateTradeDone").text
      val currentprofit = (trade \ "currentprofit").text.toDouble
      val endProfit = (trade \ "endProfit").text.toDouble
      val tradeComponent = new Trade(entryTrade, stopLossTrade, takeProfitTrade, risk, datestart, ticker)
      new TradeDoneCalculations(tradeComponent, dateTradeTriggered, tradeWinOrLose, dateTradeDone, currentprofit, endProfit, gameStateManager)
    }.toArray
    val balancel = (file \ "Balance").text.toDouble
    val backtestDatel = (file \ "BacktestDate").text.toLong
    val startbalancel = (file \ "StartBalance").text.toDouble
    val pairl = (file \ "Pair").text
    val savenamel= (file \ "SaveName").text
    val endDatel = (file \ "EndDate").text.toLong
    val startDatel = (file \ "StartDate").text.toLong
    val databaseConnectionStringl = (file \ "DatabaseConnectionString").text
    val distancecandlesl = (file \ "DistanceCandles").text.toInt
    val intervall = (file \ "Interval").text
    new GameState {
      override def startbalance: Double = startbalancel
      override def balance = balancel
      override def backtestDate = backtestDatel
      override def trades = ArrayBuffer.empty[TradeComponent]
      override def doneTrades: ArrayBuffer[TradeDoneCalculations] = ArrayBuffer(doneTradesl: _*)
      override def pair = pairl
      override def savename = savenamel
      override def endDate = endDatel
      override def startDate = startDatel
      override def databaseConnectionString = databaseConnectionStringl
      override def distancecandles = distancecandlesl
      override def interval = intervall
      override def pairList: List[String] = List.empty[String]
      override def loadFileList: List[String] = List.empty[String]
    }
}