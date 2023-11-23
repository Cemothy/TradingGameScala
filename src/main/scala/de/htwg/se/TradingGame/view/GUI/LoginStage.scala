package de.htwg.se.TradingGame.view.GUI
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, PasswordField, TextField}
import scalafx.scene.layout.{HBox, VBox}

object LoginStage extends JFXApp3 {
  override def start(): Unit = {
    stage = createStage()
  }

  def createStage(): PrimaryStage = {
    val usernameField = new TextField()
    val passwordField = new PasswordField()
    val loginButton = new Button("Login")

    loginButton.setOnAction(_ => {
      val username = usernameField.text.value
      val password = passwordField.text.value
      stage.hide()
      SelectionStage.createStage().show()

        

    })

    val layout = new VBox(10)
    layout.getChildren.addAll(
      new HBox(10, new Label("Username:"), usernameField),
      new HBox(10, new Label("Password:"), passwordField),
      loginButton
    )

    val loginScene = new Scene(layout, 300, 200)
    new PrimaryStage {
        title = "Login"
        scene = loginScene
    }
  }
}