package de.htwg.se.TradingGame.view
import de.htwg.se.TradingGame.model._
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import de.htwg.se.TradingGame.model.TradeDecoratorPattern._
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import javafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.chart.LineChart
import scalafx.scene.chart.{CategoryAxis, LineChart, NumberAxis, XYChart}
import scalafx.scene.control.{Button, ComboBox, Label, TextField, TableColumn, TableView}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.control.Tooltip
import scalafx.Includes._
import scalafx.scene.control.cell.TextFieldTableCell
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import de.htwg.se.TradingGame.model.TradingMethods
import de.htwg.se.TradingGame.model.GetMarketData
import de.htwg.se.TradingGame.model.GetMarketData.calculateTrade
import de.htwg.se.TradingGame.model.GetMarketData.calculateTradeProfit
import de.htwg.se.TradingGame.model.GetMarketData.calculateTradecurrentProfit
import scalafx.scene.input.MouseEvent
import scalafx.scene.shape.Line
import scalafx.scene.layout.BorderPane
import scalafx.scene.control._
import scalafx.scene.text.Font                                       
import scala.collection.mutable.ListBuffer                                        
import javafx.event.EventHandler


                                            

object Gui2 extends JFXApp3 {

    val executedTrades: ObservableBuffer[TradeDoneCalculations] = ObservableBuffer[TradeDoneCalculations]()


