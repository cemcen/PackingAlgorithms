package experiments

import java.io.File

import algorithms.util.Layer
import com.github.tototoshi.csv._
import geometry.{Point, Polygon}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Experiment {

  private var f: File = null
  private var csv: Boolean = true
  private var writer: CSVWriter = _
  private val csvSchema = Array("id", "sizes", "vertices", "total", "height", "width", "time", "polygonPacked", "efficiency")
  private val polygonSchema = Array("points", "area", "radius")
  private var data: ArrayBuffer[ArrayBuffer[String]] = new ArrayBuffer[ArrayBuffer[String]]()
  private var id: Int = 1
  var DEBUG_MODE: Boolean = false
  var EXPORT_STEPS: Boolean = false
  private var timer: Long = System.nanoTime
  private var pTypeCount: mutable.HashMap[String, Int] = new mutable.HashMap[String, Int]()
  private var pTypeTotal: mutable.HashMap[String, Int] = new mutable.HashMap[String, Int]()
  private var pTypeSize: mutable.HashMap[String, Double] = new mutable.HashMap[String, Double]()
  private var pTypeNumberOfVertex: mutable.HashMap[String, Int] = new mutable.HashMap[String, Int]()

  def startNewExperiment(route: String, fileName: String, csv: Boolean = true): Unit = {

    // If directory does not exists, we create the directory
    val PATH: String = route
    val paths: Array[String] = PATH.split("/")
    var directoryPath: String = ""

    this.csv = csv

    paths.foreach(path => {
      directoryPath += path
      val directory: File = new File(directoryPath)
      if (!directory.exists()) {
        directory.mkdir()
      }
      directoryPath += "/"
    })

    f = new File(route + fileName + ".csv")
    writer = CSVWriter.open(f)
    data = new ArrayBuffer[ArrayBuffer[String]]()
    id = 1
  }

  def addExperiment(height: Double, width: Double,
                    time: Double, efficiency: Double): Unit = {
    val row: ArrayBuffer[String] = new ArrayBuffer[String]()

    var sizes: ArrayBuffer[Double] = new ArrayBuffer[Double]()
    var vertices: ArrayBuffer[Int] = new ArrayBuffer[Int]()
    var total: ArrayBuffer[Int] = new ArrayBuffer[Int]()
    var polygonPacked: ArrayBuffer[Int] = new ArrayBuffer[Int]()

    for ((k,v) <- pTypeCount) {
      polygonPacked += v
      total += pTypeTotal(k)
      sizes += pTypeSize(k)
      vertices += pTypeNumberOfVertex(k)
    }

    row.+=(
      id.toString,
      sizes mkString "/",
      vertices mkString "/",
      total mkString "/",
      height.toString,
      width.toString,
      time.toString,
      polygonPacked mkString "/",
      efficiency.toString
    )
    data += row
    id += 1
  }

  def addPolygonToFile(polygon: Polygon): Unit = {
    val row: ArrayBuffer[String] = new ArrayBuffer[String]()

    val pointList: List[Point] = polygon.points
    var stringPoints = ""

    pointList.foreach(pnt => {
      stringPoints += pnt.toString + " "
    })

    stringPoints = stringPoints.slice(0,stringPoints.length - 1)

    row.+=(
      stringPoints,
      polygon.radius.toString,
      polygon.getArea.toString
    )
    data += row
    id += 1
  }

  def writeToFile(): Unit = {
    if(csv) writer.writeRow(csvSchema)
    //else writer.writeRow(polygonSchema)
    data.foreach(r => {
      writer.writeRow(r)
    })
    writer.close()
  }

  def startTest(nextPolygon: List[Polygon]): Unit = {
    pTypeCount = new mutable.HashMap[String, Int]()
    pTypeSize = new mutable.HashMap[String, Double]()
    pTypeNumberOfVertex = new mutable.HashMap[String, Int]()
    nextPolygon.foreach(pol => {
      if(!pTypeCount.contains(pol.label)) {
        pTypeCount += ((pol.label, 0))
        pTypeTotal += ((pol.label, 0))
        pTypeSize += ((pol.label, pol.radius))
        pTypeNumberOfVertex += ((pol.label, pol.points.length))
      }
      pTypeTotal(pol.label) += 1
    })
    timer = System.nanoTime
  }

  def startPiledTest(layers: List[Layer]): Unit = {
    pTypeCount = new mutable.HashMap[String, Int]()
    pTypeSize = new mutable.HashMap[String, Double]()
    pTypeNumberOfVertex = new mutable.HashMap[String, Int]()
    layers.foreach(lay => {
      lay.nextPolygon.foreach(pol => {
        if(!pTypeCount.contains(pol.label)) {
          pTypeCount += ((pol.label, 0))
          pTypeTotal += ((pol.label, 0))
          pTypeSize += ((pol.label, pol.radius))
          pTypeNumberOfVertex += ((pol.label, pol.points.length))
        }
        pTypeTotal(pol.label) += 1
      })
    })
    timer = System.nanoTime
  }

  def addedPolygon(pol: Polygon): Unit = {
    pTypeCount(pol.label) += 1
  }

  def endTest(): Double = {
    (System.nanoTime - timer) / 1e9d
  }

  def debugModeOn(): Unit = {
    DEBUG_MODE = true
  }

  def debugModeOff(): Unit = {
    DEBUG_MODE = false
  }

}
