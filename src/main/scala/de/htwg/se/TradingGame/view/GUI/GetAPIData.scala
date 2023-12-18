package de.htwg.se.TradingGame.view.GUI

import de.htwg.se.TradingGame.view.GUI.AdvCandleStickChartSample.CandleStick
import play.api.libs.json._

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ListBuffer
import scala.io.Source

object GetAPIData extends App {
    val apiKey = "OGZ3PB587B2OUFI4"

    def getCandleSticks(interval: String, symbol: String, endDate: LocalDateTime): ListBuffer[CandleStick] = {
        getCandleSticks(interval, symbol, endDate, 0)
    }

    def getCandleSticks(interval: String, symbol: String, endDate: LocalDateTime, size: Int): ListBuffer[CandleStick] = {

        val month = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        interval match {
            case "1min" => AdvCandleStickChartSample.distancecandles = 60
            case "5min" => AdvCandleStickChartSample.distancecandles = 60 * 5
            case "15min" => AdvCandleStickChartSample.distancecandles = 60 * 15
            case "60min" => AdvCandleStickChartSample.distancecandles = 60 * 60
            case "daily" => AdvCandleStickChartSample.distancecandles = 60 * 60 * 24
            case "weekly" => AdvCandleStickChartSample.distancecandles = 60 * 60 * 24 * 7
            case "monthly" => AdvCandleStickChartSample.distancecandles = 60 * 60 * 24 * 30
            case _ => throw new IllegalArgumentException("Invalid interval")
        }
        val url = s"https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=$symbol&interval=$interval&month=$month&outputsize=full&apikey=$apiKey"
        println(url)
        val result = Source.fromURL(url).mkString
        val json = Json.parse(result)
        if ((json \ "Error Message").isDefined) {
            println((json \ "Error Message").as[String])
            return ListBuffer[CandleStick]()
        }
        val timeSeries = (json \ s"Time Series ($interval)").as[JsObject]
        val candleSticks = ListBuffer[CandleStick]()



        timeSeries.keys.foreach { time =>
            val data = timeSeries(time).as[JsObject]
            val open = (data \ "1. open").as[String].toDouble
            val high = (data \ "2. high").as[String].toDouble
            val low = (data \ "3. low").as[String].toDouble
            val close = (data \ "4. close").as[String].toDouble

            val dateTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))


            if (!dateTime.isAfter(endDate)) {
            val candleStick = CandleStick(
                day = dateTime.atZone(ZoneId.systemDefault()).toEpochSecond,
                open = open,
                close = close,
                high = high,
                low = low,
            )
            if (candleSticks.size < 1000) {
                candleSticks += candleStick
            } else {
                candleSticks
            }
            }
        }
        val zize = size + candleSticks.size
        println("zize: " + zize)
          if (zize < 1000) {
            val startDate = endDate.minusMonths(1).withDayOfMonth(endDate.minusMonths(1).toLocalDate.lengthOfMonth())
            val newsize = size + candleSticks.size
            println("newsize: " + newsize) 
            candleSticks ++= getCandleSticks(interval, symbol, startDate, newsize)
        }
        candleSticks
        }


        def getLastCandleStick(interval: String, symbol: String, endDate: LocalDateTime): CandleStick = {
            val month = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM"))
            interval match {
                case "1min" => AdvCandleStickChartSample.distancecandles = 60
                case "5min" => AdvCandleStickChartSample.distancecandles = 60 * 5
                case "15min" => AdvCandleStickChartSample.distancecandles = 60 * 15
                case "60min" => AdvCandleStickChartSample.distancecandles = 60 * 60
                case "daily" => AdvCandleStickChartSample.distancecandles = 60 * 60 * 24
                case "weekly" => AdvCandleStickChartSample.distancecandles = 60 * 60 * 24 * 7
                case "monthly" => AdvCandleStickChartSample.distancecandles = 60 * 60 * 24 * 30
                case _ => throw new IllegalArgumentException("Invalid interval")
            }
            val url = s"https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=$symbol&interval=$interval&month=$month&outputsize=full&apikey=$apiKey"
            val result = Source.fromURL(url).mkString
            val json = Json.parse(result)
            if ((json \ "Error Message").isDefined) {
                println((json \ "Error Message").as[String])
                throw new NoSuchElementException((json \ "Error Message").as[String])
            }
            val timeSeries = (json \ s"Time Series ($interval)").as[JsObject]

            timeSeries.keys.foreach { time =>
                val dateTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                if (!dateTime.isAfter(endDate)) {
                    val data = timeSeries(time).as[JsObject]
                    val open = (data \ "1. open").as[String].toDouble
                    val high = (data \ "2. high").as[String].toDouble
                    val low = (data \ "3. low").as[String].toDouble
                    val close = (data \ "4. close").as[String].toDouble

                    return CandleStick(
                        day = dateTime.atZone(ZoneId.systemDefault()).toEpochSecond,
                        open = open,
                        close = close,
                        high = high,
                        low = low,
                    )
                }
            }

            throw new NoSuchElementException("No candlestick found")
        }
    }