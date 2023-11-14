package de.htwg.se.TradingGame.view

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import javafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.chart.LineChart
import scalafx.scene.chart.NumberAxis
import scalafx.scene.control.{Button, ComboBox, Label, TextField}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.control.Tooltip
import scalafx.Includes._



object Gui2 extends JFXApp3 {
    override def start(): Unit = {
        val welcomeLabel = new Label("Welcome to the Tradinggame \n Please enter your balance:")
        val balanceLabel = new Label("Balance: ")
        val balanceTextField = new TextField()
        val submitButton = new Button("Submit")
        val balanceHBox = new HBox(balanceLabel, new Label("0"))
        val inputHBox = new HBox(balanceTextField, submitButton)
        val tickerLabel = new Label("Ticker: ")
        val tickerComboBox = new ComboBox(List("EURUSD", "AUDUSD"))
        val tickerHBox = new HBox(tickerLabel, tickerComboBox)
        val dateLabel = new Label("Date: ")
        val dateTextField = new TextField()
        val hourLabel = new Label("Hour: ")
        val hourTextField = new TextField()
        val submitDateTimeButton = new Button("Submit")
        val dateTimeHBox = new HBox(dateLabel, dateTextField, hourLabel, hourTextField, submitDateTimeButton)
        val dataPointsLabel = new Label("Data Points: ")
        val dataPointsTextField = new TextField()
        val timeFrameLabel = new Label("Time Frame: ")
        val timeFrameComboBox = new ComboBox(List("1min", "5min", "15min", "1hour", "4hour", "1day"))
        val dateTimeDataPointsHBox = new HBox(dateTimeHBox, dataPointsLabel, dataPointsTextField, timeFrameLabel, timeFrameComboBox)
        val xAxis = NumberAxis()
        val yAxis = NumberAxis()
        val lineChart = new LineChart(xAxis, yAxis)
        val graphVBox = new VBox(new Label(""), lineChart, dateTimeDataPointsHBox)
        val vBox = new VBox(welcomeLabel, inputHBox, balanceHBox)

        vBox.setSpacing(10)

        vBox.setPadding(new Insets(10))

        submitButton.onAction = _ => {
            balanceHBox.children.remove(1)
            balanceHBox.children.add(new Label(balanceTextField.text.value))
            vBox.children.remove(inputHBox)
            vBox.children.add(tickerHBox)
            tickerComboBox.onAction = _ => {
                graphVBox.children.remove(0)
                graphVBox.children.add(0, new Label(tickerComboBox.value.value))
                vBox.children.add(graphVBox)
            }
        }

        def filterData(data: List[(LocalDateTime, Double)], dateTime: LocalDateTime, dataPoints: Int, timeFrame: String): List[(LocalDateTime, Double)] = {
            val filteredData = timeFrame match {
                case "1min" => data.filter(_._1.isBefore(dateTime)).takeRight(dataPoints)
                case "5min" => data.filter(_._1.isBefore(dateTime)).filter(_._1.getMinute % 5 == 0).takeRight(dataPoints)
                case "15min" => data.filter(_._1.isBefore(dateTime)).filter(_._1.getMinute % 15 == 0).takeRight(dataPoints)
                case "1hour" => data.filter(_._1.isBefore(dateTime)).filter(_._1.getMinute == 0).takeRight(dataPoints)
                case "4hour" => data.filter(_._1.isBefore(dateTime)).filter(d => d._1.getMinute == 0 && List(0, 4, 8, 12, 16, 20).contains(d._1.getHour)).takeRight(dataPoints)
                case "1day" => data.filter(_._1.isBefore(dateTime)).filter(d => d._1.getMinute == 0 && d._1.getHour == 0).takeRight(dataPoints)
            }
            filteredData
        }

        submitDateTimeButton.onAction = _ => {
            val dateTime = LocalDateTime.parse(s"${dateTextField.text.value},${hourTextField.text.value}", DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm"))
            val symbol = tickerComboBox.value.value
            val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
            val file = new java.io.File(Path).getParent + s"/Symbols/$symbol.csv"
            val fileObj = new File(file)
            if (fileObj.exists()) {
                val dataPoints = dataPointsTextField.text.value.toInt
                val data = io.Source.fromFile(file).getLines().toList.drop(1).map(_.split(",")).map(arr => (LocalDateTime.parse(arr(0)+ "," +  arr(1), DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")), arr(5).toDouble))
                val filteredData = filterData(data, dateTime, dataPoints, timeFrameComboBox.value.value)
                val series = new javafx.scene.chart.XYChart.Series[Number, Number]()
                filteredData.foreach(d => series.getData.add(new javafx.scene.chart.XYChart.Data[Number, Number](d._1.toEpochSecond(java.time.ZoneOffset.UTC), d._2)))
                lineChart.getData.clear()
                lineChart.getData.add(series)
                xAxis.setAutoRanging(false)
                xAxis.setLowerBound(filteredData.head._1.toEpochSecond(java.time.ZoneOffset.UTC))
                xAxis.setUpperBound(filteredData.last._1.toEpochSecond(java.time.ZoneOffset.UTC))
                yAxis.setAutoRanging(false)
                yAxis.setLowerBound(filteredData.map(_._2).min)
                yAxis.setUpperBound(filteredData.map(_._2).max)
                yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()))
                val tooltip = new Tooltip()
                lineChart.setOnMouseMoved((event: MouseEvent) => {
                    if (event.x >= 0 && event.x <= xAxis.getWidth() && event.y >= 0 && event.y <= yAxis.getHeight()) {
                        val xValue = xAxis.getValueForDisplay(event.x)
                        val yValue = yAxis.getValueForDisplay(event.y)
                        val xTime = LocalDateTime.ofEpochSecond(xValue.longValue(), 0, java.time.ZoneOffset.UTC)
                        val yPrice = BigDecimal(yValue.doubleValue()).setScale(5, BigDecimal.RoundingMode.HALF_UP).toDouble
                        tooltip.setText(s"Time: ${xTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm"))}, Price: $yPrice")
                        tooltip.show(lineChart, event.screenX, event.screenY)
                    } else {
                        tooltip.hide()
                    }
                })
            }
        }

        val arrowButton = new Button(">")
        arrowButton.onAction = _ => {
            val dateTime = LocalDateTime.parse(s"${dateTextField.text.value},${hourTextField.text.value}", DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm"))
            val symbol = tickerComboBox.value.value
            val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
            val file = new java.io.File(Path).getParent + s"/Symbols/$symbol.csv"
            val fileObj = new File(file)
            if (fileObj.exists()) {
                val dataPoints = dataPointsTextField.text.value.toInt
                val timeFrame = timeFrameComboBox.value.value
                val data = io.Source.fromFile(file).getLines().toList.drop(1).map(_.split(",")).map(arr => (LocalDateTime.parse(arr(0)+ "," +  arr(1), DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")), arr(5).toDouble))
                val filteredData = filterData(data, dateTime, dataPoints + 1, timeFrame)
                val series = new javafx.scene.chart.XYChart.Series[Number, Number]()
                filteredData.foreach(d => series.getData.add(new javafx.scene.chart.XYChart.Data[Number, Number](d._1.toEpochSecond(java.time.ZoneOffset.UTC), d._2)))
                lineChart.getData.clear()
                lineChart.getData.add(series)
                xAxis.setAutoRanging(false)
                xAxis.setLowerBound(filteredData.head._1.toEpochSecond(java.time.ZoneOffset.UTC))
                xAxis.setUpperBound(filteredData.last._1.toEpochSecond(java.time.ZoneOffset.UTC))
                yAxis.setAutoRanging(false)
                yAxis.setLowerBound(filteredData.map(_._2).min)
                yAxis.setUpperBound(filteredData.map(_._2).max)
                yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()))
                val tooltip = new Tooltip()
                lineChart.setOnMouseMoved((event: MouseEvent) => {
                    val xValue = xAxis.getValueForDisplay(event.x)
                    val yValue = yAxis.getValueForDisplay(event.y)
                    val xTime = LocalDateTime.ofEpochSecond(xValue.longValue(), 0, java.time.ZoneOffset.UTC)
                    val yPrice = BigDecimal(yValue.doubleValue()).setScale(5, BigDecimal.RoundingMode.HALF_UP).toDouble
                    tooltip.setText(s"Time: ${xTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm"))}, Price: $yPrice")
                    tooltip.show(lineChart, event.screenX, event.screenY)
                })
                if (timeFrame == "4hour") {
                    val newDateTime = dateTime.plusHours(4)
                    dateTextField.text = newDateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                    hourTextField.text = newDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                } else {
                    val newDateTime = dateTime.plusMinutes(timeFrame.dropRight(3).toInt)
                    dateTextField.text = newDateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                    hourTextField.text = newDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                }
            }
        }

        val arrowHBox = new HBox(arrowButton)
        graphVBox.children.add(arrowHBox)

        stage = new PrimaryStage {
            title = "Tradinggame"
            scene = new Scene(vBox, 400, 400)
        }
    }
}
