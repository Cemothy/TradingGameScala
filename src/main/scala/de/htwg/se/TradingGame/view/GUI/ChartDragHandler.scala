package de.htwg.se.TradingGame.view.GUI

import scalafx.Includes._
import scalafx.scene.input.MouseEvent
import scalafx.scene.input.ScrollEvent
import scalafx.scene.layout.Pane
import scalafx.scene.layout.StackPane
import scalafx.scene.control.Label
import scalafx.scene.paint.Color
import java.text.SimpleDateFormat
import java.util.Date
import java.time.LocalDateTime
import java.time.Instant
import java.time.ZoneOffset
import java.time.Duration
import java.time.format.DateTimeFormatter

class ChartDragHandler(chartpane: LinechartPane, crosshairPane: Pane) extends StackPane {
    var dragStartX: Double = 0
    var dragStartY: Double = 0
    val crosshair = new Crosshair(crosshairPane) // Pass the crosshairPane instead of chartpane
    crosshair.createCrosshair()

    val chartWithCrosshair = new StackPane()
    chartWithCrosshair.children.addAll(chartpane, crosshairPane)

    this.children.addAll(chartWithCrosshair)

    crosshairPane.onMouseExited = (me: MouseEvent) => {
        crosshairPane.setVisible(false)
    }

    onMouseEntered = (me: MouseEvent) => {
        crosshairPane.setVisible(true)
    }

    crosshairPane.onMousePressed = (me: MouseEvent) => {
        dragStartX = me.getX
        dragStartY = me.getY
    }


