package de.htwg.se.TradingGame.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.TradeActive
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.Trade


class TradeActiveSpec extends AnyWordSpec with Matchers {
    //try
    "A TradeActive" when {

        val tradeActive = new TradeActive(Trade(1.0, 1.0, 1.0, 1.0, "2023.02.02,12:12", "EURUSD"), isActive = true, _currentProfit = 100.0)

        "created" should {
            "have the correct initial values" in {
                tradeActive.isActive shouldBe true
                tradeActive.currentProfit shouldBe 100.0
            }
        }
        "setting the current profit" should {
            "update the current profit value" in {
                tradeActive.setcurrentProfitto(200.0)
                tradeActive.currentProfit shouldBe 200.0
            }
        }
    }
}
