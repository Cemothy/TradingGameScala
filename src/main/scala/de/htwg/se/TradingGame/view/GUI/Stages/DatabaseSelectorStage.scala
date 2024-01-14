package de.htwg.se.TradingGame.view.GUI.Stages

import de.htwg.se.TradingGame.Main.controller
import de.htwg.se.TradingGame.controller.IController
import de.htwg.se.TradingGame.model.DataSave.TradeData.databaseStrings
import de.htwg.se.TradingGame.model.InterpretterComponent.DatabaseSelectorInterpreter
import scalafx.application.JFXApp3
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.control.ListView
import scalafx.scene.layout.VBox
import scalafx.Includes._

object DatabaseSelectorStage extends JFXApp3 {

  override def start(): Unit = DatabaseSelectorStage(controller).createStage().show()
}

class DatabaseSelectorStage(controller: IController) {

  def createStage(): JFXApp3.PrimaryStage = 
    val databaseList = new ListView[String] {
    val myBuffer: ObservableBuffer[String] = new ObservableBuffer[String]
        myBuffer ++= databaseStrings
        items = myBuffer
    }
    val selectButton = new Button("Select")

    new JFXApp3.PrimaryStage {
      title = "Select Database"
      selectButton.onAction = _ => {
        val selectedIndex = databaseList.selectionModel.value.selectedIndex.value + 1
        controller.computeInput(selectedIndex.toString())
    }
      scene = new Scene {
        root = new VBox {
          padding = Insets(20)
          children = Seq(databaseList, selectButton)
        }
        stylesheets.add("de/htwg/se/TradingGame/view/GUI/CSS/darkmode.css")
      }
    }
}