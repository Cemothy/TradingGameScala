package de.htwg.se.TradingGame

import com.google.inject.Guice
import de.htwg.se.TradingGame.controller.IController
import de.htwg.se.TradingGame.model.DataSave.TradeData
import de.htwg.se.TradingGame.model.DataSave.TradeDataclass
import de.htwg.se.TradingGame.model.InterpretterComponent.Interpreter
import de.htwg.se.TradingGame.view.GUI.GUI
import de.htwg.se.TradingGame.view.TUI.TUI
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.control.ButtonType
import scalafx.scene.control.ChoiceDialog
import scalafx.scene.control.ComboBox
import scalafx.scene.control.Dialog
import scalafx.scene.control.TextInputDialog
import scalafx.stage.FileChooser

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import scala.collection.mutable.ArrayBuffer
import scalafx.application.Platform
import scalafx.scene.input.KeyCode.P

object Main extends JFXApp3 {
  val injector = Guice.createInjector(new TradingGameModule)
  val controller: IController = injector.getInstance(classOf[IController])
  val interpreter = injector.getInstance(classOf[Interpreter])
  val tradeData = injector.getInstance(classOf[TradeDataclass])

  if (interpreter.interpreterType == "BrowseInterpreter") {
    controller.setBalance(100)
  }

  val tui = new TUI(controller)
  val gui = new GUI(controller)

  override def start(): Unit = {
  

  val dialog = new ChoiceDialog(defaultChoice = "Load", choices = Seq("Load", "New"))
  dialog.title = "Start Game"
  dialog.headerText = "Choose an option"
  dialog.contentText = "Do you want to load an existing game or start a new one?"

  val result = dialog.showAndWait()

  result match {
    case Some(choice) =>
      choice match {
        case "Load" =>
          val path = Paths.get("src\\main\\scala\\de\\htwg\\se\\TradingGame\\Data")
          val files = Files.list(path).map(_.getFileName.toString.stripSuffix(".xml")).toArray.toList
          
          val loadDialog = new ChoiceDialog(defaultChoice = files.head, choices = files)
          loadDialog.title = "Load Game"
          loadDialog.headerText = "Select a file to load:"
          
          val loadResult = loadDialog.showAndWait()
          
          loadResult match {
            case Some(selectedFile) =>
              val fileNameWithoutExtension = selectedFile.asInstanceOf[String].stripSuffix(".json")
              println("loading file: " + fileNameWithoutExtension)
              tradeData.loadData(fileNameWithoutExtension)
            case _ =>
              // Handle case where no file was selected or the dialog was cancelled
          }
        case "New" =>
          val textInputDialog = new TextInputDialog()
          textInputDialog.title = "New Game"
          textInputDialog.headerText = "Enter a name for the new game file:"
          val filename = textInputDialog.showAndWait()
          filename match {
            case Some(name) =>
              TradeData.savename = name
              controller.setBalance(100)
            case None =>
          }
      }
    case None =>
  }

  val tui = new TUI(controller)
  val gui = new GUI(controller)

  new Thread {
    override def run(): Unit = {   
      while (true) {
        tui.processInputLine()
      }

    }
  }.start()
}
}