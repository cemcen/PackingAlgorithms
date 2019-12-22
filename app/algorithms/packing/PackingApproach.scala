package algorithms.packing

import algorithms.geometric.Container2D
import algorithms.geometric.polygon.network.PolygonGraph
import geometry.{Point, Polygon}
import network.Graph

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import org.scalactic._
import org.scalactic.TripleEquals._
import Tolerance._
import experiments.Experiment

abstract class PackingApproach {

  var polygonGraph: PolygonGraph = new PolygonGraph()
  var interPolygons: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()
  var polygonList: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()
  var polygonListInserted: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()
  var polygonLessArea: Polygon = new Polygon(List())

  protected var distanceBetweenPolygons: ArrayBuffer[mutable.HashMap[Int, Double]] = new ArrayBuffer[mutable.HashMap[Int, Double]]()
  protected var distanceToContainer: mutable.HashMap[Int, Double] = new mutable.HashMap[Int, Double]()
  protected var completedExterior: mutable.HashMap[Point, Boolean] = new mutable.HashMap[Point, Boolean]()

  def insertNextPolygon(insertingPolygon: Polygon, container: Container2D, polygonList: ArrayBuffer[Polygon]): Point
  def executeAlgorithm(insertingPolygon: Polygon, pointAnalyzed: Point,
                       container: Container2D, polygonList: ArrayBuffer[Polygon],
                       polygonIntersectionA: Polygon, polygonIntersectionB: Polygon): Point

  def getPolygonList: ArrayBuffer[Polygon] = {
    this.polygonGraph.getPolygonInGraph
  }

  def polygonListInsert(list: List[Polygon]): Unit = {
    polygonList ++= list
    setPolygonWithLessArea()
  }

  private def setPolygonWithLessArea(): Unit = {
    var area: Double = Double.MaxValue

    polygonList.foreach(pol => {
      if(pol.getArea < area) {
        area = pol.getArea
        polygonLessArea = pol
      }
    })
  }

  def addedPolygon(polygon: Polygon): Unit = {
    distanceBetweenPolygons += new mutable.HashMap[Int, Double]()
    polygonListInserted += polygon

    if(!completedExterior.contains(polygon.centroid)) {
      completedExterior += ((polygon.centroid, false))
    }

    if(interPolygons.nonEmpty) {
      polygonGraph.addPolygonLinksAndUpdateHoles(polygon, interPolygons)

      // Check if neighbourhood is completed.
      checkNeighbourhood()
      if(Experiment.EXPORT_STEPS) {
        printCompletedPolygons()
      }
    }
    polygonList = polygonList.tail
    if(polygon.getArea === polygonLessArea.getArea +- 1e-8) {
      setPolygonWithLessArea()
    }
  }

  def addPolygonToGraph(polygon: Polygon): Unit = {
    polygonGraph.addPolygon(polygon)
    polygonListInserted += polygon
    if(!completedExterior.contains(polygon.centroid)) {
      completedExterior += ((polygon.centroid, false))
    }
  }

  def addContainerToGraph(container: Container2D): Unit = {
    polygonGraph.addContainer(container)
    if(!completedExterior.contains(container.getPolygon.centroid)) {
      completedExterior += ((container.getPolygon.centroid, false))
    }
  }

  def changedHeightContainer(container: Container2D, oldHeight: Double, nHeight: Double): Unit = {
    polygonGraph.changedHeightContainer(container, oldHeight, nHeight)
  }

  def addLinksToGraph(polygon: Polygon, interPolygons: ArrayBuffer[Polygon]): Unit = {
    polygonGraph.firstPolygonLink(polygon, interPolygons.head)
  }

  def printGraph(width: Int, height: Int): Unit = {
    polygonGraph.exportPNGGraph(height, width, route = "debug/graphs/", filename ="polygons_graph.png", circle_size = (10,10))
    polygonGraph.exportPNGGraph(height, width, route = "debug/graphs/", filename ="polygons_container_graph.png", circle_size = (10,10), drawContainer = true)
  }

  def printCompletedPolygons(): Unit = {
    var completePolygons: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()
    completedExterior.foreach(pnt_bol => {
      if(pnt_bol._2) completePolygons += polygonGraph.getPolygon(pnt_bol._1)
    })

    polygonGraph.exportPNGGraphPackingCompleted(route = "debug/packing/",
      filename ="polygons_graph_completed.png", circle_size = (10,10),
      completePolygons)
  }

  /**
    * Check if we can pack more polygons around this polygon.
    * In case it does not we mark this polygon.
    */
  def checkNeighbourhood(): Unit = {
    polygonListInserted.foreach(pol => {
      if(!completedExterior(pol.centroid)) {
        if(!pol.isContainer) {
          completedExterior(pol.centroid) = polygonGraph.checkNeighbourhood(pol, polygonLessArea)
        }
      }
    })
  }
}
