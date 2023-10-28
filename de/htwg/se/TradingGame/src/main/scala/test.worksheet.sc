object TickerSymbol extends Enumeration {
  type TickerSymbol = Value

  val TSLA = Value("Tesla")
  val AAPL = Value("Apple")
  val AMZN = Value("Amazon Corporation")
  val MCD  = Value("McDonalds")
}

val enumString = printEnums(TickerSymbol)


def printEnums(enum: Enumeration): String = {
  var string = ""
  enum.values.foreach { value =>
    string + ${value.toString} -> ${value.id}
  }
}
//
val a = 5+5
//