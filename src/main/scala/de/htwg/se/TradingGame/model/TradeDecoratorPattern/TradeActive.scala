package de.htwg.se.TradingGame.model.TradeDecoratorPattern
import de.htwg.se.TradingGame.model._

class TradeActive(
  trade: TradeComponent,
  var isActive: Boolean,
  var _currentProfit: Double
) extends TradeDecorator(trade) {
  def currentProfit: Double = _currentProfit

  def setcurrentProfitto(value: Double): Unit = {
    _currentProfit = value
  }
}
