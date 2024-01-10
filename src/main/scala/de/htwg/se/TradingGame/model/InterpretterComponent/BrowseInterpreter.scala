package de.htwg.se.TradingGame.model.InterpretterComponent 
import de.htwg.se.TradingGame.model.GetMarketDataComponent.GetMarketData._
import de.htwg.se.TradingGame.model.InterpretterComponent.BrowseInterpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.InvestInterpreter
import de.htwg.se.TradingGame.model.InterpretterComponent.MenuInterpreter
import com.google.inject.Inject
import java.io.File
import java.time.LocalDate
import de.htwg.se.TradingGame.model.DataSave.TradeDataclass


class BrowseInterpreter @Inject() (balanceInput: String, tradeData: TradeDataclass) extends Interpreter {


    override val balance = balanceInput 
    override val descriptor: String = "Please Enter the Tickersymbol of your choice: EURUSD Date: YYYY.MM.DD,HH:MM \n\nto Stop : Q\n\n"

    val quit: String = "Q"
    val tickersymbol: String = "EURUSD [0-9]{4}.[0-9]{2}.[0-9]{2},[0-9]{2}:[0-9]{2}"
    val wrongInput: String = ".*"
    val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
    val getMarketData = new GetMarketDataclass(tradeData)



    def doTickersymbol(input: String): (String, InvestInterpreter) = (showCompany(input.split(" ")(0), input.split(" ")(1), balance.toDouble, getPriceForDateTimeDouble(input.split(" ")(1),  "OpenPrice")), new InvestInterpreter(input.split(" ")(0), input.split(" ")(1), balance, tradeData))

    def doWrongInput(input: String): (String, BrowseInterpreter) = ("Wrong input. Please choose from Available Symbols: EURUSD\n\nto Stop : Q\n\n", this)

    def doQuit(input: String): (String, BrowseInterpreter) = (getMarketData.closeProgram, this)

    override def resetState: Interpreter = {
        // Return a new instance with the initial balance
        new BrowseInterpreter(balanceInput, tradeData)
    }
    override def interpreterType: String = "BrowseInterpreter"
    override val actions: Map[String, String => (String, Interpreter)] =
        Map((wrongInput, doWrongInput),(tickersymbol, doTickersymbol),(quit,doQuit))
}