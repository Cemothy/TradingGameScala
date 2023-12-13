package de.htwg.se.TradingGame.view.GUI

import de.htwg.se.TradingGame.controller.Controller
import de.htwg.se.TradingGame.model.GetMarketData
import de.htwg.se.TradingGame.model.GetMarketData._
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Trade
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeActive
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeComponent
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeDoneCalculations
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeWithVolume
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.TradeisBuy
import de.htwg.se.TradingGame.view.GUI.AdvCandleStickChartSample.clearAndAddData
import de.htwg.se.TradingGame.view.GUI.AdvCandleStickChartSample.createChart
import de.htwg.se.TradingGame.view.GUI.BalanceStage
import de.htwg.se.TradingGame.view.GUI.GetAPIData._
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.shape.Line
import scalafx.Includes._
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
import scalafx.scene.control.Spinner
import scalafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory
import scalafx.scene.control.SplitPane
import scalafx.scene.control.TableColumn
import scalafx.scene.control.TableColumn.CellDataFeatures
import scalafx.scene.control.TableView
import scalafx.scene.control.TextField
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

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
object BacktestStage extends JFXApp3 {
    val controller = new Controller()
    override def start(): Unit = 
        val stage = new BacktestStage(controller).createStage()
        stage.show()

}
class BacktestStage(controller: Controller){
    var data = getCandleSticks("1min", "EURUSD", LocalDateTime.now())

    val chart = createChart(data)
    val chartPane = new DraggableCandleStickChart(chart)
    var dragStartX: Double = 0
    var dragStartY: Double = 0
    var summprofit = 0.0
    var nextClickAction: String = ""
    val crosshairPane = new Pane()
    val crosshair = new Crosshair(crosshairPane) // Pass the crosshairPane instead of chartPane
    crosshair.createCrosshair()
    val dateLabelcross = new Label {
    textFill = Color.Black
    style = "-fx-background-color: white; -fx-padding: 5;"

    }

    val priceLabelcross = new Label {
        textFill = Color.Black
        style = "-fx-background-color: white; -fx-padding: 5;"
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
        crosshair.updateCrosshair(me)
        entry.text = chartPane.entryprice
        takeProfit.text = chartPane.takeProfitPrice
        stopLoss.text = chartPane.stopLossPrice
        val epochSeconds = chartPane.calculateXDate(me)
        val instant = Instant.ofEpochSecond(epochSeconds.toLong)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
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
    }

    chartWithCrosshair.onMouseDragged = (me: MouseEvent) => {
        chartPane.updateOnDrag(me)
        crosshair.updateCrosshair(me)
    }

    chartWithCrosshair.onMouseReleased = (me: MouseEvent) => {
        chartPane.updateOnMouseRelease()
    }

    chartWithCrosshair.onScroll = (me: ScrollEvent) => {
        chartPane.updateOnScroll(me)
    }

     val dateInput = new DatePicker()
    val dateLabel = new Label("Date: ")
    val dateBox = new HBox(dateLabel, dateInput)
    val hourSpinner = new Spinner[Int](0, 23, 0)
    val minuteSpinner = new Spinner[Int](0, 59, 0)
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
        tradesBuffer ++= GetMarketData.donetrades



        val table = new TableView[TradeDoneCalculations](tradesBuffer) {
        columns ++= List(dateCollum, tradebuysell, volumeCollum, riskCollum, tickerCollum, entryCollum, stoplossCollum, takeprofitCollum, currentProfit)
        }
        
        val tickerComboBox = new TextField {
        promptText = "Enter Ticker"
        text = "EURUSD"
        }
        val timeframeOptions = ObservableBuffer("1min", "5min", "15min", "60min", "4h", "1d", "1w")
        val timeframeComboBox = new ComboBox[String](timeframeOptions)
        timeframeComboBox.value = "1min"
        timeframeComboBox.value.onChange { (_, _, newTimeframe) =>
            
            val newdata = getCandleSticks(newTimeframe.toString(), tickerComboBox.text.value, LocalDateTime.now())
            clearAndAddData(chart, newdata)
            
            }

