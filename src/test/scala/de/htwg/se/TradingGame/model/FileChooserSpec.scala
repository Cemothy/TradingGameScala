package de.htwg.se.TradingGame.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import java.io.File

class FileChooserSpec extends AnyWordSpec with Matchers {
    "FileChooser" should {
        "return the correct symbol path" in {
            val ticker = "AAPL"
            val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
            val expectedPath = new File(Path).getParent + s"/Symbols/$ticker.csv"
            
            val actualPath = FileChooser.getSymbolPath(ticker)
            
            actualPath should be(expectedPath)
        }
    }
}
