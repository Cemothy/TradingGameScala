package de.htwg.se.TradingGame.view.GUI.Stages

import de.htwg.se.TradingGame.Main.controller
import de.htwg.se.TradingGame.controller.IController
import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.control.ChoiceDialog
import scalafx.scene.control.TextInputDialog
import scalafx.scene.layout.VBox

object LoadorNewFileStage extends JFXApp3 {

  override def start(): Unit = {
    val stage = new LoadorNewFileStage(controller).createStage()
    stage.show()
  }
}

class LoadorNewFileStage(controller: IController) {

  def createStage(): JFXApp3.PrimaryStage = 
    val loadButton = Button("Load")
    val newButton = Button("New")

    new JFXApp3.PrimaryStage {
      title = "Select File Stage"
      loadButton.onAction = _ => controller.computeInput("Load")
      newButton.onAction = _ => controller.computeInput("New")
      
      scene = new Scene {
        root = new VBox {
          padding = Insets(20)
          children = Seq(loadButton, newButton)
        }
        stylesheets.add("de/htwg/se/TradingGame/view/GUI/CSS/darkmode.css") // Load the CSS file
      }
    }
}