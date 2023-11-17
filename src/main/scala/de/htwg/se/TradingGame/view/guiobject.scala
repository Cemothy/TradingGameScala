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


object guiobject extends JFXApp3 {
    override def start(): Unit = {
        stage = new JFXApp3.PrimaryStage {
            title.value = "My App"
            width = 680
            height = 700
            scene = new Scene {
                val xAxis = NumberAxis()
                val yAxis = NumberAxis()
                val lineChart = LineChart(xAxis, yAxis)
                lineChart.createSymbols = false

                val series = XYChart.Series[Number, Number]("My Series", ObservableBuffer((1, 2), (2, 3), (3, 4)).map { case (x, y) => XYChart.Data[Number, Number](x, y) })
                lineChart.getData.add(series)
                content = lineChart
                xAxis.delegate.setAutoRanging(false)
                yAxis.delegate.setAutoRanging(false)
                var startX = 0.0
                var startY = 0.0
                var addLineMode = false // Flag to indicate if "add line" button is clicked

                // Button to enable "add line" mode
                val addLineButton = new Button("Add Line")
                addLineButton.layoutX = 10
                addLineButton.layoutY = 400
                addLineButton.onAction = () => {
                    addLineMode = true
                }
                var horizontalLines: List[Line] = List()
                content += addLineButton

                // Horizontal line


                lineChart.onMousePressed = (event: MouseEvent) => {
                    startX = event.sceneX
                    startY = event.sceneY

                    if (addLineMode) {
                        // Create a horizontal line
                        val line = new Line()
                            line.startX = 0
                            line.startY = event.sceneY
                            line.endX = lineChart.width.value
                            line.endY = event.sceneY
                        
                        horizontalLines = line :: horizontalLines
                        content += line
                        addLineMode = false
                    }
                }

                lineChart.onMouseDragged = (event: MouseEvent) => {
                    // Calculate the pixel-to-y-axis-unit ratio
                    val yAxisHeight = yAxis.delegate.getHeight
                    val yAxisRange = yAxis.delegate.getUpperBound - yAxis.delegate.getLowerBound
                    val pixelToYAxisUnitRatio = yAxisRange / yAxisHeight

                    // Calculate the y-difference in pixels
                    val yDiffPixels = event.sceneY - startY

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
                    val xDiffPixels = event.sceneX - startX

                    // Convert the x-difference in pixels to the x-difference in x-axis units
                    val xDiff = xDiffPixels * pixelToXAxisUnitRatio

                    // Adjust the bounds for the X-axis
                    val xAxisLowerBound = xAxis.delegate.getLowerBound
                    val xAxisUpperBound = xAxis.delegate.getUpperBound

                    xAxis.delegate.setLowerBound(xAxisLowerBound - xDiff)
                    xAxis.delegate.setUpperBound(xAxisUpperBound - xDiff)
                    
                    horizontalLines.foreach { line =>
                        
                        line.startY = event.sceneY + (line.startY.value - startY)
                        line.endY = event.sceneY + (line.startY.value - startY)
                    }

                    startX = event.x
                    startY = event.y
                    // Update the position of the horizontal line


                }

                lineChart.onScroll = (event: ScrollEvent) => {
                    val zoomFactor = 1.1 // Adjust this value to control the zoom speed

                    val deltaY = event.deltaY
                    if (deltaY < 0) {
                        // Zoom out


                        xAxis.delegate.setLowerBound(xAxis.delegate.getLowerBound * zoomFactor)

                    } else {
                        // Zoom in


                        xAxis.delegate.setLowerBound(xAxis.delegate.getLowerBound / zoomFactor)

                    }

                    lineChart.requestLayout() // Request a layout pass
                }
            }
        }
    }
}
