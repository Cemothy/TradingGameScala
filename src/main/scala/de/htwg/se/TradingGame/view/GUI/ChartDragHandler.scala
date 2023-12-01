package de.htwg.se.TradingGame.view.GUI

import scalafx.Includes._
import scalafx.scene.input.MouseEvent
import scalafx.scene.input.ScrollEvent
import scalafx.scene.layout.Pane
import scalafx.scene.layout.StackPane

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

    crosshairPane.onMouseMoved = (me: MouseEvent) => {
        crosshair.updateCrosshair(me)
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