        tickerComboBox.onKeyPressed = (keyEvent: KeyEvent) => {
            if (keyEvent.code == KeyCode.Enter) {
                val newdata = getCandleSticks(timeframeComboBox.value.value, tickerComboBox.text.value, LocalDateTime.now())
                clearAndAddData(chart, newdata)
            }
        }
        val endButton = new Button("Finish Backtesting")
        val button1 = new Button("<<")
        val button2 = new Button(">>")
        val profitLabel = new Label(s"Profit: $summprofit       ")
        button2.onAction = () => {
            //data.incrementDataByNCandles(1)

            clearAndAddData(chart, data)
            tradesBuffer.clear()
            tradesBuffer ++= GetMarketData.donetrades.map(trade => {
                updatecurrentProfit(trade)
                trade
            })
            // Update summprofit
            summprofit = tradesBuffer.map(_.currentprofit).sum
            profitLabel.text = s"Profit: $summprofit"

            table.refresh()
        }

        button1.onAction = () => {
            //data.decrementDataByNCandles(1)

            clearAndAddData(chart, data)
            tradesBuffer.clear()
            tradesBuffer ++= GetMarketData.donetrades.map(trade => {
                updatecurrentProfit(trade)
                trade
            })
            // Update summprofit
            summprofit = tradesBuffer.map(_.currentprofit).sum
            profitLabel.text = s"Profit: $summprofit"

            table.refresh()
        }
        
        val topButtons = new HBox(endButton, tickerComboBox,timeframeComboBox, button1, button2)

       

        val hourLabel = new Label("Hour: ")
        val minuteLabel = new Label("Minute: ")

        val hourBox = new HBox(hourLabel, hourSpinner)
        val minuteBox = new HBox(minuteLabel, minuteSpinner)
        


 

        
        VBox.setVgrow(chartPane, Priority.Always)

        

        val applyDateButton = new Button("Apply Date")
        
        
        
        applyDateButton.setOnAction(_ => {
            val selectedDate = dateInput.getValue // Get the selected date from the date picker
            val selectedHour = hourSpinner.getValue // Get the selected hour from the spinner
            val selectedMinute = minuteSpinner.getValue // Get the selected minute from the spinner
            val dateTime = selectedDate.atTime(selectedHour, selectedMinute) // Combine the date and time into a LocalDateTime object


            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            val formattedDate = selectedDate.format(formatter)
            val browseinput = s"${tickerComboBox.text.toString} ${formattedDate},${String.format("%02d",selectedHour)}:${String.format("%02d",selectedMinute)}"
            val dateinput = s"${formattedDate},${String.format("%02d",selectedHour)}:${String.format("%02d",selectedMinute)}"
            //data.setDate(dateinput)
            clearAndAddData(chart, data)
            chartPane.setupperboundxtolastdata(data)
            controller.computeInput(browseinput)
            controller.printDesctriptor()
            tradesBuffer.clear()
               tradesBuffer ++= GetMarketData.donetrades.map(trade => {
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
            controller.printDesctriptor()

            tradesBuffer.clear()
            tradesBuffer ++= GetMarketData.donetrades
           
            table.refresh()

        })

        val tradeBoxButton = new Button("Trade Box")
        tradeBoxButton.onAction = () => {
            // Call createLongPositionBox with appropriate parameters
            
        }

        val startHorizontalLineButton = new Button("0---")
        val horizontalLineButton = new Button("----")
        startHorizontalLineButton.onAction = (ae: ActionEvent) => {
            nextClickAction = "starthorizontal"
        }

        horizontalLineButton.onAction = (ae: ActionEvent) => {
            nextClickAction = "horizontal"
        }
        val leftButtons = new VBox(
            tradeBoxButton,
            startHorizontalLineButton,
            horizontalLineButton
        )
         
        chartWithCrosshair.onMouseClicked = (me: MouseEvent) => {
            if (nextClickAction == "starthorizontal") {
                chartPane.plotHorizontalStartLine(me)
                nextClickAction = "" // Reset the action
            } else if (nextClickAction == "horizontal") {
                chartPane.plotHorizontalLine(me)
                nextClickAction = "" // Reset the action
            }
        }
        val rightButtons = new VBox(
            new Button("Button 1"),
            new Button("Button 2"),
            new Button("Button 3")
        )

        val inputBox = new VBox(
            dateBox,
            hourBox,
            minuteBox,
            applyDateButton,
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
            controller.printDesctriptor()
        })




        stage
    }
}
