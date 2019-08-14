package algorithms.packing
import algorithms.geometric.{Container2D, LocusAlgorithm}
import geometry.{Point, Polygon}
import network.Graph

import scala.collection.mutable.ArrayBuffer
import org.scalactic._
import org.scalactic.TripleEquals._
import Tolerance._

/**
  *
  * This class implements the advance front geometric packing heuristic.
  *
  *
  * This approach uses the minskowski sum to search for the optimal position of the next inserted polygon.
  * In each iteration we save the 'front' polygon, that is to say, the polygons that could have a new polygon
  * as neighbour. When inserting a new polygon we need to update which polygon still are on the 'front'.
  *
  */
class AdvanceFrontPacking extends PackingAlgorithm {

  override var nextPolygon: List[Polygon] = _
  override var finalPolygonPosition: ArrayBuffer[Polygon] = _
  override protected var container: Container2D = _
  override var packingTechnique: PackingApproach = _
  override var graph: Graph = _

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

    // For neighbourhood we will use a graph.
    graph = new Graph()
    graph.addContainer(container)

    // Update the graph representing the packing
    firstPolygon.movePolygon(leftBottomPoint)
    graph = graph.addPolygon1Intersection(firstPolygon, container.getPolygon)
    packingTechnique.updateGraph(graph)

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
    //graph = packingTechnique.getGraph

    /*finalPolygonPosition.foreach(pol => {
      var originalPolygon = false
      polygonList.foreach(polygon => {
        if((Math.abs(pol.getArea) - Math.abs(polygon.getArea)) === 0.0 +- 1e-3) {
          var maybe = true
          polygon.points.foreach(pnt => {
            maybe = maybe && pol.points.contains(pnt)
          })
          originalPolygon = originalPolygon || maybe
        }
      })
      if(!originalPolygon)
        pol.setHole()
      pol
    })

    // Filter container polygon found on routes.
    finalPolygonPosition = finalPolygonPosition.filter(pol => {
      var containsAll = true
      container.getPolygon.points.foreach(pnt => {
        containsAll = containsAll && pol.points.contains(pnt)
      })
      !containsAll
    })*/
  }
}
