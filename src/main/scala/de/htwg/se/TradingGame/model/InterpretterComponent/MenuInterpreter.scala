package de.htwg.se.TradingGame.model.InterpretterComponent 
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.model.InterpretterComponent._
import com.google.inject.Inject
import de.htwg.se.TradingGame.model.DataSave.TradeDataclass

class MenuInterpreter @Inject() (tradeData: TradeDataclass) extends Interpreter {

    override val balance: String = "1000"
    override val descriptor: String = "Welcome to TradingChampions!\n\nChoose your Starting Balance:\n"

    val balanceInput: String = "[1-9][0-9]*"
    val wrongInput: String = ".*"

    def doBalance(input: String): (String, BrowseInterpreter) = ("", new BrowseInterpreter(input, tradeData))

    def doWrongInput(input: String): (String, MenuInterpreter) = ("Wrong input. Please type a number!", this)
    
    override def resetState: Interpreter = {
        // Reset the state by creating a new instance with the default balance
        new MenuInterpreter(tradeData)
    }
    override def interpreterType: String = "MenuInterpreter"

    override val actions: Map[String, String => (String, Interpreter)] =
        Map((wrongInput, doWrongInput),(balanceInput,doBalance))
}