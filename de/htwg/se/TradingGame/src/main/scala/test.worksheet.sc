import java.io.{BufferedWriter, File, FileWriter}
//test
object CSVFilter {
  def main(args: Array[String]): Unit = {
    val inputFile = new File("src/Symbols/EURUSD1.csv")
    val outputFile = new File("src/Symbols/EURUSD.csv")

    val source = scala.io.Source.fromFile(inputFile)
    val lines = source.getLines()

    val filteredLines = lines.filter(line => {
      val year = line.split(",")(0).split('.')(0).toInt
      year >= 2023
    })

    val writer = new BufferedWriter(new FileWriter(outputFile))
    filteredLines.foreach(line => {
      writer.write(line + "\n")
    })

    writer.close()
    source.close()
  }
}
