package de.htwg.se.TradingGame.model

import java.io.File

object FileChooser {
    def getSymbolPath(ticker: String): String = {
        val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
        val file = new File(Path).getParent + s"/Symbols/$ticker.csv"
        file
    }
}
