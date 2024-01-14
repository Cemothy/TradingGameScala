package de.htwg.se.TradingGame.view.GUI.Stages

import de.htwg.se.TradingGame.Main.controller
import de.htwg.se.TradingGame.controller.IController
import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.control.TextField
import scalafx.scene.layout.VBox

object SelectNewFileStage extends JFXApp3 {
  override def start(): Unit = SelectNewFileStage(controller).createStage().show()
}

class SelectNewFileStage(controller: IController) {

  def createStage(): JFXApp3.PrimaryStage =
    val fileNameTextField = new TextField {
      promptText = "Enter new file name"
    }
    val createButton = Button("Create")

    new JFXApp3.PrimaryStage {
      title = "Create New File"
      createButton.onAction = _ => controller.computeInput(fileNameTextField.text.value)
      scene = new Scene {
        root = new VBox {
          padding = Insets(20)
          children = Seq(fileNameTextField, createButton)
        }
        stylesheets.add("de/htwg/se/TradingGame/view/GUI/CSS/darkmode.css")
      }
  }
}