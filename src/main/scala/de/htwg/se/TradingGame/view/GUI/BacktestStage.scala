package de.htwg.se.TradingGame.view.GUI

import de.htwg.se.TradingGame.Main.controller
import de.htwg.se.TradingGame.controller.IController
import de.htwg.se.TradingGame.model.DataSave.TradeData
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Trade
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeActive
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeDoneCalculations
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeWithVolume
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeisBuy
import de.htwg.se.TradingGame.view.GUI.AdvCandleStickChartSample._
import de.htwg.se.TradingGame.view.GUI.BalanceStage
import de.htwg.se.TradingGame.view.GUI.GetAPIData._
import de.htwg.se.TradingGame.view.GUI.GetDatabaseData._
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.shape.Line
import scalafx.Includes._
import scalafx.animation.KeyFrame
import scalafx.animation.Timeline
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Orientation
import scalafx.geometry.Point2D
import scalafx.scene.Scene
import scalafx.scene.chart.LineChart
import scalafx.scene.chart.NumberAxis
import scalafx.scene.chart.XYChart
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.Button
import scalafx.scene.control.ComboBox
import scalafx.scene.control.DatePicker
import scalafx.scene.control.Label
import scalafx.scene.control.Slider
import scalafx.scene.control.Spinner
import scalafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory
import scalafx.scene.control.SplitPane
import scalafx.scene.control.TableColumn
import scalafx.scene.control.TableColumn.CellDataFeatures
import scalafx.scene.control.TableView
import scalafx.scene.control.TextField
import scalafx.scene.control.ToggleButton
import scalafx.scene.input.KeyCode
import scalafx.scene.input.KeyCode.B
import scalafx.scene.input.KeyEvent
import scalafx.scene.input.MouseButton
import scalafx.scene.input.MouseEvent
import scalafx.scene.input.ScrollEvent
import scalafx.scene.layout.HBox
import scalafx.scene.layout.Pane
import scalafx.scene.layout.Priority
import scalafx.scene.layout.Region
import scalafx.scene.layout.StackPane
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.util.Duration

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object BacktestStage extends JFXApp3 {

    override def start(): Unit = 
        val stage = new BacktestStage(controller).createStage()
        stage.show()

}
class BacktestStage(controller: IController){
    val sizecandles = 3500

    var data = getCandleSticksdadabase("1h", "EURUSD", LocalDateTime.now(), sizecandles)

    val chart = createChart(data)
    val chartPane = new DraggableCandleStickChart(chart)
    var dragStartX: Double = 0
    var dragStartY: Double = 0
    var summprofit = 0.0
    var nextClickAction: String = ""
    val crosshairPane = new Pane()
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
    val crosshair = new Crosshair(crosshairPane) // Pass the crosshairPane instead of chartPane
    crosshair.createCrosshair()
    val dateLabelcross = new Label {
    textFill = Color.Black
    style = "-fx-background-color: white; -fx-padding: 5;"

    }
    val timeframeOptions = ObservableBuffer("1m", "5m", "15m", "1h", "4h", "1d", "1w")
    val timeframeComboBox = new ComboBox[String](timeframeOptions)
    val priceLabelcross = new Label {
        textFill = Color.Black
        style = "-fx-background-color: white; -fx-padding: 5;"
    }
    val tickerComboBox = new TextField {
        promptText = "Enter Ticker"
        text = "EURUSD"
    }
    val chartWithCrosshair = new StackPane()
    chartWithCrosshair.children.addAll(chartPane, crosshairPane)
    val datepane = new Pane()
    val pricepane = new Pane()
    datepane.mouseTransparent = true
    pricepane.mouseTransparent = true
    datepane.children.add(dateLabelcross)
    pricepane.children.add(priceLabelcross)
    chartWithCrosshair.children.addAll(datepane, pricepane)
    dateLabelcross.mouseTransparent = true
    priceLabelcross.mouseTransparent = true
    crosshairPane.mouseTransparent = true



    chartWithCrosshair.onMouseExited = (me: MouseEvent) => {
        crosshairPane.setVisible(false)
    }

    chartWithCrosshair.onMouseEntered = (me: MouseEvent) => {
        crosshairPane.setVisible(true)
    }

   

    
    val entry = new TextField()
        entry.onKeyPressed = (keyEvent: KeyEvent) => {
            // Check if Enter key was pressed
            if (keyEvent.code == KeyCode.Enter) {
                // Get price from TextField
                val priceText = entry.text.value

                // Plot horizontal line at price
                chartPane.entryline(priceText)
            }
        }
        val takeProfit = new TextField()
        takeProfit.onKeyPressed = (keyEvent: KeyEvent) => {
            if (keyEvent.code == KeyCode.Enter) {
                val priceText = takeProfit.text.value
                chartPane.takeProfitLine(priceText)
            }
        }

