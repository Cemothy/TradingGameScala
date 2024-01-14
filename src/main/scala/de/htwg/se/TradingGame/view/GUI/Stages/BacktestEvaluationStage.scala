package de.htwg.se.TradingGame.view.GUI.Stages


import de.htwg.se.TradingGame.Main.controller
import de.htwg.se.TradingGame.controller.IController
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox

object BacktestEvaluationStage extends JFXApp3 {
  override def start(): Unit = BacktestEvaluationStage(controller).createStage().show()
}

class BacktestEvaluationStage(controller: IController) {

  def createStage(): JFXApp3.PrimaryStage = 
    val endButton = new Button("Done")

    new JFXApp3.PrimaryStage {
      title = "Evaluation Stage"
      scene = new Scene {
        root = new VBox(endButton)
        stylesheets.add("de/htwg/se/TradingGame/view/GUI/CSS/darkmode.css")
      }
      endButton.onAction = _ => {
        // Handle end button click
        println("End button clicked")
        // handleEndButtonClick()
      }
    }
}