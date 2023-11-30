package de.htwg.se.TradingGame.model 
import de.htwg.se.TradingGame.model._

class MenuInterpreter extends Interpreter {

    override val balance: String = "1000"
    override val descriptor: String = "Welcome to TradingChampions!\n\nChoose your Starting Balance:\n"

    val balanceInput: String = "[1-9][0-9]*"
    val wrongInput: String = ".*"

    def doBalance(input: String): (String, BrowseInterpreter) = ("", new BrowseInterpreter(input))

    def doWrongInput(input: String): (String, MenuInterpreter) = ("Wrong input. Please type a number!", this)

    override val actions: Map[String, String => (String, Interpreter)] =
        Map((wrongInput, doWrongInput),(balanceInput,doBalance))
}