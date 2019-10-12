package algorithms.packing

import algorithms.geometric.Container2D
import algorithms.util.Layer
import geometry.Polygon
import network.Graph

import scala.collection.mutable.ArrayBuffer

abstract class PackingAlgorithm {

  protected var nextPolygon: List[Polygon]
  protected var layersNextPolygons: List[Layer]
  protected var finalPolygonPosition: ArrayBuffer[Polygon]
  protected var container: Container2D
  protected var packingTechnique: PackingApproach

  /**
    * Sets the order of the insertion of the polygon.
    */
  def setNextPolygonList(nextPolygon: List[Polygon]): Unit = {
    this.nextPolygon = nextPolygon
  }

  /**
    * Sets the order of the insertion of the polygon.
    */
  def setLayersPolygonList(nextPolygons: List[Layer]): Unit = this.layersNextPolygons = nextPolygons

  /**
    * Getter for packing result.
    */
  def getPolygonPositions: ArrayBuffer[Polygon] = finalPolygonPosition


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
