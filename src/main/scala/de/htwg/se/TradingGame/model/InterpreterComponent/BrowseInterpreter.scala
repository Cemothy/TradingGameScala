package de.htwg.se.TradingGame.model 
import de.htwg.se.TradingGame.model.MenuInterpreter
import de.htwg.se.TradingGame.model.BrowseInterpreter
import de.htwg.se.TradingGame.model.InvestInterpreter
import de.htwg.se.TradingGame.model.TradingMethods._
import de.htwg.se.TradingGame.model.GetMarketData
import java.time.LocalDate
import java.io.File
import scala.util.Try
import scala.util.Success
import scala.util.Failure

class BrowseInterpreter(balanceInput: String) extends Interpreter {

    val getMarketData = new GetMarketData
    override val balance = balanceInput 
    override val descriptor: String = "Please Enter the Tickersymbol of your choice: EURUSD Date: YYYY.MM.DD,HH:MM \n\nto Stop : Q\n\n"

    val quit: String = "Q"
    val tickersymbol: String = "EURUSD [0-9]{4}.[0-9]{2}.[0-9]{2},[0-9]{2}:[0-9]{2}"
    val wrongInput: String = ".*"
    val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath



    def doTickersymbol(input: String): (String, Interpreter) = {
        val result = Try {
            val price = getMarketData.getPriceForDateTimeDouble(input.split(" ")(1), new java.io.File(Path).getParent +  s"/Symbols/${input.split(" ")(0)}.csv", 5)
            val companyInfo = showCompany(input.split(" ")(0), input.split(" ")(1), balance.toDouble, price)
            val interpreter = new InvestInterpreter(input.split(" ")(0), input.split(" ")(1), balance)
            (companyInfo, interpreter)
        }

        result match {
            case Success(value) => value
            case Failure(exception) => ("Date is not in file", this)
        }
    }

    def doWrongInput(input: String): (String, BrowseInterpreter) = ("Wrong input. Please choose from Available Symbols: EURUSD\n\nto Stop : Q\n\n", this)

    def doQuit(input: String): (String, BrowseInterpreter) = (getMarketData.closeProgram, this)

    override val actions: Map[String, String => (String, Interpreter)] =
        Map((wrongInput, doWrongInput),(tickersymbol, doTickersymbol),(quit,doQuit))
}