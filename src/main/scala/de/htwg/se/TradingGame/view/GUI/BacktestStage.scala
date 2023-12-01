package de.htwg.se.TradingGame.view.GUI

import de.htwg.se.TradingGame.view.GUI.BalanceStage._
import de.htwg.se.TradingGame.view.GUI.LinechartPane
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.geometry.Orientation
import scalafx.scene.Scene
import scalafx.scene.chart.LineChart
import scalafx.scene.chart.NumberAxis
import scalafx.scene.chart.XYChart
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.Button
import scalafx.scene.control.Label
import scalafx.scene.control.SplitPane
import scalafx.scene.control.TableView
import scalafx.scene.control.TextField
import scalafx.scene.input.KeyCode.B
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.HBox
import scalafx.scene.layout.Pane
import scalafx.scene.layout.Priority
import scalafx.scene.layout.Region
import scalafx.scene.layout.StackPane
import scalafx.scene.layout.VBox
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.ComboBox
import scalafx.scene.control.DatePicker
import scalafx.scene.control.Spinner
import scalafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory

object BacktestStage extends JFXApp3 {
    override def start(): Unit = {
        stage = createStage()
    }
    val crosshairPane = new Pane()
    def createStage(): PrimaryStage = {
        val timeframeOptions = ObservableBuffer("1m", "5m", "15m", "1h", "4h", "1day", "1week")
        val timeframeComboBox = new ComboBox[String](timeframeOptions)
        timeframeComboBox.value = "1m"
        val tickerDropdown = new TickerSelection()
        val tickerComboBox = tickerDropdown.createTickerDropdown()
        val endButton = new Button("Finish Backtesting")
        val Button1 = new Button("Button1")
        val Button2 = new Button("Button2")
        val topButtons = new HBox(endButton, tickerComboBox,timeframeComboBox, Button1, Button2)

        val dateInput = new DatePicker()
        val dateLabel = new Label("Date: ")
        val dateBox = new HBox(dateLabel, dateInput)
        val hourSpinner = new Spinner[Int](0, 23, 0)
        val minuteSpinner = new Spinner[Int](0, 59, 0)

        val hourLabel = new Label("Hour: ")
        val minuteLabel = new Label("Minute: ")

        val hourBox = new HBox(hourLabel, hourSpinner)
        val minuteBox = new HBox(minuteLabel, minuteSpinner)

        val leftButtons = new VBox(
            new Button("Button 1"),
            new Button("Button 2"),
            new Button("Button 3")
        )

        val rightButtons = new VBox(
            new Button("Button 1"),
            new Button("Button 2"),
            new Button("Button 3")
        )


 
        val chartpane = new LinechartPane()
        chartpane.initializeLineChart(tickerComboBox.value.value)
        VBox.setVgrow(chartpane, Priority.Always)


        val chartWithCrosshair = new ChartDragHandler(chartpane, crosshairPane)
        val applyDateButton = new Button("Apply Date")
        applyDateButton.setOnAction(_ => {
        val selectedDate = dateInput.getValue // Get the selected date from the date picker
        val selectedHour = hourSpinner.getValue // Get the selected hour from the spinner
        val selectedMinute = minuteSpinner.getValue // Get the selected minute from the spinner
        val dateTime = selectedDate.atTime(selectedHour, selectedMinute) // Combine the date and time into a LocalDateTime object
        chartpane.updateDate(dateTime)
        })
        val inputBox = new VBox(
            dateBox,
            hourBox,
            minuteBox,
            applyDateButton,
            new Label("Input 1"),
            new TextField(),
            new Label("Input 2"),
            new TextField(),
            new Label("Input 3"),
            new TextField()
        )

        val balanceLabel = new Label(s"Balance: ${balance}")
        val profitLabel = new Label("Profit: $0       ")
        val spacer = new Region()
        HBox.setHgrow(spacer, Priority.Always)
        val balanceProfitBox = new HBox(balanceLabel, spacer, profitLabel)
        
        val splitPane2 = new SplitPane()
        splitPane2.orientation = Orientation.Horizontal
        splitPane2.items.addAll(chartWithCrosshair, inputBox)
        SplitPane.setResizableWithParent(inputBox, false)

        val table = new TableView[String]()
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
        })
        tickerComboBox.value.onChange { (_, _, newTicker) =>
            chartpane.initializeLineChart(newTicker)
            }

        timeframeComboBox.onAction = () => {
            val selectedTimeframe = timeframeComboBox.value.value
            // Update the line chart with the selected timeframe
            chartpane.updateTimeframe(selectedTimeframe)
            }

        stage
    }
}
