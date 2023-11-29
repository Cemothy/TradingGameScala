package de.htwg.se.TradingGame.view.GUI

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Alert, TextField}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.Label

object BalanceStage extends JFXApp3 {
  var balance: Double = 0.0
  override def start(): Unit = {
    stage = createStage()
  }
  def createStage(): PrimaryStage = {
    val startButton = new Button("Start Backtesting")
    val balanceLabel = new Label("What Balance do you want to start with?")
    val balanceInput = new TextField()

    val stage = new PrimaryStage {
      title = "Balance Stage"
      scene = new Scene(new VBox(balanceLabel, balanceInput, startButton))
    }

    startButton.setOnAction(_ => {
      balance = balanceInput.text.value.toDouble
      stage.hide()
      BacktestStage.createStage().show()
    })

    stage
  }
}
