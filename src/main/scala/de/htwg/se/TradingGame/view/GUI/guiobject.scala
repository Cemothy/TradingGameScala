package de.htwg.se.TradingGame.view.GUI
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import scalafx.collections.ObservableBuffer
import scalafx.scene.input.ScrollEvent
import scalafx.scene.input.MouseEvent
import scalafx.Includes._
import scalafx.geometry.Point2D
import scalafx.scene.control.Button
import scalafx.scene.shape.Line
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.layout.Pane
import scalafx.scene.layout.VBox
import java.time.LocalDateTime
import java.io.File
import java.time.format.DateTimeFormatter
import scalafx.geometry.Side
import scalafx.util.StringConverter
import java.time.Instant
import java.time.ZoneId


object guiobject extends JFXApp3 {
    // Define conversion functions

    override def start(): Unit = {
        stage = new JFXApp3.PrimaryStage {
            title.value = "My App"
            width = 800
            height = 700
            scene = new Scene {
                
                val lineChartPane = new Pane()
                val xAxis = NumberAxis()
                xAxis.tickLabelFormatter = StringConverter.toStringConverter((epochSecond: Number) => {
                val instant = Instant.ofEpochSecond(epochSecond.longValue)
                val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(date)
                })
                val yAxis = NumberAxis()
                def updateYAxis(): Unit = {
                    val yAxisRange = yAxis.getUpperBound - yAxis.getLowerBound
                    val tickUnit = 0.10 * yAxisRange
                    yAxis.setTickUnit(tickUnit)
        
                }
                updateYAxis()
                
                val lineChart = LineChart(xAxis, yAxis)
                lineChartPane.children.add(lineChart)

                lineChart.createSymbols = false


                 
                                            
                val Path: String = new File("src/main/scala/de/htwg/se/TradingGame/model/BrowseInterpreter.scala").getAbsolutePath
                val file = new java.io.File(Path).getParent + s"/Symbols/EURUSDtest.csv"
                val fileObj = new File(file)
                if (fileObj.exists()) {

                val data = io.Source.fromFile(file).getLines().toList.drop(1).map(_.split(",")).map(arr => (LocalDateTime.parse(arr(0)+ "," +  arr(1), DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")), arr(5).toDouble))
                val series = new javafx.scene.chart.XYChart.Series[Number, Number]()
                data.foreach(d => series.getData.add(new javafx.scene.chart.XYChart.Data[Number, Number](d._1.toEpochSecond(java.time.ZoneOffset.UTC), d._2)))
                lineChart.getData.clear()
                lineChart.getData.add(series)
                xAxis.setAutoRanging(false)
                val numDataPoints = data.length
                val timeInterval = data.last._1.toEpochSecond(java.time.ZoneOffset.UTC) - data.head._1.toEpochSecond(java.time.ZoneOffset.UTC)
                val timePerDataPoint = timeInterval / numDataPoints
                val visibleDataPoints = 200
                val lowerBound = data.last._1.toEpochSecond(java.time.ZoneOffset.UTC) - (visibleDataPoints * timePerDataPoint)
                xAxis.setLowerBound(lowerBound)
                xAxis.setUpperBound(data.last._1.toEpochSecond(java.time.ZoneOffset.UTC))
                yAxis.setAutoRanging(false)
                val minValue = data.takeRight(visibleDataPoints).map(_._2).min
                val maxValue = data.takeRight(visibleDataPoints).map(_._2).max
                val range = maxValue - minValue
                yAxis.setLowerBound(minValue - range * 0.1) // Set lower bound to 10% below the minimum value
                yAxis.setUpperBound(maxValue + range * 0.1) // Set upper bound to 10% above the maximum value
                yAxis.setTickUnit(range * 1.2) // Set tick unit to 20% of the range
                updateYAxis()
                }
                xAxis.delegate.setAutoRanging(false)
                yAxis.delegate.setAutoRanging(false)



                lineChart.delegate.YAxis.setSide(Side.RIGHT)



                var startX = 0.0
                var startY = 0.0
                var addLineMode = false // Flag to indicate if "add line" button is clicked
                var addHighLowLineMode = false // Flag to indicate if "high/low line" button is clicked

                // Button to enable "add line" mode
               
               

              
                
                // Horizontal line
                val crosshairVerticalLine = new Line ()
                crosshairVerticalLine.startX = 0
                crosshairVerticalLine.endX = 0
                crosshairVerticalLine.startY = 0
                crosshairVerticalLine.endY = 0
                crosshairVerticalLine.strokeDashArray = Seq(5d, 5d)
                crosshairVerticalLine.mouseTransparent = true

                val crosshairHorizontalLine = new Line ()
                crosshairHorizontalLine.startX = 0
                crosshairHorizontalLine.endX = 0
                crosshairHorizontalLine.startY = 0
                crosshairHorizontalLine.endY = 0
                crosshairHorizontalLine.strokeDashArray = Seq(5d, 5d)
                crosshairHorizontalLine.mouseTransparent = true

                lineChartPane.children.addAll(crosshairVerticalLine, crosshairHorizontalLine)
                
                content = lineChartPane

                

                var lastPoint: Point2D = Point2D.Zero

                lineChart.onMouseMoved = (event: MouseEvent) => {
                    val point = lineChartPane.sceneToLocal(new Point2D(event.sceneX, event.sceneY))
                    if (point.distance(lastPoint) > 1) { // Only update if the mouse has moved more than 1 pixel
                        crosshairVerticalLine.startX = point.x
                        crosshairVerticalLine.endX = point.x
                        crosshairHorizontalLine.startY = point.y
                        crosshairHorizontalLine.endY = point.y
                        crosshairHorizontalLine.startX = 0
                        crosshairHorizontalLine.endX = lineChart.width.value
                        crosshairVerticalLine.startY = 0
                        crosshairVerticalLine.endY = lineChart.height.value
                        lastPoint = point
                    }
                }

                lineChart.onMouseEntered = _ => {
                    crosshairVerticalLine.visible = true
                    crosshairHorizontalLine.visible = true
                }

                lineChart.onMouseExited = _ => {
                    crosshairVerticalLine.visible = false
                    crosshairHorizontalLine.visible = false
                }

                lineChart.onMousePressed = (event: MouseEvent) => {
                    val point = lineChart.sceneToLocal(new Point2D(event.sceneX, event.sceneY))
                    startX = point.x
                    startY = point.y

                   
                   
                }

                lineChart.onMouseDragged = (event: MouseEvent) => {


                
                    val point = lineChartPane.sceneToLocal(new Point2D(event.sceneX, event.sceneY))
                    

                    if(point.getX > lineChart.width.value - 50){
                        val zoomFactor = 0.01 // Adjust this value to control the zoom speed
                        val dragY = point.getY - startY
                        val yAxisLowerBound = yAxis.delegate.getLowerBound
                        val yAxisUpperBound = yAxis.delegate.getUpperBound
                        val range = yAxisUpperBound - yAxisLowerBound
                        val zoomStep = range * zoomFactor

                        if (dragY < 0) {
                            // Dragging up, show lower numbers
                            val newLowerBound = yAxisLowerBound + zoomStep
                            val newUpperBound = yAxisUpperBound - zoomStep
                            yAxis.delegate.setLowerBound(newLowerBound)
                            yAxis.delegate.setUpperBound(newUpperBound)
                            
                        } else {
                            // Dragging down, show higher numbers
                            val newLowerBound = yAxisLowerBound - zoomStep
                            val newUpperBound = yAxisUpperBound + zoomStep
                            yAxis.delegate.setLowerBound(newLowerBound)
                            yAxis.delegate.setUpperBound(newUpperBound)
                            
                        }
                    }else{

                    // Calculate the pixel-to-y-axis-unit ratio
                    val yAxisHeight = yAxis.delegate.getHeight
                    val yAxisRange = yAxis.delegate.getUpperBound - yAxis.delegate.getLowerBound
                    val pixelToYAxisUnitRatio = yAxisRange / yAxisHeight

                    // Calculate the y-difference in pixels
                    val yDiffPixels = point.y - startY

                    // Convert the y-difference in pixels to the y-difference in y-axis units
                    val yDiff = yDiffPixels * pixelToYAxisUnitRatio

                    // Adjust the bounds for the Y-axis
                    val yAxisLowerBound = yAxis.delegate.getLowerBound
                    val yAxisUpperBound = yAxis.delegate.getUpperBound

                    yAxis.delegate.setLowerBound(yAxisLowerBound + yDiff)
                    yAxis.delegate.setUpperBound(yAxisUpperBound + yDiff)

                    // Calculate the pixel-to-x-axis-unit ratio
                    val xAxisWidth = xAxis.delegate.getWidth
                    val xAxisRange = xAxis.delegate.getUpperBound - xAxis.delegate.getLowerBound
                    val pixelToXAxisUnitRatio = xAxisRange / xAxisWidth

                    // Calculate the x-difference in pixels
                    val xDiffPixels = point.x - startX

                    // Convert the x-difference in pixels to the x-difference in x-axis units
                    val xDiff = xDiffPixels * pixelToXAxisUnitRatio

                    // Adjust the bounds for the X-axis
                    val xAxisLowerBound = xAxis.delegate.getLowerBound
                    val xAxisUpperBound = xAxis.delegate.getUpperBound

                    xAxis.delegate.setLowerBound(xAxisLowerBound - xDiff)
                    xAxis.delegate.setUpperBound(xAxisUpperBound - xDiff)

                    crosshairVerticalLine.startX = crosshairVerticalLine.startX.value + xDiffPixels
                    crosshairVerticalLine.endX = crosshairVerticalLine.endX.value + xDiffPixels
                    crosshairHorizontalLine.startY = crosshairHorizontalLine.startY.value + yDiffPixels
                    crosshairHorizontalLine.endY = crosshairHorizontalLine.endY.value + yDiffPixels
                }
                    updateYAxis()
                    startX = point.x
                    startY = point.y
                }

                
                lineChart.onScroll = (event: ScrollEvent) => {
                val zoomFactor = 0.1 // Adjust this value to control the zoom speed

                val deltaY = event.deltaY
                val xAxisLowerBound = xAxis.delegate.getLowerBound
                val xAxisUpperBound = xAxis.delegate.getUpperBound
                val range = xAxisUpperBound - xAxisLowerBound
                val zoomStep = range * zoomFactor
                val xAxisWidth = xAxis.delegate.getWidth
                val zoomhighlowlines = xAxisWidth * zoomFactor

                if (deltaY < 0) {
                    // Zoom out
                    val newLowerBound = xAxisLowerBound - zoomStep
                    xAxis.delegate.setLowerBound(newLowerBound)
                    
                } else {
                    // Zoom in
                    val newLowerBound = xAxisLowerBound + zoomStep
                    xAxis.delegate.setLowerBound(newLowerBound)
                    
                }
            }
                
                    updateYAxis()
            }
        }
    }
}