        val stopLoss = new TextField()
        stopLoss.onKeyPressed = (keyEvent: KeyEvent) => {
            if (keyEvent.code == KeyCode.Enter) {
                val priceText = stopLoss.text.value
                chartPane.stopLossLine(priceText)
            }
        }
    chartWithCrosshair.onMouseMoved = (me: MouseEvent) => {
        chartPane.updateAllLines()
        crosshair.updateCrosshair(me)
        entry.text = chartPane.entryprice
        takeProfit.text = chartPane.takeProfitPrice
        stopLoss.text = chartPane.stopLossPrice
        val epochSeconds = chartPane.calculateXDate(me)
        val instant = Instant.ofEpochSecond(epochSeconds.toLong)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        
        val dateString = localDateTime.format(formatter)
        val price = chartPane.calculateYPrice(me)

        // Update the labels
        dateLabelcross.text = dateString
        priceLabelcross.text = price

        // Position the labels
        dateLabelcross.layoutX = me.getX
        dateLabelcross.layoutY = chartPane.height.value - dateLabelcross.height.value
        priceLabelcross.layoutX = 0
        priceLabelcross.layoutY = me.getY
    }
    chartWithCrosshair.onMousePressed = (me: MouseEvent) => {
        chartPane.updateOnMousePress(me)
        chartPane.updateAllLines()
    }

    chartWithCrosshair.onMouseDragged = (me: MouseEvent) => {
        chartPane.updateOnDrag(me)
        crosshair.updateCrosshair(me)
        chartPane.updateAllLines()
    if (getLowestXTimeisinBounds(chart) && (gettingData == false)){
        val epochSeconds = getLowestXTime(chart).get
        val dateTime = LocalDateTime.ofEpochSecond(epochSeconds.toLong, 0, ZoneOffset.UTC)
        adjustCandlestoleft(chart, timeframeComboBox.value.value, tickerComboBox.text.value, dateTime, 1000)
    } else if(getHighestXTimeIsInBounds(chart) && (gettingData == false)){
        val epochSeconds = getHighestXTime(chart).get
        val dateTime = LocalDateTime.ofEpochSecond(epochSeconds.toLong, 0, ZoneOffset.UTC)
        adjustCandlesToRight(chart, timeframeComboBox.value.value, tickerComboBox.text.value, dateTime, 1000)
    }
}

    chartWithCrosshair.onMouseReleased = (me: MouseEvent) => {
        chartPane.updateOnMouseRelease()
        chartPane.updateAllLines()
    }

    chartWithCrosshair.onScroll = (me: ScrollEvent) => {
        chartPane.updateOnScroll(me)
        chartPane.updateAllLines()
            //   // Get the lower bound of the x-axis
            // val xAxis = chart.getXAxis
            // val getLowerBoundMethod = xAxis.getClass.getMethod("getLowerBound")
            // val xAxisLowerBound = getLowerBoundMethod.invoke(xAxis).asInstanceOf[Double]

            // // Get the day of the last candle that is most left
            // val lastCandleMostLeftDay = if (chart.data.nonEmpty && chart.data.head.getData.nonEmpty) {
            //     chart.data.head.getData.head.getXValue.asInstanceOf[Double]
            // } else Double.MaxValue
            // val instant = Instant.ofEpochSecond(lastCandleMostLeftDay.toLong)
            // val lastCandleMostLeftDayDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            // // If the lower x bound is left from the last candle that is most left
            // if (xAxisLowerBound < lastCandleMostLeftDay) {


            // }
    }

