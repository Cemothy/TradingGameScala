package de.htwg.se.TradingGame.view.GUI

import de.htwg.se.TradingGame.controller.IController
import de.htwg.se.TradingGame.util.Observer
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.Button
import scalafx.scene.control.Label
import scalafx.scene.control.TextField
import scalafx.scene.layout.HBox
import scalafx.scene.layout.VBox

class BalanceStage(controller: IController) {

  def createStage(): PrimaryStage = {
    val startButton = new Button("Start Backtesting")
    val balanceLabel = new Label("What Balance do you want to start with?")
    val balanceInput = new TextField()

    val stage = new PrimaryStage {
      title = "Balance Stage"
      scene = new Scene(new VBox(balanceLabel, balanceInput, startButton))
    }

    startButton.setOnAction(_ => {
      try {
        val balance = balanceInput.text.value.toDouble
        controller.balance = balance 
        controller.computeInput(balanceInput.text.value)
        controller.printDescriptor()
        stage.hide()
        new BacktestStage(controller).createStage().show()
      } catch {
        case e: NumberFormatException =>
          new Alert(AlertType.Error, "Please enter a valid number for balance").showAndWait()
      }
    })

    stage
  }
}

