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

import java.time.format.DateTimeFormatter

object BacktestStage extends JFXApp3 {
    val controller = new Controller()
    override def start(): Unit = 
        val stage = new BacktestStage(controller).createStage()
        stage.show()

}
class BacktestStage(controller: Controller){
   val data = AllTickerArrays("EURUSD", "1m")
   data.setTicker("EURUSD")
    data.setTimeFrame("1m")
    val chart = createChart(data.getCandleSticks)
    val chartPane = new DraggableCandleStickChart(chart)
    var dragStartX: Double = 0
    var dragStartY: Double = 0
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

    def calculateDate(x: Double): String = {
    // Your logic to calculate the date based on the x-coordinate goes here.
    // This is just a placeholder implementation.
    "Some Date"
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
        val date = calculateDate(me.getX)
        val price = chartPane.calculateYPrice(me)

        // Update the labels
        dateLabelcross.text = date
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
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            val formattedDate = dateInput.getValue.format(formatter)
            calculateCurrentProfit(trade, TradeWithVolume(trade, controller.balance).volume, 1.1 , s"$formattedDate,${String.format("%02d",hourSpinner.getValue)}:${String.format("%02d",minuteSpinner.getValue)}")
        }

        val tradesBuffer = ObservableBuffer[TradeDoneCalculations]()
        tradesBuffer ++= GetMarketData.donetrades



        val table = new TableView[TradeDoneCalculations](tradesBuffer) {
        columns ++= List(dateCollum, tradebuysell, volumeCollum, riskCollum, tickerCollum, entryCollum, stoplossCollum, takeprofitCollum, currentProfit)
        }

        val timeframeOptions = ObservableBuffer("1m", "5m", "15m", "1h", "4h", "1day", "1week")
        val timeframeComboBox = new ComboBox[String](timeframeOptions)
        timeframeComboBox.value = "1m"
        timeframeComboBox.value.onChange { (_, _, newTimeframe) =>
            data.setTimeFrame(newTimeframe)
            
            clearAndAddData(chart, data.getCandleSticks)
            }
        val tickerDropdown = new TickerSelection()
        val tickerComboBox = tickerDropdown.createTickerDropdown()
        val endButton = new Button("Finish Backtesting")
        val Button1 = new Button("Button1")
        val Button2 = new Button("Button2")
        val topButtons = new HBox(endButton, tickerComboBox,timeframeComboBox, Button1, Button2)

       

        val hourLabel = new Label("Hour: ")
        val minuteLabel = new Label("Minute: ")

        val hourBox = new HBox(hourLabel, hourSpinner)
        val minuteBox = new HBox(minuteLabel, minuteSpinner)
        


 

        
        VBox.setVgrow(chartPane, Priority.Always)

        

        val applyDateButton = new Button("Apply Date")
        var summprofit = 0.0
        val profitLabel = new Label(s"Profit: $summprofit       ")
        applyDateButton.setOnAction(_ => {
            val selectedDate = dateInput.getValue // Get the selected date from the date picker
            val selectedHour = hourSpinner.getValue // Get the selected hour from the spinner
            val selectedMinute = minuteSpinner.getValue // Get the selected minute from the spinner
            val dateTime = selectedDate.atTime(selectedHour, selectedMinute) // Combine the date and time into a LocalDateTime object


            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            val formattedDate = selectedDate.format(formatter)
            val browseinput = s"${tickerComboBox.value.value} ${formattedDate},${String.format("%02d",selectedHour)}:${String.format("%02d",selectedMinute)}"
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
        tickerComboBox.value.onChange { (_, _, newTicker) =>

            }

        timeframeComboBox.onAction = () => {
            val selectedTimeframe = timeframeComboBox.value.value
            // Update the line chart with the selected timeframe

            }

        stage
    }
}
