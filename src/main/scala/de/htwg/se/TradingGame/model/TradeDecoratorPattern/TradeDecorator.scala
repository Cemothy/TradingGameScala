package de.htwg.se.TradingGame.model.TradeDecoratorPattern

trait TradeDecorator(val trade: TradeComponent) extends TradeComponent {
    def entryTrade: Double = trade.entryTrade
    def stopLossTrade: Double = trade.stopLossTrade
    def takeProfitTrade: Double = trade.takeProfitTrade
    def risk: Double = trade.risk
    def datestart: String = trade.datestart
    def ticker: String = trade.ticker
}

