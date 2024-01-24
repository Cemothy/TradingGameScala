package de.htwg.se.TradingGame.view.GUI.Stages

import de.htwg.se.TradingGame.Main.controller
import de.htwg.se.TradingGame.controller.IController
import scalafx.application.JFXApp3
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.control.ComboBox
import scalafx.scene.layout.VBox

import java.nio.file.Files
import java.nio.file.Paths
import scala.jdk.CollectionConverters._


object SelectLoadFileStage extends JFXApp3 {
  override def start(): Unit = SelectLoadFileStage(controller).createStage().show()
}

class SelectLoadFileStage(controller: IController) {

  def createStage(): JFXApp3.PrimaryStage = 
    val files = controller.interpreter.gameStateManager.currentState.loadFileList
    val filesObservableBuffer = ObservableBuffer[String](files.toList: _*)
    

    val fileSelectionComboBox = new ComboBox[String] {
        items = filesObservableBuffer
        promptText = "Select a file"
    }

    val loadButton = new Button("Load")

    new JFXApp3.PrimaryStage {
      title = "Select File"
      loadButton.onAction = _ => controller.computeInput(fileSelectionComboBox.getValue.split("\\.")(0))
      scene = new Scene {
        root = new VBox {
          padding = Insets(20)
          children = Seq(fileSelectionComboBox, loadButton)
        }
        stylesheets.add("de/htwg/se/TradingGame/view/GUI/CSS/darkmode.css")
      }
    }
}