package TradingGame
import java.time.LocalDate




class InvestInterpreter extends Interpreter {


    override val descriptor: String = "Please enter your entryTrade:"

    val invest: String = "[1-9][0-9]*"
    val wrongInput: String = ".*"

    def doInvest(input: String): (String, BrowseInterpreter) = (TradingMethods.currentTrade(new Trade(input.toDouble, 200, 300, 2, LocalDate.parse("2023-10-26"), "TSLA")), new BrowseInterpreter)

    def doWrongInput(input: String): (String, InvestInterpreter) = ("Wrong input. Pleas type a numbers", this)

    override val actions: Map[String, String => (String, Interpreter)] =
        Map((wrongInput, doWrongInput),(invest,doInvest))
}