    override def start(): Unit = {
        val welcomeLabel = new Label("Welcome to the Tradinggame \n Please enter your balance:")
        val balanceLabel = new Label("Balance: ")
        var balanceString = 0.0
        var startBalance = 0.0
        val balanceTextField = new TextField()
        val submitButton = new Button("Submit")
        val balanceHBox = new HBox(balanceLabel, new Label("0"))
        val inputHBox = new HBox(balanceTextField, submitButton)
        val tickerLabel = new Label("Ticker: ")
        val tickerComboBox = new ComboBox(List("EURUSD", "AUDUSD"))
        val tickerHBox = new HBox(tickerLabel, tickerComboBox)
        val dateLabel = new Label("Date: ")
        val dateTextField = new TextField()
        val hourLabel = new Label("Hour: ")
        val hourTextField = new TextField()
        val submitDateTimeButton = new Button("Submit")
        val dateTimeHBox = new HBox(dateLabel, dateTextField, hourLabel, hourTextField, submitDateTimeButton)
                                                

        


        val tradesTable = new TableView[TradeDoneCalculations](executedTrades) {
        columns ++= Seq(
            new TableColumn[TradeDoneCalculations, Double] {
                text = "Entry"
                cellValueFactory = cellData => ObjectProperty(cellData.value.trade.entryTrade)
            },
            new TableColumn[TradeDoneCalculations, Double] {
                text = "Stop Loss"
                cellValueFactory = cellData => ObjectProperty(cellData.value.trade.stopLossTrade)
            },
            new TableColumn[TradeDoneCalculations, Double] {
                text = "Take Profit"
                cellValueFactory = cellData => ObjectProperty(cellData.value.trade.takeProfitTrade)
            },
            new TableColumn[TradeDoneCalculations, Double] {
                text = "Risk %"
                cellValueFactory = cellData => ObjectProperty(cellData.value.trade.risk)
            },
            new TableColumn[TradeDoneCalculations, String] {
                text = "Date/Time"
                cellValueFactory = cellData => ObjectProperty(cellData.value.trade.datestart)
            },
            new TableColumn[TradeDoneCalculations, String] {
                text = "Ticker"
                cellValueFactory = cellData => ObjectProperty(cellData.value.trade.ticker)
                        },
            new TableColumn[TradeDoneCalculations, String] {
                text = "dateTradeTiggered"
                cellValueFactory = cellData => ObjectProperty(cellData.value.dateTradeTriggered)
            },
            new TableColumn[TradeDoneCalculations, String] {
                text = "dateTradeDone"
                cellValueFactory = cellData => ObjectProperty(cellData.value.dateTradeDone)
            },
            new TableColumn[TradeDoneCalculations, String] {
                text = "TradeWinnorLoose"
                cellValueFactory = cellData => ObjectProperty(cellData.value.tradeWinOrLose)
            },
            new TableColumn[TradeDoneCalculations, String] {
                text = "tradeBuyorSell"
                cellValueFactory = cellData => ObjectProperty((cellData.value.trade.takeProfitTrade > cellData.value.trade.stopLossTrade).toString())
            },
            new TableColumn[TradeDoneCalculations, String] {
                text = "Profit/Loss"
                cellValueFactory = cellData => ObjectProperty(calculateTradeProfit(cellData.value, startBalance).toString())
            },
            new TableColumn[TradeDoneCalculations, String] {
                text = "Current Profit/Loss"
                cellValueFactory = cellData => ObjectProperty(calculateTradecurrentProfit(cellData.value, startBalance, s"${dateTextField.text.value},${hourTextField.text.value}").toString())
            },
                    )
                }
                    

                   
                    val dataPointsLabel = new Label("Data Points: ")
                    val dataPointsTextField = new TextField()
                    val timeFrameLabel = new Label("Time Frame: ")
                    val timeFrameComboBox = new ComboBox(List("1min", "5min", "15min", "1hour", "4hour", "1day"))
                    val dateTimeDataPointsHBox = new HBox(dateTimeHBox, dataPointsLabel, dataPointsTextField, timeFrameLabel, timeFrameComboBox)
                    val entryLabel = new Label("Entry: ")
                    val entryTextField = new TextField()
                    val takeProfitLabel = new Label("Take Profit: ")
                    val takeProfitTextField = new TextField()
                    val stopLossLabel = new Label("Stop Loss: ")
                    val stopLossTextField = new TextField()
                    val riskLabel = new Label("Risk %: ")
                    val riskTextField = new TextField()
                    val executeButton = new Button("Execute")
                    val entryTakeProfitStopLossRiskHBox = new HBox(entryLabel, entryTextField, takeProfitLabel, takeProfitTextField, stopLossLabel, stopLossTextField, riskLabel, riskTextField, executeButton)
                    val xAxis = NumberAxis()
                    val yAxis = NumberAxis()
                    val lineChart = new LineChart(xAxis, yAxis)
                    val graphVBox = new VBox(new Label(""), lineChart, dateTimeDataPointsHBox, entryTakeProfitStopLossRiskHBox)
                    val vBox = new VBox(welcomeLabel, inputHBox, tickerHBox, balanceHBox, graphVBox, tradesTable)

                    val getEntryButton = new Button("get entry")
                    val getProfitButton = new Button("get Profit")
                    val getStoplossButton = new Button("get Stoploss")
                    var getEntry = false
                    var getProfit = false
                    var getStoploss = false
                    var entry = 0.0
                    var takeProfit = 0.0
                    var stopLoss = 0.0

                    getEntryButton.onAction = _ => {
                        getEntry = true
                        getProfit = false
                        getStoploss = false
                    }

                    getProfitButton.onAction = _ => {
                        getEntry = false
                        getProfit = true
                        getStoploss = false
                    }

                    getStoplossButton.onAction = _ => {
                        getEntry = false
                        getProfit = false
                        getStoploss = true
                    }

                    lineChart.setOnMouseClicked(new EventHandler[javafx.scene.input.MouseEvent] {
                        override def handle(event:javafx.scene.input.MouseEvent): Unit = {
                            if (getEntry) {
                                entry = yAxis.getValueForDisplay(event.getY).doubleValue()
                                entryTextField.text.value = entry.toString
                            } else if (getProfit) {
                                takeProfit = yAxis.getValueForDisplay(event.getY).doubleValue()
                                takeProfitTextField.text.value = takeProfit.toString
                            } else if (getStoploss) {
                                stopLoss = yAxis.getValueForDisplay(event.getY).doubleValue()
                                stopLossTextField.text.value = stopLoss.toString
                            }
                        }
                    })

                                                
                                        vBox.setSpacing(10)

                                        vBox.setPadding(new Insets(10))

                                        submitButton.onAction = _ => {
                                            balanceHBox.children.remove(1)
                                            balanceHBox.children.remove(0)
                                            balanceHBox.children.add(new Label("Balance: " + balanceTextField.text.value + "EUR" +"  Profit:"))
                                            balanceString = balanceTextField.text.value.toDouble
                                            startBalance = balanceString
                                            vBox.children.remove(inputHBox)
                                            tickerComboBox.onAction = _ => {
                                                graphVBox.children.remove(0)
                                                graphVBox.children.add(0, new Label(tickerComboBox.value.value))
                                                vBox.children.add(graphVBox)
                                            }
                                        }

                                        def filterData(data: List[(LocalDateTime, Double)], dateTime: LocalDateTime, dataPoints: Int, timeFrame: String): List[(LocalDateTime, Double)] = {
                                            val filteredData = timeFrame match {
                                                case "1min" => data.filter(_._1.isBefore(dateTime)).takeRight(dataPoints)
                                                case "5min" => data.filter(_._1.isBefore(dateTime)).filter(_._1.getMinute % 5 == 0).takeRight(dataPoints)
                                                case "15min" => data.filter(_._1.isBefore(dateTime)).filter(_._1.getMinute % 15 == 0).takeRight(dataPoints)
                                                case "1hour" => data.filter(_._1.isBefore(dateTime)).filter(_._1.getMinute == 0).takeRight(dataPoints)
                                                case "4hour" => data.filter(_._1.isBefore(dateTime)).filter(d => d._1.getMinute == 0 && List(0, 4, 8, 12, 16, 20).contains(d._1.getHour)).takeRight(dataPoints)
                                                case "1day" => data.filter(_._1.isBefore(dateTime)).filter(d => d._1.getMinute == 0 && d._1.getHour == 0).takeRight(dataPoints)
                                            }
                                            filteredData
                                        }

                                        submitDateTimeButton.onAction = _ => {
                                        
                                            if(executedTrades.length > 0){
                                                balanceHBox.children.remove(0)
                                                var calculatecurrentProfit = 0.0
                                                balanceString = startBalance
                                                executedTrades.foreach(trade => {
                                                    calculatecurrentProfit += calculateTradecurrentProfit(trade, startBalance, s"${dateTextField.text.value},${hourTextField.text.value}")
                                                    
                                                })
                                                balanceString += calculatecurrentProfit
                                                balanceHBox.children.add(new Label(balanceString + "EUR" +"  Profit: " + calculatecurrentProfit + "EUR"))
                                            }
                                            vBox.children.remove(inputHBox)
                                            tickerComboBox.onAction = _ => {
                                                graphVBox.children.remove(0)
                                                graphVBox.children.add(0, new Label(tickerComboBox.value.value))
                                                vBox.children.add(graphVBox)
                                            }
                                        
                                            val dateTime = LocalDateTime.parse(s"${dateTextField.text.value},${hourTextField.text.value}", DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm"))
                                            val symbol = tickerComboBox.value.value
                                            val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
                                            val file = new java.io.File(Path).getParent + s"/Symbols/$symbol.csv"
                                            val fileObj = new File(file)
                                            if (fileObj.exists()) {
                                                val dataPoints = dataPointsTextField.text.value.toInt
                                                val data = io.Source.fromFile(file).getLines().toList.drop(1).map(_.split(",")).map(arr => (LocalDateTime.parse(arr(0)+ "," +  arr(1), DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")), arr(5).toDouble))
                                                val filteredData = filterData(data, dateTime, dataPoints, timeFrameComboBox.value.value)
                                                val series = new javafx.scene.chart.XYChart.Series[Number, Number]()
                                                filteredData.foreach(d => series.getData.add(new javafx.scene.chart.XYChart.Data[Number, Number](d._1.toEpochSecond(java.time.ZoneOffset.UTC), d._2)))
                                                lineChart.getData.clear()
                                                lineChart.getData.add(series)
                                                xAxis.setAutoRanging(false)
                                                xAxis.setLowerBound(filteredData.head._1.toEpochSecond(java.time.ZoneOffset.UTC))
                                                xAxis.setUpperBound(filteredData.last._1.toEpochSecond(java.time.ZoneOffset.UTC))
                                                yAxis.setAutoRanging(false)
                                                yAxis.setLowerBound(filteredData.map(_._2).min)
                                                yAxis.setUpperBound(filteredData.map(_._2).max)
                                                yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()))
                                                
                                            }
                                        }
                                        

                                        executeButton.onAction = _ => {
                                        val entry = entryTextField.text.value.toDouble
                                        val takeProfit = takeProfitTextField.text.value.toDouble
                                        val stopLoss = stopLossTextField.text.value.toDouble
                                        val risk = riskTextField.text.value.toDouble
                                        val dateTime = s"${dateTextField.text.value},${hourTextField.text.value}"
                                        val ticker = tickerComboBox.value.value
                                        val trade = calculateTrade(Trade(entry, stopLoss, takeProfit, risk, dateTime, ticker))
                                        executedTrades += trade
                                        tradesTable.refresh()
                                    }
                                        
                                        val arrowButton = new Button(">")
                                        arrowButton.onAction = _ => {
                                            if (executedTrades.length > 0) {
                                                balanceHBox.children.remove(0)
                                                var calculatecurrentProfit = 0.0
                                                balanceString = startBalance
                                                executedTrades.foreach(trade => {
                                                    calculatecurrentProfit += calculateTradecurrentProfit(trade, startBalance, s"${dateTextField.text.value},${hourTextField.text.value}")
                                                })
                                                balanceString += calculatecurrentProfit
                                                balanceHBox.children.add(new Label(balanceString + "EUR" + "  Profit: " + calculatecurrentProfit + "EUR"))
                                            }
                                            val timeFrame = timeFrameComboBox.value.value
                                            val dateTime = LocalDateTime.parse(s"${dateTextField.text.value},${hourTextField.text.value}", DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm"))
                                            val newDateTime = timeFrame match {
                                                case "1min" => dateTime.plusMinutes(1)
                                                case "5min" => dateTime.plusMinutes(5)
                                                case "15min" => dateTime.plusMinutes(15)
                                                case "30min" => dateTime.plusMinutes(30)
                                                case "1hour" => dateTime.plusHours(1)
                                                case "4hour" => dateTime.plusHours(4)
                                                case "1day" => dateTime.plusDays(1)
                                                case "1week" => dateTime.plusWeeks(1)
                                            }
                                            dateTextField.text.value = newDateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                                            hourTextField.text.value = newDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                                            val symbol = tickerComboBox.value.value
                                            val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
                                            val file = new java.io.File(Path).getParent + s"/Symbols/$symbol.csv"
                                            val newDateTimeinFile = GetMarketData.nextPossibleDateinFile(newDateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")), file)
                                            val priceOfNewDate = GetMarketData.getPriceForDateTimeDouble(newDateTimeinFile, file, 5)
                                                val series = lineChart.getData.get(0)
                                                series.getData.add(new javafx.scene.chart.XYChart.Data[Number, Number](LocalDateTime.parse(newDateTimeinFile, DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")).toEpochSecond(java.time.ZoneOffset.UTC), priceOfNewDate))
                                                xAxis.setUpperBound(LocalDateTime.parse(newDateTimeinFile, DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")).toEpochSecond(java.time.ZoneOffset.UTC))
                                                yAxis.setLowerBound(math.min(yAxis.getLowerBound(), priceOfNewDate))
                                                yAxis.setUpperBound(math.max(yAxis.getUpperBound(), priceOfNewDate))
                                                yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()))
                                            }
                                        


                                        val arrowHBox = new HBox(arrowButton)
                                        graphVBox.children.addAll(arrowHBox,  getEntryButton, getProfitButton, getStoplossButton)

                                        stage = new PrimaryStage {
                                            title = "Tradinggame"
                                            scene = new Scene(vBox, 400, 400)
                                        }
                                    }
                                }
 