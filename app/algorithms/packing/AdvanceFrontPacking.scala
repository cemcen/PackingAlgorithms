package algorithms.packing
import algorithms.geometric.{Container2D, LocusAlgorithm}
import geometry.{Point, Polygon}
import network.Graph

import scala.collection.mutable.ArrayBuffer
import org.scalactic._
import org.scalactic.TripleEquals._
import Tolerance._
import algorithms.util.Layer
import experiments.Experiment

import scala.collection.mutable

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
  override protected var layersNextPolygons: List[Layer] = _
  override var finalPolygonPosition: ArrayBuffer[Polygon] = _
  override protected var container: Container2D = _
  override var packingTechnique: PackingApproach = _

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

    if(Experiment.DEBUG_MODE) {
      Experiment.startTest(nextPolygon)
    }

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
        if(Experiment.DEBUG_MODE) {
          Experiment.addedPolygon(insertingPolygon)
        }
        insertingPolygon.movePolygon(bestCenterPos)
        polygonList += insertingPolygon
        packingTechnique.addedPolygon(insertingPolygon)
      }
    })

    if(Experiment.DEBUG_MODE) {
      val timeElapsed = Experiment.endTest()
      val polygons = packingTechnique.getPolygonList
      var area: Double = 0

      polygons.foreach(pol => {
        if(!pol.isHole) {
          area += pol.getArea
        }
      })

      Experiment.addExperiment(container.getHeight, container.getWidth, timeElapsed, area/(container.getWidth*container.getHeight))
    }

    finalPolygonPosition = packingTechnique.getPolygonList
  }
}
