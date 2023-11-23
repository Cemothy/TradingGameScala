package de.htwg.se.TradingGame.view.GUI

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Alert}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.control.Alert.AlertType


object SelectionStage extends JFXApp3 {
    override def start(): Unit = {
        stage = createStage()
    }

    def createStage(): PrimaryStage = {
        val liveTradingButton = new Button("Live trading")
        val backtestingButton = new Button("Backtesting")


        val stage = new PrimaryStage {
            title = "Make your choice"
            scene = new Scene(new VBox(10, liveTradingButton, backtestingButton), 300, 200)
        }

        liveTradingButton.setOnAction(_ => {
            val alert = new Alert(AlertType.Information) {
                title = "Not Implemented"
                headerText = "Feature not implemented yet"
                contentText = "This feature will be available in future updates."
            }
            alert.showAndWait()
        })

        backtestingButton.setOnAction(_ => {
            stage.hide()
            BalanceStage.createStage().show()
        })

        stage

       
    }
}
     