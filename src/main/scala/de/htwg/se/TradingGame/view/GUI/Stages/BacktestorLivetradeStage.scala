package de.htwg.se.TradingGame.view.GUI.Stages
import de.htwg.se.TradingGame.Main.controller
import de.htwg.se.TradingGame.controller.IController
import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.Includes._
object BacktestOrLiveTrade extends JFXApp3 {
  override def start(): Unit = 
    val stage = BacktestOrLiveTradeStage(controller).createStage().show()
}

class BacktestOrLiveTradeStage(controller: IController) {

  def createStage(): JFXApp3.PrimaryStage = 
    new JFXApp3.PrimaryStage {
      title = "Backtest or Live Trade"
      scene = new Scene {
        root = new VBox {
          padding = Insets(20)
          children = Seq(
            new Button("Backtest") {
              onAction = _ => controller.computeInput("Backtest")
            },
            new Button("Live Trade") {
              onAction = _ => {
                controller.computeInput("Live Trade")
                new Alert(AlertType.Information) {
                  initOwner(scene().window())
                  title = "Information"
                  headerText = "Live Trade"
                  contentText = "Not yet implemented."
                }.showAndWait()
              }
            }
          )
        }
        stylesheets.add("de/htwg/se/TradingGame/view/GUI/CSS/darkmode.css")
      }
  }
}