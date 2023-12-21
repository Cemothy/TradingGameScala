package de.htwg.se.TradingGame.view.GUI

import de.htwg.se.TradingGame.controller.Controller
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

class BalanceStage(controller: Controller){


  

  var balance: Double = 0.0

  def createStage(): PrimaryStage = {
  val startButton = new Button("Start Backtesting")
  val balanceLabel = new Label("What Balance do you want to start with?")
  val balanceInput = new TextField()

  val stage = new PrimaryStage
    stage.title = "Balance Stage"
    stage.scene = new Scene(new VBox(balanceLabel, balanceInput, startButton))
  

  startButton.setOnAction(_ => {
    controller.balance = balanceInput.text.value.toDouble
    controller.computeInput(balanceInput.text.value)
    controller.printDescriptor()
    stage.hide()
    new BacktestStage(controller).createStage().show()
  })

  stage
}

    
}
