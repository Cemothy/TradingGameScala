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

object BacktestStage extends JFXApp3 {
    override def start(): Unit = {
        stage = createStage()
    }
    val crosshairPane = new Pane()
    def createStage(): PrimaryStage = {
        val endButton = new Button("Finish Backtesting")
        val Button1 = new Button("Button1")
        val Button2 = new Button("Button2")
        val topButtons = new HBox(endButton, Button1, Button2)

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
        chartpane.initializeLineChart()
        VBox.setVgrow(chartpane, Priority.Always)

        
       

       

        val chartWithCrosshair = new ChartDragHandler(chartpane, crosshairPane)






        val inputBox = new VBox(
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

        stage
    }
}
