package TradingGame 
import MainClass._
import TradingMethods._
import java.time.LocalDate


class BrowseInterpreter(balance: String) extends Interpreter {

    
    override val descriptor: String = "Please Enter the Tickersymbol of your choice:\nAvailable Symbols:\nTSLA : Tesla\nAAPL : Apple\nAMZN : Amazon\nMCD  : McDonalds\n\nto Stop : Q\n\n"

    val quit: String = "Q"
    val tickersymbol: String = "[A-Z]{3,4}"
    val wrongInput: String = ".*"

    def doTickersymbol(input: String): (String, InvestInterpreter) = (showCompany(input, LocalDate.parse("2023-10-26"), balance.toDouble ), new InvestInterpreter(input, balance))

    def doWrongInput(input: String): (String, BrowseInterpreter) = ("Wrong input. Please choose from Available Symbols:\nTSLA : Tesla\nAAPL : Apple\nAMZN : Amazon\nMCD  : McDonalds\n\nto Stop : Q\n\n", this)

    def doQuit(input: String): (String, BrowseInterpreter) = (closeProgram, this)

    override val actions: Map[String, String => (String, Interpreter)] =
        Map((wrongInput, doWrongInput),(tickersymbol, doTickersymbol),(quit,doQuit))
}