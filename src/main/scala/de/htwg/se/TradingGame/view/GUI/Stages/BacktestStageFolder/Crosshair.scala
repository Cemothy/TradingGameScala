package de.htwg.se.TradingGame.view.GUI.Stages.BacktestStageFolder

import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Pane
import scalafx.scene.shape.Line
import scalafx.scene.input.KeyCode.C
import scalafx.scene.paint.Color

class Crosshair(chartpane: Pane) {
  val crosshairVertical = new Line(){
    stroke = Color.WHITE
  }
  val crosshairHorizontal = new Line(){
    stroke = Color.WHITE
  }

  def createCrosshair(): Unit =
    chartpane.children.addAll(crosshairVertical, crosshairHorizontal)
    crosshairVertical.setStrokeWidth(0.5)
    crosshairVertical.strokeDashArray = Seq(5d, 5d)
    crosshairVertical.mouseTransparent = true
    crosshairHorizontal.setStrokeWidth(0.5)
    crosshairHorizontal.strokeDashArray = Seq(5d, 5d)
    crosshairHorizontal.mouseTransparent = true

  def updateCrosshair(me: MouseEvent): Unit = 
    crosshairVertical.setStartX(me.getX)
    crosshairVertical.setEndX(me.getX)
    crosshairVertical.setStartY(0)
    crosshairVertical.setEndY(chartpane.height.value)
    crosshairHorizontal.setStartY(me.getY)
    crosshairHorizontal.setEndY(me.getY)
    crosshairHorizontal.setStartX(0)
    crosshairHorizontal.setEndX(chartpane.width.value)
  
}
