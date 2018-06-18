package algorithms

import algorithms.packing.PackingAlgorithm
import dto.dim2D.input.InputPolygon
import geometry.{Polygon, PolygonFactory}

import scala.collection.mutable.ArrayBuffer

class Packing2D{

  private var containerLength: Double = 0
  private var containerWidth: Double = 0
  private var packingAlgorithm: PackingAlgorithm = _
  private var polygonList: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()

  /**
    * Executes the packing algorithm.
    * @param nextPolygon list with the order of the polygon to be inserted.
    */
  def executeAlgorithm(nextPolygon: List[Polygon]): Unit = {

    // First set the polygon list order.
    packingAlgorithm.setNextPolygonList(nextPolygon)

    // Then execute the algorithm.
    packingAlgorithm.executeAlgorithm()

    // Finally get the result of the algorithm.
    polygonList = packingAlgorithm.getPolygonPositions
  }

  /**
    * Getter for polygon list.
    */
  def getPolygonList: ArrayBuffer[Polygon] = polygonList

  /**
    * Sets container dimensions.
    */
  def setContainerDimensions(width: Double, length: Double): Unit = {
    containerWidth = width
    containerLength = length
  }

  /**
    * Sets the algorithm to be used.
    */
  def setPackingAlgorithm(algorithm: PackingAlgorithm): Unit = packingAlgorithm = algorithm
}

object Packing2D {

  private val packing2d: Packing2D = new Packing2D

  /**
    * Interface to create a polygon mesh with geometric packing algorithm.
    * @param data Properties of the input polygons.
    * @param width width of the container.
    * @param length length of the container.
    * @return The list of polygons with local coordinates.
    */
  def createMesh(data: List[InputPolygon], width: Int, length: Int): ArrayBuffer[Polygon] = {

    // The first step of this algorithm is to choose the order of insertion of the polygons.
    val nextPolygon: List[Polygon] = PolygonFactory.createPolygonArrayInsertion(data)

    // We need to tell the algorithm on which container we will pack.
    packing2d.setContainerDimensions(width, length)

    // We execute the algorithm.
    packing2d.executeAlgorithm(nextPolygon)

    // Finally retrieve the result.
    packing2d.getPolygonList
  }
}
