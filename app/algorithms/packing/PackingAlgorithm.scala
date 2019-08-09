package algorithms.packing

import algorithms.geometric.Container2D
import geometry.Polygon
import network.Graph

import scala.collection.mutable.ArrayBuffer

abstract class PackingAlgorithm {

  protected var nextPolygon: List[Polygon]
  protected var finalPolygonPosition: ArrayBuffer[Polygon]
  protected var container: Container2D
  protected var packingTechnique: PackingApproach
  protected var graph: Graph

  /**
    * Sets the order of the insertion of the polygon.
    */
  def setNextPolygonList(nextPolygon: List[Polygon]): Unit = this.nextPolygon = nextPolygon

  /**
    * Getter for packing result.
    */
  def getPolygonPositions: ArrayBuffer[Polygon] = finalPolygonPosition


  /**
    * Get packing graph.
    */
  def getGraph: Graph = graph

  /**
    * Creates a new container.
    */
  def createContainer(width: Double, height: Double): Unit = {
    container = Container2D(width, height)
  }

  /**
    * This method is the algorithm.
    */
  def executeAlgorithm(): Unit

  def setPackingTechnique(packing: PackingApproach): Unit = {
    packingTechnique = packing
  }
}
