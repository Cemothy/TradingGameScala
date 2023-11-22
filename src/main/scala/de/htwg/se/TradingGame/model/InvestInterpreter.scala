package de.htwg.se.TradingGame.model 
import de.htwg.se.TradingGame.model._
import java.time.LocalDate
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._

//InvestInterpreter should read entryTrade, stopLossTrade, takeProfitTrade, riskTrade from terminal input

class InvestInterpreter(tickersymbol: String, dateTime: String, balance: String) extends Interpreter {


    private var currentTradestore: Trade = _
    var TradeDoneCalculationsstore: TradeDoneCalculations = _
    override val descriptor: String = "Please enter your entryTrade stopLossTrade takeProfitTrade riskTrade\n"
    

    val invest: String = "[1-9][0-9]*.[0-9]* [1-9][0-9]*.[0-9]* [1-9][0-9]*.[0-9]* [1-9][0-9]*.[0-9]*"
    val wrongInput: String = ".*"


    def doInvest(input: String): (String, BrowseInterpreter) = {
        
        this.currentTradestore = new Trade(input.split(" ")(0).toDouble, input.split(" ")(1).toDouble, input.split(" ")(2).toDouble, input.split(" ")(3).toDouble, dateTime, tickersymbol)
        this.TradeDoneCalculationsstore = GetMarketData.calculateTrade(currentTradestore)
        GetMarketData.trades.addOne(TradeDoneCalculationsstore)
        
        GetMarketData.balance = balance.toDouble
        (TradingMethods.currentTrade(currentTradestore), new BrowseInterpreter(balance))
    }
        

    def doWrongInput(input: String): (String, InvestInterpreter) = ("Wrong input. Pleas type a numbers", this)

    override val actions: Map[String, String => (String, Interpreter)] =
        Map((wrongInput, doWrongInput),(invest,doInvest))
}