    def calculateDate(x: Double): String = {
        // Get the height of the chart pane
        val chartWidh = chartpane.width.value

        // Calculate the ratio of the y position to the chart height
        val ratio = x / chartWidh

        // Get the range of your prices
        val dateRange = chartpane.xAxis.delegate.getUpperBound - chartpane.xAxis.delegate.getLowerBound

        // Calculate the price corresponding to the y position
        val price = (1 - ratio) * dateRange + chartpane.xAxis.delegate.getLowerBound

        // Format the price as a string
        val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(price.longValue * 1000), ZoneOffset.UTC)
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd,HH:mm")
        val formattedDate = date.format(formatter)
        formattedDate
    }

    def calculatePrice(y: Double): String = {
    // Get the height of the chart pane
        val chartHeight = chartpane.height.value

        // Calculate the ratio of the y position to the chart height
        val ratio = y / chartHeight

        // Get the range of your prices
        val priceRange = chartpane.yAxis.delegate.getUpperBound - chartpane.yAxis.delegate.getLowerBound

        // Calculate the price corresponding to the y position
        val price = (1 - ratio) * priceRange + chartpane.yAxis.delegate.getLowerBound

        // Format the price as a string
        f"$price%.5f"
    }
    val dateLabel = new Label {
        textFill = Color.Black
        style = "-fx-background-color: white; -fx-padding: 5;"

    }

    val priceLabel = new Label {
        textFill = Color.Black
        style = "-fx-background-color: white; -fx-padding: 5;"
    }
    val datepane = new Pane()
    val pricepane = new Pane()
    datepane.mouseTransparent = true
    pricepane.mouseTransparent = true
    datepane.children.add(dateLabel)
    pricepane.children.add(priceLabel)
    chartWithCrosshair.children.addAll(datepane, pricepane)
    dateLabel.mouseTransparent = true
    priceLabel.mouseTransparent = true

    def updatedatepriceLabel (me: MouseEvent): Unit = {
        val date = calculateDate(me.getX)
        val price = calculatePrice(me.getY)
        dateLabel.text = date
        priceLabel.text = price
        dateLabel.layoutX = me.getX
        dateLabel.layoutY = chartpane.height.value - dateLabel.height.value - 10 
        priceLabel.layoutX = 0
        priceLabel.layoutY = me.getY
    }


    crosshairPane.onMouseMoved = (me: MouseEvent) => {
        crosshair.updateCrosshair(me)
        updatedatepriceLabel(me)

    }

    crosshairPane.onMouseDragged = (me: MouseEvent) => {
        val dragEndX = me.getX
        val dragEndY = me.getY

        // Calculate the difference between the start and end points
        val diffX = dragEndX - dragStartX
        val diffY = dragEndY - dragStartY

        if (dragEndX < 50) {
            val zoomFactor = 0.01 // Adjust this value to control the zoom speed
            val dragY = dragEndY - dragStartY
            val yAxisLowerBound = chartpane.yAxis.delegate.getLowerBound
            val yAxisUpperBound = chartpane.yAxis.delegate.getUpperBound
            val range = yAxisUpperBound - yAxisLowerBound
            val zoomStep = range * zoomFactor

            if (dragY < 0) {
                // Dragging up, show lower numbers
                val newLowerBound = yAxisLowerBound + zoomStep
                val newUpperBound = yAxisUpperBound - zoomStep
                chartpane.yAxis.delegate.setLowerBound(newLowerBound)
                chartpane.yAxis.delegate.setUpperBound(newUpperBound)
            } else {
                // Dragging down, show higher numbers
                val newLowerBound = yAxisLowerBound - zoomStep
                val newUpperBound = yAxisUpperBound + zoomStep
                chartpane.yAxis.delegate.setLowerBound(newLowerBound)
                chartpane.yAxis.delegate.setUpperBound(newUpperBound)
            }
        } else {
            // Calculate the pixel-to-y-axis-unit ratio
            val yAxisHeight = chartpane.yAxis.delegate.getHeight
            val yAxisRange = chartpane.yAxis.delegate.getUpperBound - chartpane.yAxis.delegate.getLowerBound
            val pixelToYAxisUnitRatio = yAxisRange / yAxisHeight

            // Calculate the y-difference in pixels
            val yDiffPixels = dragEndY - dragStartY

            // Convert the y-difference in pixels to the y-difference in y-axis units
            val yDiff = yDiffPixels * pixelToYAxisUnitRatio

            // Adjust the bounds for the Y-axis
            val yAxisLowerBound = chartpane.yAxis.delegate.getLowerBound
            val yAxisUpperBound = chartpane.yAxis.delegate.getUpperBound

            chartpane.yAxis.delegate.setLowerBound(yAxisLowerBound + yDiff)
            chartpane.yAxis.delegate.setUpperBound(yAxisUpperBound + yDiff)

            // Calculate the pixel-to-x-axis-unit ratio
            val xAxisWidth = chartpane.xAxis.delegate.getWidth
            val xAxisRange = chartpane.xAxis.delegate.getUpperBound - chartpane.xAxis.delegate.getLowerBound
            val pixelToXAxisUnitRatio = xAxisRange / xAxisWidth

            // Calculate the x-difference in pixels
            val xDiffPixels = dragEndX - dragStartX

            // Convert the x-difference in pixels to the x-difference in x-axis units
            val xDiff = xDiffPixels * pixelToXAxisUnitRatio

            // Adjust the bounds for the X-axis
            val xAxisLowerBound = chartpane.xAxis.delegate.getLowerBound
            val xAxisUpperBound = chartpane.xAxis.delegate.getUpperBound

            chartpane.xAxis.delegate.setLowerBound(xAxisLowerBound - xDiff)
            chartpane.xAxis.delegate.setUpperBound(xAxisUpperBound - xDiff)

        }
        crosshair.updateCrosshair(me)
        updatedatepriceLabel(me)

        // Update the start points for the next drag event
        dragStartX = dragEndX
        dragStartY = dragEndY
    }

    crosshairPane.onMouseReleased = (me: MouseEvent) => {
        // Reset the start points
        dragStartX = 0
        dragStartY = 0
    }

    crosshairPane.onScroll = (event: ScrollEvent) => {
        val zoomFactor = 0.1 // Adjust this value to control the zoom speed

        val deltaY = event.deltaY
        val xAxisLowerBound = chartpane.xAxis.delegate.getLowerBound
        val xAxisUpperBound = chartpane.xAxis.delegate.getUpperBound
        val range = xAxisUpperBound - xAxisLowerBound
        val zoomStep = range * zoomFactor
        val xAxisWidth = chartpane.xAxis.delegate.getWidth
        val zoomhighlowlines = xAxisWidth * zoomFactor
        if (deltaY < 0) {
                // Zoom out
                val newLowerBound = xAxisLowerBound - zoomStep
                chartpane.xAxis.delegate.setLowerBound(newLowerBound)
                
            } else {
                // Zoom in
                val newLowerBound = xAxisLowerBound + zoomStep
                chartpane.xAxis.delegate.setLowerBound(newLowerBound)
            }
    }
}