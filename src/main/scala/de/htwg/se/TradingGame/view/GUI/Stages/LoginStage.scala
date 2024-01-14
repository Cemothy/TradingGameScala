package de.htwg.se.TradingGame.view.GUI.Stages

import de.htwg.se.TradingGame.Main.controller
import de.htwg.se.TradingGame.controller.IController
import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.control.PasswordField
import scalafx.scene.control.TextField
import scalafx.scene.layout.VBox

object LoginStage extends JFXApp3 {
  override def start(): Unit = LoginStage(controller).createStage().show()
}

class LoginStage(controller: IController) {

  def createStage(): JFXApp3.PrimaryStage = {
    val usernameField = new TextField {
      promptText = "Username"
    }
    val passwordField = new PasswordField {
      promptText = "Password"
    }
    val loginButton = Button("Login")

    new JFXApp3.PrimaryStage {
      title = "Login"
      loginButton.onAction = _ => controller.computeInput(usernameField.text.value + " " + passwordField.text.value)
      
      scene = new Scene {
        root = new VBox {
          padding = Insets(20)
          children = Seq(usernameField, passwordField, loginButton)
        }
        stylesheets.add("de/htwg/se/TradingGame/view/GUI/CSS/darkmode.css")
      }
    }
  }
}