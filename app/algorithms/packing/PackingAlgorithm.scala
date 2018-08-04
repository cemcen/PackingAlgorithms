package algorithms.packing

import algorithms.geometric.Container
import geometry.Polygon

import scala.collection.mutable.ArrayBuffer

abstract class PackingAlgorithm {

  protected var nextPolygon: List[Polygon]
  protected var finalPolygonPosition: ArrayBuffer[Polygon]
  protected var container: Container

  /**
    * Sets the order of the insertion of the polygon.
    */
  private def setNextPolygonList(nextPolygon: List[Polygon]): Unit = this.nextPolygon = nextPolygon

  /**
    * Getter for packing result.
    */
  def getPolygonPositions: ArrayBuffer[Polygon] = finalPolygonPosition

  /**
    * Executes the packing algorithm.
    */
  def executePackingAlgorithm(nextPolygon: List[Polygon], width: Int, height: Int): Unit = {
    container = new Container(width, height)
    setNextPolygonList(nextPolygon)
    executeAlgorithm()
  }

  /**
    * This method is the algorithm.
    */
  def executeAlgorithm(): Unit
}
