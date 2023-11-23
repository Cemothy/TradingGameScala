package de.htwg.se.TradingGame.view.GUI
import scala.io
import scala.io.StdIn
import de.htwg.se.TradingGame.controller.Controller
import de.htwg.se.TradingGame.util.Observer
import de.htwg.se.TradingGame.model.* 
import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene.paint._
import scalafx.scene.text.Text
import scalafx.scene.control.{ComboBox, Label, TextField, Button}
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import java.io.File
import scala.io.Source
import scalafx.scene.control.ComboBox
import scalafx.collections.ObservableBuffer
import javafx.scene.chart.XYChart
import scalafx.Includes._
import javafx.collections.FXCollections
import javafx.scene.chart.XYChart.Series
import javafx.scene.chart.XYChart.Data
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset
import javafx.scene.layout.VBox
import scalafx.scene.Group



object GUI extends JFXApp3 {

  override def start(): Unit = {
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")


    stage = new JFXApp3.PrimaryStage {
        title.value = "TradingGame"
        width = 1000
        height = 450
        scene = new Scene {
            fill = Color.rgb(38, 38, 38)
            content = new HBox {
                padding = Insets(20)
                children = Seq(
                    new Text {
                        text = "TradingGame"
                        style = "-fx-font-size: 100pt"
                        fill = new LinearGradient(
                            endX = 0,
                            stops = Stops(PaleGreen, SeaGreen))
                        effect = new DropShadow {
                            color = DarkGreen
                            radius = 15
                            spread = 0.25
                        }
                    },
                    new Label("Select Symbol: "),
                    new ComboBox(Seq("EURUSD")) {
                        onAction = handle {
                            val symbol = value.value
                            if (symbol == "EURUSD") {
                                val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
                                val file = new java.io.File(Path).getParent + s"/Symbols/EURUSD.csv"
                                val source = Source.fromFile(file)
                                val lines = source.getLines().toList
                                source.close()
                                // Convert date strings to Long values
                                val data = lines.map { line =>
                                    val cols = line.split(",").map(_.trim)
                                    val date = LocalDateTime.parse(cols(0) + "," + cols(1), formatter)
                                    val value = cols(5).toDouble
                                    (date.toEpochSecond(ZoneOffset.UTC), value)
                                }
                    
                                // Use Number for both the X and Y types in the Series
                                val series = new Series[Number, Number]()
                                series.setName("EURUSD")
                                val dateTimeField = new TextField {
                                    promptText = "Enter a date and time in the format yyyy.MM.dd,HH:mm"
                                }
                                val dateTimeButton: Button = new Button("Plot data")
                                dateTimeButton.onAction = handle {
                                    val dateTimeString = dateTimeField.text.value
                                    val dateTime = LocalDateTime.parse(dateTimeString, formatter)
                                    val dataBeforeDateTime = data.filter(_._1 < dateTime.toEpochSecond(ZoneOffset.UTC)).takeRight(1000)
                                        
                                    val firstTime = dataBeforeDateTime.map(_._1).min
                                    val lastTime = dataBeforeDateTime.map(_._1).max
                                    val minValue = dataBeforeDateTime.map(_._2).min
                                    val maxValue = dataBeforeDateTime.map(_._2).max
                                    // Create the y-axis with the desired range and tick unit
                                    val yAxis = new NumberAxis(minValue, maxValue, (maxValue - minValue) / 10)
                                    yAxis.setAutoRanging(false)

                                    // Create the x-axis with the desired range and tick unit
                                    val xAxis = new NumberAxis(firstTime, lastTime, (lastTime - firstTime) / 10)
                                    xAxis.setAutoRanging(false)

                                    val chart = new LineChart(xAxis, yAxis)

                                    // Use Number for both the X and Y types in the Series
                                    val series = new Series[Number, Number]()
                                    series.setName("EURUSD")
                                    series.setData(FXCollections.observableArrayList(dataBeforeDateTime.map { case (date, value) => new Data[Number, Number](date, value) }: _*))

                                    chart.getData.add(series)

                                    // Add the chart to the scene
                                    content = new HBox {
                                        padding = Insets(20)
                                        children = Seq(
                                            chart,
                                            new scalafx.scene.layout.VBox {
                                                padding = Insets(20)
                                                children = Seq(
                                                    new Label("Enter a date and time: "),
                                                    dateTimeField,
                                                    dateTimeButton
                                                )
                                            }
                                        )
                                    }
                                }

                                // Add the chart to the scene
                                content = new HBox {
                                    padding = Insets(20)
                                    children = Seq(
                                        new LineChart(new NumberAxis(), new NumberAxis()),
                                        new scalafx.scene.layout.VBox {
                                            padding = Insets(20)
                                            children = Seq(
                                                new Label("Enter a date and time: "),
                                                dateTimeField,
                                                dateTimeButton
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    // Show the stage
    stage.show()

  }
}
