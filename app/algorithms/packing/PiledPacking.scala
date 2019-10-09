package algorithms.packing

import algorithms.geometric.Container2D
import geometry.{Point, Polygon}
import network.Graph

import scala.collection.mutable.ArrayBuffer

class PiledPacking(val piles: List[Double]) extends PackingAlgorithm {

  override var nextPolygon: List[Polygon] = List()
  override var finalPolygonPosition: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()
  override protected var container: Container2D = new Container2D(100,100)
  override protected var packingTechnique: PackingApproach = _

  /**
    * Executes the packing algorithm. Classes that extends this abstract class must implement this method.
    */
  override def executeAlgorithm(): Unit = {

    // Store packed polygons.
    var polygonList: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()

    // Insert first polygon on packing.
    val firstPolygon: Polygon = nextPolygon.head
    val locusContainer: Polygon = container.getInnerLocus(firstPolygon)
    val leftBottomPoint: Point = locusContainer.points.head

    // Update the graph representing the packing
    firstPolygon.movePolygon(leftBottomPoint)
    packingTechnique.polygonListInsert(nextPolygon)

    // Update the graph with the polygons connexions.
    packingTechnique.addContainerToGraph(container)
    packingTechnique.addPolygonToGraph(firstPolygon)
    val interPolygon: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()
    interPolygon += container.getPolygon
    packingTechnique.addLinksToGraph(firstPolygon, interPolygon)

    polygonList += firstPolygon
    packingTechnique.addedPolygon(firstPolygon)

    // Iterate over nextPolygon array to pack every polygon in the array.
    nextPolygon.tail.indices.foreach(i => {

      // Next polygon to be inserted.
      val insertingPolygon: Polygon = nextPolygon.tail(i)

      // Best center position.
      val bestCenterPos: Point = packingTechnique.insertNextPolygon(insertingPolygon, container, polygonList)

      // Save polygon Position in the array.
      if(bestCenterPos != null) {
        insertingPolygon.movePolygon(bestCenterPos)
        polygonList += insertingPolygon
        packingTechnique.addedPolygon(insertingPolygon)
      }
    })

    finalPolygonPosition = packingTechnique.getPolygonList
  }
}
