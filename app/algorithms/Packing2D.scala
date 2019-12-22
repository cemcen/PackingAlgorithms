package algorithms

import algorithms.packing.PackingAlgorithm
import algorithms.util.Layer
import dto.dim2D.input.{InputLayer, InputPolygon}
import geometry.{Polygon, PolygonFactory}
import network.Graph

import scala.collection.mutable.ArrayBuffer

class Packing2D {
  private var packingAlgorithm: PackingAlgorithm = _
  private var polygonList: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()

  /**
    * Executes the packing algorithm.
    *
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

  def executeMultiLayerAlgorithm(nextPolygons: List[Layer]): Unit = {

    // First set the polygon list order.
    packingAlgorithm.setLayersPolygonList(nextPolygons)

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
    * Creates new container
    */
  def setContainerDimensions(width: Double, height: Double): Unit = {
    packingAlgorithm.createContainer(width, height)
  }

  /**
    * Sets the algorithm to be used.
    */
  def setPackingAlgorithm(algorithm: PackingAlgorithm): Unit = packingAlgorithm = algorithm
}

object Packing2D {

  private val packing2d: Packing2D = new Packing2D

  /**
    * Set packing algorithm.
    */
  def setPackingAlgorithm(packingAlgorithm: PackingAlgorithm): Unit = {
    packing2d.setPackingAlgorithm(packingAlgorithm)
  }

  /**
    * Interface to create a polygon mesh with geometric packing algorithm.
    * @param data Properties of the input polygons.
    * @param width width of the container.
    * @param height height of the container.
    * @return The list of polygons with local coordinates.
    */
  def createMesh(data: List[InputPolygon], width: Double, height: Double, randomShape: Boolean, regularity: Int, polygonLimit: Int = Int.MaxValue): ArrayBuffer[Polygon] = {

    // The first step of this algorithm is to choose the order of insertion of the polygons.
    val nextPolygon: List[Polygon] = PolygonFactory.createPolygonArrayInsertion(data, height, width, randomShape, regularity, polygonLimit)

    // We need to tell the algorithm on which container we will pack.
    packing2d.setContainerDimensions(width, height)

    // We execute the algorithm.
    packing2d.executeAlgorithm(nextPolygon)

    // Finally retrieve the result.
    packing2d.getPolygonList
  }

  /**
    * Interface to create a polygon mesh with geometric packing algorithm. This is a multilayer packing algorithm.
    * @param layers List with the properties of each layer.
    * @param width width of the container.
    * @return The list of polygons with local coordinates.
    */
  def createMultiLayerMesh(layers: List[InputLayer], width: Double, randomShape: Boolean, polygonLimit: Int = Int.MaxValue): ArrayBuffer[Polygon] = {

    // For each layer we need to have the polygon list with the distribution given.
    var layersNextPolygons: ArrayBuffer[Layer] = new ArrayBuffer[Layer]()
    layers.foreach(lay => {
      val layerNextPolygon: List[Polygon] = PolygonFactory.createPolygonArrayInsertion(lay.polygons, lay.height.get, width, randomShape, lay.regularity.get, polygonLimit)
      layersNextPolygons += Layer(layerNextPolygon, lay.height.get)
    })

    // We need to tell the algorithm on which container we will pack.
    packing2d.setContainerDimensions(width, layers.head.height.get)

    // We execute the algorithm.
    packing2d.executeMultiLayerAlgorithm(layersNextPolygons.toList)

    // Finally retrieve the result.
    packing2d.getPolygonList
  }
}
