package de.htwg.se.TradingGame.view.GUI
import scalafx.scene.shape.{Rectangle, Line}
import scalafx.scene.paint.Color
import scalafx.scene.Group

class CandleStick(x: Double, open: Double, close: Double, high: Double, low: Double) extends Group {
  val candle = new Rectangle ()
    candle.x = x
    candle.width = 10
    candle.height = Math.abs(open - close)
    candle.y = Math.min(open, close)
    candle.fill = if (open > close) Color.Red else Color.Green
  

  val highLowLine = new Line {
    startX = x + 5
    endX = x + 5
    startY = high
    endY = low
  }

  children = List(candle, highLowLine)
}