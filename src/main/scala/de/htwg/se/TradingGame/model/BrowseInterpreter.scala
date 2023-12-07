package de.htwg.se.TradingGame.model 
import de.htwg.se.TradingGame.model.MenuInterpreter
import de.htwg.se.TradingGame.model.BrowseInterpreter
import de.htwg.se.TradingGame.model.InvestInterpreter
import de.htwg.se.TradingGame.model.TradingMethods._
import de.htwg.se.TradingGame.model.GetMarketData._
import java.time.LocalDate
import java.io.File

class BrowseInterpreter(balance: String) extends Interpreter {

    
    override val descriptor: String = "Please Enter the Tickersymbol of your choice: EURUSD Date: YYYY.MM.DD,HH:MM \n\nto Stop : Q\n\n"

    val quit: String = "Q"
    val tickersymbol: String = "EURUSD [0-9]{4}.[0-9]{2}.[0-9]{2},[0-9]{2}:[0-9]{2}"
    val wrongInput: String = ".*"
    val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath



    def doTickersymbol(input: String): (String, InvestInterpreter) = {
        val price = getPriceForDateTimeDouble(input.split(" ")(1), new java.io.File(Path).getParent +  s"/Symbols/${input.split(" ")(0)}.csv", 5)
        val companyInfo = price match {
            case Some(x) => showCompany(input.split(" ")(0), input.split(" ")(1), balance.toDouble, x)
            case None => showCompany(input.split(" ")(0), input.split(" ")(1), balance.toDouble, 0.0)
        }
        val interpreter = new InvestInterpreter(input.split(" ")(0), input.split(" ")(1), balance)
        (companyInfo, interpreter)
    }

    def doWrongInput(input: String): (String, BrowseInterpreter) = ("Wrong input. Please choose from Available Symbols: EURUSD\n\nto Stop : Q\n\n", this)

    def doQuit(input: String): (String, BrowseInterpreter) = (closeProgram, this)

    override val actions: Map[String, String => (String, Interpreter)] =
        Map((wrongInput, doWrongInput),(tickersymbol, doTickersymbol),(quit,doQuit))
}