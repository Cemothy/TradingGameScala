package de.htwg.se.TradingGame

import com.google.inject.Guice
import com.google.inject.Injector
import de.htwg.se.TradingGame.controller.IController
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.view.GUI.GUI
import de.htwg.se.TradingGame.view.TUI.TUI
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.application.Platform
import scalafx.scene.control.ButtonType
import scalafx.scene.control.ChoiceDialog
import scalafx.scene.control.ComboBox
import scalafx.scene.control.Dialog
import scalafx.scene.control.TextInputDialog
import scalafx.scene.input.KeyCode.P
import scalafx.stage.FileChooser

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import scala.collection.mutable.ArrayBuffer

object Main extends JFXApp3 {
  val injector: Injector = Guice.createInjector(new TradingGameModule)
  val controller: IController = injector.getInstance(classOf[IController])
  val interpreter = injector.getInstance(classOf[Interpreter])



  val tui = new TUI(controller)
  val gui = new GUI(controller)

  override def start(): Unit = {
  new Thread {
    override def run(): Unit = {   
      while (true) {
        tui.processInputLine()
      }

    }
  }.start()
}
}