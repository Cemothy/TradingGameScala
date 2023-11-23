import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Polygon}
import scalafx.application.JFXApp3.PrimaryStage

object heart extends JFXApp3 {
    override def start(): Unit = {
        stage = new PrimaryStage {
            title = "Heart"
            scene = new Scene(300, 400) {
                fill = Color.White

                val circle1 = new Circle()
                circle1.centerX = 100
                circle1.centerY = 100
                circle1.radius = 50
                val circle2 = new Circle()
                circle2.centerX = 200
                circle2.centerY = 100
                circle2.radius = 50

                val polygon = new Polygon()
                polygon.points ++= List(50.0, 100.0, 250.0, 100.0, 150.0, 250.0)
                polygon.fill = Color.Red
                circle1.fill = Color.Red
                circle2.fill = Color.Red

                content = List(circle1, circle2, polygon)
            }
        }
    }
}
        


