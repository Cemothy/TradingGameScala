package de.htwg.se.TradingGame.model 
import de.htwg.se.TradingGame.model._
import java.time.LocalDate
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import de.htwg.se.TradingGame.model.TradingMethods.showCompany
import de.htwg.se.TradingGame.model.GetMarketData.getPriceForDateTimeDouble
import java.io.File

//InvestInterpreter should read entryTrade, stopLossTrade, takeProfitTrade, riskTrade from terminal input

class InvestInterpreter(tickersymbol: String, dateTime: String, balanceInput: String) extends Interpreter {


    override val balance = balanceInput
    override val descriptor: String = "Please enter your entryTrade stopLossTrade takeProfitTrade riskTrade\n"
    
    val tickersymbolbrowse: String = "EURUSD [0-9]{4}.[0-9]{2}.[0-9]{2},[0-9]{2}:[0-9]{2}"
    val invest: String = "[1-9][0-9]*.[0-9]* [1-9][0-9]*.[0-9]* [1-9][0-9]*.[0-9]* [1-9][0-9]*.[0-9]*"
    val wrongInput: String = ".*"
    val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath


    def doInvest(input: String): (String, BrowseInterpreter) = {
        
        val currentTradestore = new Trade(input.split(" ")(0).toDouble, input.split(" ")(1).toDouble, input.split(" ")(2).toDouble, input.split(" ")(3).toDouble, dateTime, tickersymbol)
        GetMarketData.trades.addOne(new TradeisBuy(currentTradestore))
        GetMarketData.donetrades.addOne(new TradeDoneCalculations(currentTradestore))
        
        GetMarketData.balance = balance.toDouble
        (TradingMethods.currentTrade(currentTradestore), new BrowseInterpreter(balance))
    }
        
    def doTickersymbol(input: String): (String, InvestInterpreter) = (showCompany(input.split(" ")(0), input.split(" ")(1), balance.toDouble, getPriceForDateTimeDouble(input.split(" ")(1), "OpenPrice")), new InvestInterpreter(input.split(" ")(0), input.split(" ")(1), balance))
    def doWrongInput(input: String): (String, InvestInterpreter) = ("Wrong input. Pleas type a numbers", this)

    override val actions: Map[String, String => (String, Interpreter)] =
        Map((wrongInput, doWrongInput),(tickersymbolbrowse, doTickersymbol),(invest,doInvest))
}