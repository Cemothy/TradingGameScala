package de.htwg.se.TradingGame.view.GUI.Stages

import de.htwg.se.TradingGame.Main.controller
import de.htwg.se.TradingGame.controller.IController
import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.control.Label
import scalafx.scene.control.TextField
import scalafx.scene.layout.VBox

object BalanceStage extends JFXApp3 {
  override def start(): Unit = BalanceStage(controller).createStage().show()
}

class BalanceStage(controller: IController) {

  def createStage(): JFXApp3.PrimaryStage = 
    val startButton = Button("Start Backtesting")
    val balanceLabel = Label("What Balance do you want to start with?")
    val balanceInput = TextField()

    new JFXApp3.PrimaryStage {
      title = "Balance Stage"
      startButton.onAction = _ => controller.computeInput(balanceInput.text.value)
      scene = new Scene {
        root = new VBox {
          padding = Insets(20)
          children = Seq(balanceLabel, balanceInput, startButton)
        }
        stylesheets.add("de/htwg/se/TradingGame/view/GUI/CSS/darkmode.css")
      }
    }
}