     val dateInput = new TextField {
        promptText = "Enter Date"
        text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm"))
     }
    def createStage(): PrimaryStage = {
        val entryCollum = new TableColumn[TradeDoneCalculations, Double] {
            text = "Entry Price"
            cellValueFactory = { (features: CellDataFeatures[TradeDoneCalculations, Double]) => 
                ObjectProperty(features.value.entryTrade)
                }
            }
        val stoplossCollum = new TableColumn[TradeDoneCalculations, Double] {
            text = "Stop Loss"
            cellValueFactory = { (features: CellDataFeatures[TradeDoneCalculations, Double]) => 
                ObjectProperty(features.value.stopLossTrade)
                }
            }
        val takeprofitCollum = new TableColumn[TradeDoneCalculations, Double] {
            text = "Take Profit"
            cellValueFactory = { (features: CellDataFeatures[TradeDoneCalculations, Double]) => 
                ObjectProperty(features.value.takeProfitTrade)
                }
            }
        val riskCollum = new TableColumn[TradeDoneCalculations, Double] {
            text = "Risk"
            cellValueFactory = { (features: CellDataFeatures[TradeDoneCalculations, Double]) => 
                ObjectProperty(features.value.risk)
                }
            }
        val dateCollum = new TableColumn[TradeDoneCalculations, String] {
            text = "Date"
            cellValueFactory = { (features: CellDataFeatures[TradeDoneCalculations, String]) => 
                ObjectProperty(features.value.datestart)
                }
            }
        val tickerCollum = new TableColumn[TradeDoneCalculations, String] {
            text = "Ticker"
            cellValueFactory = { (features: CellDataFeatures[TradeDoneCalculations, String]) => 
                ObjectProperty(features.value.ticker)
                }
            }
        val volumeCollum = new TableColumn[TradeDoneCalculations, Double] {
            text = "Volume"
            cellValueFactory = { (features: CellDataFeatures[TradeDoneCalculations, Double]) => 
                ObjectProperty(new TradeWithVolume(features.value, controller.balance).volume)
            }
        }
        val tradebuysell = new TableColumn[TradeDoneCalculations, String] {
            text = "Type"
            cellValueFactory = { (features: CellDataFeatures[TradeDoneCalculations, String]) => 
                ObjectProperty(new TradeisBuy(features.value).isTradeBuy)
            }
        }


        val currentProfit = new TableColumn[TradeDoneCalculations, Double] {
            text = "currentProfit"
            cellValueFactory = { (features: CellDataFeatures[TradeDoneCalculations, Double]) => 
                ObjectProperty(features.value.currentprofit)
            }
            }
         
        def updatecurrentProfit(trade: TradeDoneCalculations): Unit = {
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
            
            //calculateCurrentProfit(trade, TradeWithVolume(trade, controller.balance).volume, data.currentPrice  , data.endDate.format(formatter))
        }

        val tradesBuffer = ObservableBuffer[TradeDoneCalculations]()
        tradesBuffer ++= TradeData.donetrades



        val table = new TableView[TradeDoneCalculations](tradesBuffer) {
        columns ++= List(dateCollum, tradebuysell, volumeCollum, riskCollum, tickerCollum, entryCollum, stoplossCollum, takeprofitCollum, currentProfit)
        }
        
   
        
        timeframeComboBox.value = "1h"
        timeframeComboBox.value.onChange { (_, _, newTimeframe) =>
            
            val newdata = getCandleSticksdadabase(newTimeframe.toString(), tickerComboBox.text.value, LocalDateTime.parse(dateInput.text.value, formatter), sizecandles)
            clearAndAddData(chart, newdata)
            setLowerBoundForCandlesnumber(chart)
            
            }

        tickerComboBox.onKeyPressed = (keyEvent: KeyEvent) => {
            if (keyEvent.code == KeyCode.Enter) {
                val newdata = getCandleSticksdadabase(timeframeComboBox.value.value, tickerComboBox.text.value, LocalDateTime.parse(dateInput.text.value, formatter), sizecandles)
                clearAndAddData(chart, newdata)
                updateCandleStickChartAxis(chart, newdata)
            }
        }
        val endButton = new Button("Finish Backtesting")
        val button1 = new Button("<<")
        
        val button2 = new Button(">>")
        val profitLabel = new Label(s"Profit: $summprofit       ")
        val speedSlider = new Slider {
            min = 10
            max = 1000
            value = 200
        }

        class TimelineToggleButton extends ToggleButton {
            var timeline: Timeline = _
            
            def startTimeline(): Unit = {
                timeline = new Timeline {
                    cycleCount = Timeline.Indefinite
                    keyFrames = KeyFrame(Duration(speedSlider.value.value), onFinished = _ => showdataandupdatedatetext()

                    )
                }
                timeline.play()
            }
        }


        def showdataandupdatedatetext(): Unit = {
            val currentDateTime = LocalDateTime.parse(dateInput.text.value, formatter)
            val newDateTime = timeframeComboBox.value.value match {
                    case "1m" => currentDateTime.plusMinutes(1)
                    case "5m" => currentDateTime.plusMinutes(5)
                    case "15m" => currentDateTime.plusMinutes(15)
                    case "1h" => currentDateTime.plusHours(1)
                    case "4h" => currentDateTime.plusHours(4)
                    case "1d" => currentDateTime.plusDays(1)
                    case "1w" => currentDateTime.plusWeeks(1)
                    case _ => currentDateTime
                }
            showData(chart, LocalDateTime.parse(dateInput.text.value, DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")), timeframeComboBox.value.value, tickerComboBox.text.value )
            dateInput.text = newDateTime.format(formatter)
        }




        val runDataButton = new TimelineToggleButton {
            text = "Run Data"

            selected.onChange { (_, _, isSelected) =>
                if (isSelected) {
                    startTimeline()
                    text = "Stop"
                } else {
                    if (timeline != null) {
                        timeline.stop()
                    }
                    text = "Run Data"
                }
            }
        }

        speedSlider.value.onChange { (_, _, newValue) =>
            if (runDataButton.selected.value) {
                if (runDataButton.timeline != null) {
                    runDataButton.timeline.stop()
                }
                runDataButton.startTimeline()
            }
        }

        button2.onAction = () => {
            val currentDateTime = LocalDateTime.parse(dateInput.text.value, formatter)

            val newDateTime = timeframeComboBox.value.value match {
                case "1m" => currentDateTime.plusMinutes(1)
                case "5m" => currentDateTime.plusMinutes(5)
                case "15m" => currentDateTime.plusMinutes(15)
                case "1h" => currentDateTime.plusHours(1)
                case "4h" => currentDateTime.plusHours(4)
                case "1d" => currentDateTime.plusDays(1)
                case "1w" => currentDateTime.plusWeeks(1)
                case _ => currentDateTime
            }

            
            val browseinput = s"${tickerComboBox.text.value} ${dateInput.text.value}"
            println(browseinput)
            controller.computeInput(browseinput)
            controller.printDescriptor()
        
            showData(chart, LocalDateTime.parse(dateInput.text.value, DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")),timeframeComboBox.value.value, tickerComboBox.text.value )
            dateInput.text = newDateTime.format(formatter)
            tradesBuffer.clear()
            tradesBuffer ++= TradeData.donetrades.map(trade => {
                updatecurrentProfit(trade)
                trade
            })
            // Update summprofit
            summprofit = tradesBuffer.map(_.currentprofit).sum
            profitLabel.text = s"Profit: $summprofit"

            table.refresh()
        }

        button1.onAction = () => {
            val currentDateTime = LocalDateTime.parse(dateInput.text.value, formatter)

            val newDateTime = timeframeComboBox.value.value match {
                case "1m" => currentDateTime.minusMinutes(1)
                case "5m" => currentDateTime.minusMinutes(5)
                case "15m" => currentDateTime.minusMinutes(15)
                case "1h" => currentDateTime.minusHours(1)
                case "4h" => currentDateTime.minusHours(4)
                case "1d" => currentDateTime.minusDays(1)
                case "1w" => currentDateTime.minusWeeks(1)
                case _ => currentDateTime
            }

            dateInput.text = newDateTime.format(formatter)

            deleteFirstCandle(chart)
            tradesBuffer.clear()
            tradesBuffer ++= TradeData.donetrades.map(trade => {
                updatecurrentProfit(trade)
                trade
            })
            // Update summprofit
            summprofit = tradesBuffer.map(_.currentprofit).sum
            profitLabel.text = s"Profit: $summprofit"

            table.refresh()
        }
        
        val topButtons = new HBox(endButton, tickerComboBox,timeframeComboBox, button1,runDataButton, button2, speedSlider)

       


        


 

        
        VBox.setVgrow(chartPane, Priority.Always)

        

        val applyDateButton = new Button("Apply Date")
        val gotoDateButton = new Button("Go to Date")
        val applydateHbox = new HBox(applyDateButton, gotoDateButton)
        gotoDateButton.onAction = (event: ActionEvent) => {
            nextClickAction = "gotodate"
            }
        
        
        applyDateButton.setOnAction(_ => {
            val browseinput = s"${tickerComboBox.text.value} ${dateInput.text.value}"
            
            val newdata = getCandleSticksdadabase(timeframeComboBox.value.value, tickerComboBox.text.value, LocalDateTime.parse(dateInput.text.value, formatter), sizecandles)
            clearAndAddData(chart, newdata)
            updateCandleStickChartAxis(chart ,newdata)
            println(browseinput)
            controller.computeInput(browseinput)
            controller.printDescriptor()
            tradesBuffer.clear()
               tradesBuffer ++= TradeData.donetrades.map(trade => {
                updatecurrentProfit(trade)
                trade
            })
            // Update summprofit
            summprofit = tradesBuffer.map(_.currentprofit).sum
            profitLabel.text = s"Profit: $summprofit"

            table.refresh()
        })
        

        val risk = new TextField()
        val enterTradeButton = new Button("Enter Trade")
        enterTradeButton.setOnAction(_ => {
            val investinput = s"${entry.text.value} ${stopLoss.text.value} ${takeProfit.text.value} ${risk.text.value}"
            controller.computeInput(investinput)
            controller.printDescriptor()
 

            tradesBuffer.clear()
            tradesBuffer ++= TradeData.donetrades
           
            table.refresh()

        })

        val tradeBoxButton = new Button("Trade Box")
        tradeBoxButton.onAction = () => {
            // Call createLongPositionBox with appropriate parameters
            
        }

        val startHorizontalLineButton = new Button("0---")
        val horizontalLineButton = new Button("----")
        val sentimentcircleButton = new ToggleButton(".oO0")
        sentimentcircleButton.onAction = (ae: ActionEvent) => {
            if (sentimentcircleButton.selected.value) {
                chartPane.addcircle(tickerComboBox.text.value)
            } else {
                chartPane.deleteAllCircles()
            }
}
        startHorizontalLineButton.onAction = (ae: ActionEvent) => {
            nextClickAction = "starthorizontal"
        }

        horizontalLineButton.onAction = (ae: ActionEvent) => {
            nextClickAction = "horizontal"
        }
        val leftButtons = new VBox(
            tradeBoxButton,
            startHorizontalLineButton,
            horizontalLineButton,
            sentimentcircleButton
        )
         
        chartWithCrosshair.onMouseClicked = (me: MouseEvent) => {
            if (nextClickAction == "starthorizontal") {
                
                chartPane.plotHorizontalStartLine(me)
                nextClickAction = "" // Reset the action
            } else if (nextClickAction == "horizontal") {
                chartPane.plotHorizontalLine(me)
                nextClickAction = "" // Reset the action
            } else if(nextClickAction == "gotodate") {
                val epochSeconds = chartPane.calculateXDate(me)
                val instant = Instant.ofEpochSecond(epochSeconds.toLong)
                val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
                val dateString = localDateTime.format(formatter)
                dateInput.text = dateString
                val newdata = getCandleSticksdadabase(timeframeComboBox.value.value, tickerComboBox.text.value, LocalDateTime.parse(dateInput.text.value, formatter), sizecandles)
                addDataAndHideAfterDate(chart, newdata, localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond)
                //updateCandleStickChartAxis(chart, newdata)
                val browseinput = s"${tickerComboBox.text.value} ${dateInput.text.value}"
                println(browseinput)
                controller.computeInput(browseinput)
                controller.printDescriptor()
                nextClickAction = ""
            }
        }
        val rightButtons = new VBox(
            new Button("Button 1"),
            new Button("Button 2"),
            new Button("Button 3")
        )

        val inputBox = new VBox(
            dateInput,
            applydateHbox,
            new Label("Entry Price: "),
            entry,
            new Label("Stop Loss: "),
            stopLoss,
            new Label("Take Profit:"),
            takeProfit,
            new Label("Risk in %:"),
            risk,
            enterTradeButton
        )
        

        val balanceLabel = new Label(s"Balance: ${controller.balance}")
        
        val spacer = new Region()
        HBox.setHgrow(spacer, Priority.Always)
        val balanceProfitBox = new HBox(balanceLabel, spacer, profitLabel)
        
        val splitPane2 = new SplitPane()
        splitPane2.orientation = Orientation.Horizontal
        splitPane2.items.addAll(chartWithCrosshair, inputBox)
        SplitPane.setResizableWithParent(inputBox, false)

        
        val tablewithlabel = new VBox(balanceProfitBox, table)
        val splitPane1 = new SplitPane()
        splitPane1.orientation = Orientation.Vertical
        splitPane1.items.addAll(splitPane2, tablewithlabel)

        val horizontalBox = new HBox(leftButtons, splitPane1, rightButtons)
        HBox.setHgrow(splitPane1, Priority.Always)


        val mainPane = new VBox(topButtons, horizontalBox)
        mainPane.fillWidth = true



        val stage = new PrimaryStage {
            title = "Backtesting Stage"
            scene = new Scene(mainPane)
        }

        endButton.setOnAction(_ => {
            stage.hide()
            BacktestEvaluation.createStage().show()
            controller.computeInput("Q")
            controller.printDescriptor()
        })




        stage
    }
}
