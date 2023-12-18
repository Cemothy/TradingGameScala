package de.htwg.se.TradingGame.view.GUI

import play.api.libs.json._

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import scala.io.Source

object GetAPINewsSentiment extends App {
  val apiKey = "OGZ3PB587B2OUFI4"

def getNewsSentiment(symbol: String): JsValue = {
  val url = s"https://www.alphavantage.co/query?function=NEWS_SENTIMENT&tickers=$symbol&limit=1000&apikey=$apiKey"
  println(url)
  val result = Source.fromURL(url).mkString
  val json = Json.parse(result)

  if ((json \ "Error Message").isDefined) {
    println((json \ "Error Message").as[String])
    return JsNull
  }

  // Create a custom DateTimeFormatter
  val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")

  // Extract the feed data from the JSON response
  val feedData = (json \ "feed").as[JsArray].value

  // Extract the ticker sentiment score, relevance score, and date
  val scores = feedData.flatMap { item =>
    val dateString = (item \ "time_published").as[String]
    val date = LocalDateTime.parse(dateString, formatter)
    val epochTime = date.toEpochSecond(ZoneOffset.UTC)
    val tickerSentiment = (item \ "ticker_sentiment").as[JsArray].value
    tickerSentiment.map { ticker =>
      val tickerName = (ticker \ "ticker").as[String]
      val relevanceScore = (ticker \ "relevance_score").as[String]
      val tickerSentimentScore = (ticker \ "ticker_sentiment_score").as[String]
      val weightedSentiment = relevanceScore.toDouble * tickerSentimentScore.toDouble
      (epochTime, tickerName, weightedSentiment)
    }
  }

  // Filter the scores to include only the specified symbol
  val filteredScores = scores.filter { case (_, tickerName, _) => tickerName == symbol }

  // Return the filtered scores
  val data = JsArray(filteredScores.map { case (epochTime, tickerName, weightedSentiment) =>
    Json.obj(
      "date" -> epochTime,
      "weighted_sentiment" -> weightedSentiment
    )
  })
  //print(data)
  data
}
  //val sentimentData = GetAPINewsSentiment.getNewsSentiment("AAPL")
}