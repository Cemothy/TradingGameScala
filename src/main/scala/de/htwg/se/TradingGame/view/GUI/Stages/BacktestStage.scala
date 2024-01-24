package de.htwg.se.TradingGame.view.GUI.Stages

import de.htwg.se.TradingGame.Main.controller
import de.htwg.se.TradingGame.controller.IController
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
import de.htwg.se.TradingGame.view.GUI.Stages.BacktestStageFolder.DraggableCandleStickChart
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.TradeDoneCalculations
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.TradeWithVolume
import de.htwg.se.TradingGame.model.TradeDecoratorPattern.Decorator.ConcreteDecorators.TradeisBuy
import de.htwg.se.TradingGame.view.GUI.Stages.BacktestStageFolder._



object BacktestStage extends JFXApp3 {
    override def start(): Unit = BacktestStage(controller).createStage().show()
}
class BacktestStage(controller: IController){
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
    val chartPane = new DraggableCandleStickChart(controller)
    var nextClickAction: String = ""
    var summprofit = 0.0
    val crosshairPane = new Pane()
    val crosshair = new Crosshair(crosshairPane)
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
        if (keyEvent.code == KeyCode.Enter) 
            val priceText = entry.text.value
            chartPane.entryline(priceText)
    }
    val takeProfit = new TextField()
    takeProfit.onKeyPressed = (keyEvent: KeyEvent) => {
        if (keyEvent.code == KeyCode.Enter) 
            val priceText = takeProfit.text.value
            chartPane.takeProfitLine(priceText)
    }
    val stopLoss = new TextField()
    stopLoss.onKeyPressed = (keyEvent: KeyEvent) => {
        if (keyEvent.code == KeyCode.Enter) 
            val priceText = stopLoss.text.value
            chartPane.stopLossLine(priceText)
    }
    chartWithCrosshair.onMouseMoved = (me: MouseEvent) => {
        crosshair.updateCrosshair(me)
        val epochSeconds = chartPane.calculateXDate(me)
        val instant = Instant.ofEpochSecond(epochSeconds.toLong)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val dateString = localDateTime.format(formatter)
        val price = chartPane.calculateYPrice(me)
        dateLabelcross.text = dateString
        priceLabelcross.text = price
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
        chartPane.updateAllLines()
    }
    chartWithCrosshair.onScroll = (me: ScrollEvent) => {
        chartPane.updateOnScroll(me)
        chartPane.updateAllLines()
    }
    val dateInput = new TextField {
        promptText = "Enter Date"
        text = LocalDateTime.now().format(formatter)
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
                ObjectProperty(new TradeWithVolume(features.value, controller.gameStateManager.currentState.balance).volume)
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
        timeframeComboBox.value = "1h"
        timeframeComboBox.value.onChange { (_, _, newTimeframe) =>
        //TODO: ändere timeframe vom chart und state und setze den chart richtig
        }
        val tradesBuffer = ObservableBuffer[TradeDoneCalculations]()
        tradesBuffer ++= controller.gameStateManager.currentState.doneTrades

        val table = new TableView[TradeDoneCalculations](tradesBuffer) {
            columns ++= List(dateCollum, tradebuysell, volumeCollum, riskCollum, tickerCollum, entryCollum, stoplossCollum, takeprofitCollum, currentProfit)
        }
        tickerComboBox.onKeyPressed = (keyEvent: KeyEvent) => {
            if (keyEvent.code == KeyCode.Enter) {
                //TODO: ändere ticker vom chart state und setze das fenster richtig
            }
        }
        val endButton = new Button("Finish Backtesting")
        val backtrackButton = new Button("<<")
        val addcandlebuton = new Button(">>")
        val profitLabel = new Label(s"Profit: $summprofit       ")
        val speedSlider = new Slider {
            min = 30
            max = 1000
            value = 200
        }
        class TimelineToggleButton extends ToggleButton {
            var timeline: Timeline = _
            def startTimeline(): Unit = 
                timeline = new Timeline {
                    cycleCount = Timeline.Indefinite
                    keyFrames = KeyFrame(Duration(speedSlider.value.value), onFinished = _ => showdataandupdatedatetext()
                    )
                }
                timeline.play()
        }
        def showdataandupdatedatetext(): Unit = {
         //TODO: set the backtest date to the next value and update the text to the backtestdate
        }
        val runDataButton = new TimelineToggleButton {
            text = "Run Data"
            selected.onChange { (_, _, isSelected) =>
                if (isSelected) 
                    startTimeline()
                    text = "Stop"
                else
                    if (timeline != null) 
                        timeline.stop()
                    text = "Run Data"
            }
        }
        speedSlider.value.onChange { (_, _, newValue) =>
            if (runDataButton.selected.value) 
                if (runDataButton.timeline != null) 
                    runDataButton.timeline.stop()
                runDataButton.startTimeline()
        }
        addcandlebuton.onAction = () => {
            //TODO: add one timeeinheit to backtestdate and update the table current profit 
        }
        backtrackButton.onAction = () => {
          //TODO: update current profit and set backtest date one back
        }
        val topButtons = new HBox(endButton, tickerComboBox,timeframeComboBox, backtrackButton,runDataButton, addcandlebuton, speedSlider)
        VBox.setVgrow(chartPane, Priority.Always)
        val applyDateButton = new Button("Apply Date")
        val gotoDateButton = new Button("Go to Date")
        val applydateHbox = new HBox(applyDateButton, gotoDateButton)
        gotoDateButton.onAction = (event: ActionEvent) => {
            nextClickAction = "gotodate"
        }
        applyDateButton.setOnAction(_ => {
            //TODO: update current profit of table. set backtest date to applied date and set the window so it fits the data
        })
        val risk = new TextField()
        val enterTradeButton = new Button("Enter Trade")
        enterTradeButton.setOnAction(_ => {
            //TODO: send inputs to interpretter. update trade table. 
        })
        val tradeBoxButton = new Button("Trade Box")
        tradeBoxButton.onAction = () => {
            //TODO: implement tradebox
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
            if (nextClickAction == "starthorizontal") 
                chartPane.plotHorizontalStartLine(me)
                nextClickAction = "" 
            else if (nextClickAction == "horizontal") 
                chartPane.plotHorizontalLine(me)
                nextClickAction = "" 
            else if(nextClickAction == "gotodate") 
                //TODO: set backtestdate to the clicked date via interpretter
                nextClickAction = ""
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
        val balanceLabel = new Label(s"Balance: ${controller.gameStateManager.currentState.balance}")
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
        scene = new Scene(mainPane) {
            stylesheets.add("de/htwg/se/TradingGame/view/GUI/CSS/darkmode.css")
        }
        }
        endButton.setOnAction(_ => {
            stage.hide()
            val backtestEvaluationStage = new BacktestEvaluationStage(controller)
            backtestEvaluationStage.createStage().show()
            controller.computeInput("Q")
        })
        stage
    }
}
