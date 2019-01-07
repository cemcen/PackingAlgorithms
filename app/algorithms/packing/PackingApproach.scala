package algorithms.packing

import algorithms.geometric.Container2D
import geometry.{Point, Polygon}
import network.Graph

import scala.collection.mutable.ArrayBuffer

abstract class PackingApproach {

  var graph: Graph = new Graph()

  def updateGraph(graph: Graph): Unit = {
    this.graph = graph
  }

  def insertNextPolygon(insertingPolygon: Polygon, container: Container2D, polygonList: ArrayBuffer[Polygon]): Point
  def executeAlgorithm(insertingPolygon: Polygon, pointAnalyzed: Point,
                       container: Container2D, polygonList: ArrayBuffer[Polygon],
                       polygonIntersectionA: Polygon, polygonIntersectionB: Polygon): Point

}
