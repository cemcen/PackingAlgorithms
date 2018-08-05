package algorithms.packing
import algorithms.geometric.{Container2D, LocusAlgorithm}
import geometry.{Point, Polygon}

import scala.collection.mutable.ArrayBuffer

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
    firstPolygon.movePolygon(leftBottomPoint)
    polygonList += firstPolygon

    // Iterate over nextPolygon array to pack every polygon in the array.
    nextPolygon.tail.indices.foreach( i => {

      // Next polygon to be inserted.
      val insertingPolygon: Polygon = nextPolygon.tail(i)

      // First case Container vs Polygons.
      val containerLocus: Polygon = container.getInnerLocus(insertingPolygon)

      // Best center position.
      var bestCenterPos: Point = null

      // Check every polygon for possible insertion.
      polygonList.indices.foreach(i => {
        // Current polygon
        val polygon: Polygon = polygonList(i)

        // Get locus from polygon inserted.
        val polygonLocus: Polygon = LocusAlgorithm.getLocusOfTwoPolygons(polygon, insertingPolygon)
        val intersectionPoints: List[Point] = containerLocus.intersectPolygon(polygonLocus)

        intersectionPoints.foreach(pnt => {
          // Save centroid.
          val centroid: Point = insertingPolygon.centroid

          // Move Polygon.
          insertingPolygon.movePolygon(pnt)

          // Check if intersection.
          var intersects = false

          polygonList.foreach(pol => {
            if(insertingPolygon.intersectPolygon(pol).size > 1) intersects = true
          })

          // TODO: Packing condition.
          if(!intersects) bestCenterPos = pnt
          insertingPolygon.movePolygon(centroid)
        })
      })

      // Second case Polygons vs Polygons.
      polygonList.indices.foreach(i => {

        // First Polygon
        val polygonA: Polygon = polygonList(i)


        polygonList.indices.filter(j => j > i).foreach(j => {
          // Second Polygon
          val polygonB: Polygon = polygonList(j)

          // Get locus from the two polygons.
          val polygonALocus: Polygon = LocusAlgorithm.getLocusOfTwoPolygons(polygonA, insertingPolygon)
          val polygonBLocus: Polygon = LocusAlgorithm.getLocusOfTwoPolygons(polygonB, insertingPolygon)
          val intersectionPoints: List[Point] = polygonALocus.intersectPolygon(polygonBLocus)

          intersectionPoints.foreach(pnt => {

            // Save centroid.
            val centroid: Point = insertingPolygon.centroid

            // Move Polygon.
            insertingPolygon.movePolygon(pnt)

            // Check if intersection.
            var intersects = false

            polygonList.foreach(pol => {
              if(insertingPolygon.intersectPolygon(pol).size > 1) intersects = true
            })

            // TODO: Packing condition.
            if(!intersects && container.isInside(pnt) && container.getPolygon.intersectPolygon(insertingPolygon).size < 2) bestCenterPos = pnt
            insertingPolygon.movePolygon(centroid)
          })
        })
      })

      // Save polygon Position in the array.
      insertingPolygon.movePolygon(bestCenterPos)
      polygonList += insertingPolygon
    })

    finalPolygonPosition = polygonList
  }

}
