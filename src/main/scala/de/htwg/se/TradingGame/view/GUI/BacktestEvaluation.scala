package de.htwg.se.TradingGame.view.GUI


import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Alert, TextField}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.Label

object BacktestEvaluation extends JFXApp3 {
    override def start(): Unit = {
        stage = createStage()
    }

    def createStage(): PrimaryStage = {


  val endButton = new Button("Done")

  val stage = new PrimaryStage {
    title = "Evaluation Stage"
    scene = new Scene(new VBox(endButton))
  }

  endButton.setOnAction(_ => {
    stage.hide()

  })

  stage
}
}