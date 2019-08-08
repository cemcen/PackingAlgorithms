package algorithms.packing

import algorithms.geometric.Container2D
import geometry.{Point, Polygon}
import network.Graph

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

abstract class PackingApproach {

  var graph: Graph = new Graph()
  protected var distanceBetweenPolygons: ArrayBuffer[mutable.HashMap[Int, Double]] = new ArrayBuffer[mutable.HashMap[Int, Double]]()
  protected var distanceToContainer: mutable.HashMap[Int, Double] = new mutable.HashMap[Int, Double]()

  def updateGraph(graph: Graph): Unit = {
    this.graph = graph
  }

  def addedPolygon(): Unit = {
    distanceBetweenPolygons += new mutable.HashMap[Int, Double]()
  }

  def insertNextPolygon(insertingPolygon: Polygon, container: Container2D, polygonList: ArrayBuffer[Polygon]): Point
  def executeAlgorithm(insertingPolygon: Polygon, pointAnalyzed: Point,
                       container: Container2D, polygonList: ArrayBuffer[Polygon],
                       polygonIntersectionA: Polygon, polygonIntersectionB: Polygon): Point

  def getPolygonList: ArrayBuffer[Polygon] = this.graph.getPolygonInGraph()
}
