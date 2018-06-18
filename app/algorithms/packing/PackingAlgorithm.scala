package algorithms.packing

import geometry.Polygon

import scala.collection.mutable.ArrayBuffer

abstract class PackingAlgorithm {

  var nextPolygon: List[Polygon]
  var finalPolygonPosition: ArrayBuffer[Polygon]

  /**
    * Sets the order of the insertion of the polygon.
    */
  def setNextPolygonList(nextPolygon: List[Polygon]): Unit = this.nextPolygon = nextPolygon

  /**
    * Getter for packing result.
    */
  def getPolygonPositions: ArrayBuffer[Polygon] = finalPolygonPosition

  /**
    * Executes the packing algorithm. Classes that extends this abstract class must implement this method.
    */
  def executeAlgorithm(): Unit
}
