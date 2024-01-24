package de.htwg.se.TradingGame.view.GUI.Stages

import de.htwg.se.TradingGame.Main.controller
import de.htwg.se.TradingGame.controller.IController
import scalafx.application.JFXApp3
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.control.ComboBox
import scalafx.scene.control.DatePicker
import scalafx.scene.control.Label
import scalafx.scene.control.TextField
import scalafx.scene.layout.VBox

import java.time.format.DateTimeFormatter
object ChoosePairAndDateStage extends JFXApp3 {
  override def start(): Unit = ChoosePairAndDateStage(controller).createStage().show()
}

class ChoosePairAndDateStage(controller: IController) {

  def createStage(): JFXApp3.PrimaryStage = {
  val startButton = Button("Start Trading")
  val pairLabel = Label("Select a pair to trade:")
  val pairComboBox = new ComboBox[String](){
    val myBuffer: ObservableBuffer[String] = new ObservableBuffer[String]
    myBuffer ++= controller.interpreter.gameStateManager.currentState.pairList
    items = myBuffer
  }

 

  val dateLabel = Label("Select a date and time (HH:mm):")
  val datePicker = new DatePicker()
  val timeField = TextField()

  new JFXApp3.PrimaryStage {
    title = "Choose Pair and Date Stage"
    startButton.onAction = _ => {
      val date = datePicker.value.value.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
      val time = timeField.text.value
      controller.computeInput(s"${pairComboBox.value.value} $date,$time")
    }
    scene = new Scene {
      root = new VBox {
        padding = Insets(20)
        children = Seq(pairLabel, pairComboBox, dateLabel, datePicker, timeField, startButton)
      }
      stylesheets.add("de/htwg/se/TradingGame/view/GUI/CSS/darkmode.css")
    }
  }
}
}