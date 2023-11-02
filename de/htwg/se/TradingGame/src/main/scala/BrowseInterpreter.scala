package TradingGame 
import MainClass._
import TradingMethods._
import java.time.LocalDate
import GetMarketData._


class BrowseInterpreter(balance: String) extends Interpreter {

    
    override val descriptor: String = "Please Enter the Tickersymbol of your choice: EURUSD Date: YYYY.MM.DD,HH:MM \n\nto Stop : Q\n\n"

    val quit: String = "Q"
    val tickersymbol: String = "EURUSD [0-9]{4}.[0-9]{2}.[0-9]{2},[0-9]{2}:[0-9]{2}"
    val wrongInput: String = ".*"


    def doTickersymbol(input: String): (String, InvestInterpreter) = (showCompany(input.split(" ")(0), input.split(" ")(1), balance.toDouble, getPriceForDateTime(input.split(" ")(1), s"src/Symbols/${input.split(" ")(0)}.csv") ), new InvestInterpreter(input.split(" ")(0), input.split(" ")(1), balance))

    def doWrongInput(input: String): (String, BrowseInterpreter) = ("Wrong input. Please choose from Available Symbols: EURUSD\n\nto Stop : Q\n\n", this)

    def doQuit(input: String): (String, BrowseInterpreter) = (closeProgram, this)

    override val actions: Map[String, String => (String, Interpreter)] =
        Map((wrongInput, doWrongInput),(tickersymbol, doTickersymbol),(quit,doQuit))
}