package algorithms.packing

import algorithms.geometric.Container2D
import algorithms.geometric.polygon.network.PolygonGraph
import geometry.{Point, Polygon}
import network.Graph

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

abstract class PackingApproach {

  var graph: Graph = new Graph()
  var polygonGraph: PolygonGraph = new PolygonGraph()
  var interPolygons: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()

  protected var distanceBetweenPolygons: ArrayBuffer[mutable.HashMap[Int, Double]] = new ArrayBuffer[mutable.HashMap[Int, Double]]()
  protected var distanceToContainer: mutable.HashMap[Int, Double] = new mutable.HashMap[Int, Double]()
  protected var completedExterior: mutable.HashMap[Int, Boolean] = new mutable.HashMap[Int, Boolean]()

  def insertNextPolygon(insertingPolygon: Polygon, container: Container2D, polygonList: ArrayBuffer[Polygon]): Point
  def executeAlgorithm(insertingPolygon: Polygon, pointAnalyzed: Point,
                       container: Container2D, polygonList: ArrayBuffer[Polygon],
                       polygonIntersectionA: Polygon, polygonIntersectionB: Polygon): Point

  def getPolygonList: ArrayBuffer[Polygon] = this.graph.getPolygonInGraph()

  def updateGraph(graph: Graph): Unit = {
    this.graph = graph
  }

  def getGraph: Graph = graph

  def addedPolygon(polygon: Polygon): Unit = {
    distanceBetweenPolygons += new mutable.HashMap[Int, Double]()

    if(interPolygons.nonEmpty) {
      polygonGraph.addPolygonLinks(polygon, interPolygons)

      // Check if neighbourhood is completed.
      // If its surrounded by polygons there cannot be
      interPolygons.foreach(pol => {
        checkNeighbourhood(pol)
      })
    }
  }

  def addPolygonToGraph(polygon: Polygon): Unit = {
    polygonGraph.addPolygon(polygon)
  }

  def addLinksToGraph(polygon: Polygon, interPolygons: ArrayBuffer[Polygon]): Unit = {
    polygonGraph.addPolygonLinks(polygon, interPolygons)
  }

  def printGraph(width: Int, height: Int): Unit = {

    graph.exportPNGGraph(height, width, route = "debug/graphs/", filename = "graph.png", circle_size = (10,10))
    polygonGraph.exportPNGGraph(height, width, route = "debug/graphs/", filename ="polygons_graph.png", circle_size = (10,10))
    polygonGraph.exportPNGGraph(height, width,
      route = "debug/graphs/", filename ="polygons_container_graph.png",
      circle_size = (10,10), drawContainer = true)
  }

  /**
    * Check if we can pack more polygons around this polygon.
    * In case it does not we mark this polygon.
    */
  def checkNeighbourhood(polygon: Polygon): Unit = {

  }
}
