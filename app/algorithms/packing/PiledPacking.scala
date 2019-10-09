package algorithms.packing

import algorithms.geometric.Container2D
import algorithms.util.Layer
import geometry.{Point, Polygon}
import network.Graph

import scala.collection.mutable.ArrayBuffer

class PiledPacking extends PackingAlgorithm {

  override var nextPolygon: List[Polygon] = List()
  override protected var layersNextPolygons: List[Layer] = _
  override var finalPolygonPosition: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()
  override protected var container: Container2D = new Container2D(100,100)
  override protected var packingTechnique: PackingApproach = _

  /**
    * Executes the packing algorithm. Classes that extends this abstract class must implement this method.
    */
  override def executeAlgorithm(): Unit = {

    // Store packed polygons.
    var polygonList: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()

    var nextLayerPolygonList: List[Polygon] = layersNextPolygons.head.nextPolygon

    // Insert first polygon on packing.
    val firstPolygon: Polygon = nextLayerPolygonList.head
    val locusContainer: Polygon = container.getInnerLocus(firstPolygon)
    val leftBottomPoint: Point = locusContainer.points.head

    // Update the graph representing the packing
    firstPolygon.movePolygon(leftBottomPoint)
    packingTechnique.polygonListInsert(nextLayerPolygonList.toList)

    // Update the graph with the polygons connexions.
    packingTechnique.addContainerToGraph(container)
    packingTechnique.addPolygonToGraph(firstPolygon)
    val interPolygon: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()
    interPolygon += container.getPolygon
    packingTechnique.addLinksToGraph(firstPolygon, interPolygon)

    polygonList += firstPolygon
    packingTechnique.addedPolygon(firstPolygon)

    runIteration(nextLayerPolygonList.tail, polygonList)
    layersNextPolygons.tail.foreach(lay => {
      // TODO: Increase container height and run new layer iteration.
      println(lay.height)
      nextLayerPolygonList = lay.nextPolygon
      //runIteration(nextLayerPolygonList, polygonList)
    })

    finalPolygonPosition = packingTechnique.getPolygonList
  }


  def runIteration(nextPolygon: List[Polygon], polygonList: ArrayBuffer[Polygon]): Unit = {
    // Iterate over nextPolygon array to pack every polygon in the array.
    nextPolygon.indices.foreach(i => {

      // Next polygon to be inserted.
      val insertingPolygon: Polygon = nextPolygon(i)

      // Best center position.
      val bestCenterPos: Point = packingTechnique.insertNextPolygon(insertingPolygon, container, polygonList)

      // Save polygon Position in the array.
      if(bestCenterPos != null) {
        insertingPolygon.movePolygon(bestCenterPos)
        polygonList += insertingPolygon
        packingTechnique.addedPolygon(insertingPolygon)
      }
    })
  }
}