package TradingGame 

class MenuInterpreter extends Interpreter {

    override val descriptor: String = "Welcome to TradingChampions!\n\nChoose your Starting Balance:\n"

    val balance: String = "[1-9][0-9]*"
    val wrongInput: String = ".*"

    def doBalance(input: String): (String, BrowseInterpreter) = ("", new BrowseInterpreter(input))

    def doWrongInput(input: String): (String, MenuInterpreter) = ("Wrong input. Please type a number!", this)

    override val actions: Map[String, String => (String, Interpreter)] =
        Map((wrongInput, doWrongInput),(balance,doBalance))
}