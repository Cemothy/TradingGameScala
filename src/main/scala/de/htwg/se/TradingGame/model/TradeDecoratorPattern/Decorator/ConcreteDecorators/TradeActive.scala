package de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators

import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.TradeDecorator


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
