package de.htwg.se.TradingGame.view.GUI
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.ComboBox
import java.io.File

class TickerSelection {
  def createTickerDropdown(): ComboBox[String] = {
    val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
    val file = new File(Path).getParent + s"/Symbols"
    val folder = new File(file)
    val csvFiles = folder.listFiles.filter(_.getName.endsWith(".csv"))
    val tickerOptions = ObservableBuffer(csvFiles.map(_.getName.stripSuffix(".csv")): _*)
    val tickerDropdown = new ComboBox[String](tickerOptions)
    tickerDropdown.promptText = "Select Ticker"
    if (tickerOptions.nonEmpty) {
      tickerDropdown.value = tickerOptions.last
    }
    
    tickerDropdown
  }
}
