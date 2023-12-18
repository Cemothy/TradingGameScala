package de.htwg.se.TradingGame.view.GUI

import java.io.PrintWriter
import scala.io.Source

object getString {
  def main(args: Array[String]): Unit = {
    val source = Source.fromFile("C:\\Users\\Samuel\\Documents\\SoftwareEngeneering\\TradingGameScala\\src\\main\\scala\\de\\htwg\\se\\TradingGame\\view\\GUI\\string.txt")
    val lines = source.getLines().toList
    source.close()

    val newLines = lines.zipWithIndex.filter { case (_, index) => index % 4 == 3 }.map(_._1)
    newLines.foreach(println)

  }
}