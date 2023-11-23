package de.htwg.se.TradingGame.view.GUI

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Alert, TextField, TableView}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.Label
import scalafx.scene.input.KeyCode.B
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import scalafx.scene.layout.Priority
import scalafx.scene.control.SplitPane
import scalafx.geometry.Orientation


object BacktestStage extends JFXApp3 {
    override def start(): Unit = {
        stage = createStage()
    }

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

        val xAxis = new NumberAxis()
        val yAxis = new NumberAxis()
        val chart = new LineChart(xAxis, yAxis)
        chart.maxWidth = Double.MaxValue
        chart.maxHeight = Double.MaxValue
        val chartpane = new VBox(chart)
        VBox.setVgrow(chart, Priority.Always)


        val inputBox = new VBox(
            new Label("Input 1"),
            new TextField(),
            new Label("Input 2"),
            new TextField(),
            new Label("Input 3"),
            new TextField()
        )

        val splitPane2 = new SplitPane()
        splitPane2.orientation = Orientation.Horizontal
        splitPane2.items.addAll(chartpane, inputBox)
        SplitPane.setResizableWithParent(inputBox, false)

        val table = new TableView[String]()
        val splitPane1 = new SplitPane()
        splitPane1.orientation = Orientation.Vertical
        splitPane1.items.addAll(splitPane2, table)



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
