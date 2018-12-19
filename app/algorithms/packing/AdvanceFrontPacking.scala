package algorithms.packing
import algorithms.geometric.{Container2D, LocusAlgorithm}
import geometry.{HalfEdge, Point, Polygon}

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
    firstPolygon.setHalfEdges()
    container.getPolygon.updateHalfEdge(firstPolygon)
    polygonList += firstPolygon

    // Iterate over nextPolygon array to pack every polygon in the array.
    nextPolygon.tail.indices.foreach( i => {

      // Next polygon to be inserted.
      val insertingPolygon: Polygon = nextPolygon.tail(i)

      // We need to be sure the polygon has half edges.
      insertingPolygon.setHalfEdges()

      // First case Container vs Polygons.
      val containerLocus: Polygon = container.getInnerLocus(insertingPolygon)

      // Best center position.
      var bestCenterPos: Point = null

      // Minimum area
      var minimumArea: Double = Double.MaxValue

      // Polygons that will be updated.
      var polygonsUpdated: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()

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

          // TODO: Borrar desde aqui
          // Check if intersection.
          var intersects = false

          polygonList.foreach(pol => {
            if(insertingPolygon.intersectPolygon(pol).size > 1) intersects = true
          })

          // TODO: Packing condition.
          if(!intersects && container.isInside(pnt) && container.getPolygon.intersectPolygon(insertingPolygon).size < 2) bestCenterPos = pnt
          // TODO: Borrar hasta aqui

          /*
          insertingPolygon.resetHalfEdges()
          insertingPolygon.setHalfEdges()

          // Check if intersection.
          var intersects = false

          var auxUpdating: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()

          // Check if this position doesn't intersect with other polygon.
          polygonList.foreach(pol => {
            val checkInterPoints: List[Point] = insertingPolygon.intersectPolygon(pol).distinct
            if(checkInterPoints.size > 1) intersects = true
            else if (checkInterPoints.size == 1) auxUpdating += pol
          })

          // TODO: Packing condition.
          // TODO: It can intersect the container in two position and still be possible.
          if(!intersects && container.isInside(pnt) && (0 until 2 contains container.getPolygon.intersectPolygon(insertingPolygon).size)) {

            var iPoints: List[Point] = List()

            val containerPolygon: Polygon = container.getPolygon
            iPoints = containerPolygon.intersectPolygon(insertingPolygon)
            println("INTERSECTION")
            iPoints.foreach(println)
            println("Polygon")
            containerPolygon.points.foreach(println)
            println("inserting")
            insertingPolygon.points.foreach(println)
            // Here if we have more than one point we have to choose one.
            val polygonAPoint: Point = iPoints.head

            if(containerPolygon.points.contains(polygonAPoint)) iPoints = containerPolygon.getNearestPoints(polygonAPoint)
            else  iPoints = containerPolygon.getNearestPointsFromPoint(polygonAPoint)

            val nextPointA: Point = iPoints.head
            val previousPointA: Point = iPoints.tail.head

            val nextPointAHE: HalfEdge = containerPolygon.getHalfEdge(nextPointA, isInterior = true)
            val previousPointAHE: HalfEdge = containerPolygon.getHalfEdge(previousPointA, isInterior = true)
            val pointAHE: HalfEdge = insertingPolygon.getHalfEdge(polygonAPoint, isInterior = false)

            // The same for the other polygon.
            iPoints = polygon.intersectPolygon(insertingPolygon)
            println("INTERSECTION")
            iPoints.foreach(println)
            println("Polygon")
            polygon.points.foreach(println)
            println("inserting")
            insertingPolygon.points.foreach(println)
            val polygonBPoint: Point = iPoints.head

            if(polygon.points.contains(polygonBPoint)) iPoints = polygon.getNearestPoints(polygonBPoint)
            else  iPoints = polygon.getNearestPointsFromPoint(polygonBPoint)

            val nextPointB: Point = iPoints.tail.head
            val previousPointB: Point = iPoints.head

            println("Insert Keys")
            insertingPolygon.halfEdges.keys.foreach(println)
            println("polygon Keys")
            polygon.halfEdges.keys.foreach(println)

            val nextPointBHE: HalfEdge = polygon.getHalfEdge(nextPointB, isInterior = false)
            val previousPointBHE: HalfEdge = polygon.getHalfEdge(previousPointB, isInterior = false)
            val pointBHE: HalfEdge = insertingPolygon.getHalfEdge(polygonBPoint, isInterior = false)

            val area1: Double = holeArea(nextPointAHE, previousPointBHE, pointAHE, pointBHE)
            val area2: Double = holeArea(nextPointBHE, previousPointAHE, pointBHE, pointAHE)

            println(area1)
            println(area2)
            // Condition minimum density
            if(Math.min(area1, area2) < minimumArea) {
              minimumArea = Math.min(area1, area2)
              bestCenterPos = pnt
              auxUpdating += locusContainer
              polygonsUpdated = auxUpdating
              println("hola")
            }
          }
          */

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
      if(bestCenterPos != null) {
        insertingPolygon.movePolygon(bestCenterPos)
        //insertingPolygon.resetHalfEdges()
        //insertingPolygon.setHalfEdges()
        //polygonsUpdated.foreach(pol => pol.updateHalfEdge(insertingPolygon))
        polygonList += insertingPolygon
      }
    })

    finalPolygonPosition = polygonList
  }

  /**
    * Returns the area of the hole.
    */
  def holeArea(nextHalfEdgeA: HalfEdge, previousHalfEdgeB: HalfEdge,
               pointAHalfEdge: HalfEdge, pointBHalfEdge: HalfEdge): Double = {

    if (pointAHalfEdge.pointA.equals(pointBHalfEdge.pointA)) return 0

    var area: Double = Double.MaxValue

    var previousPoint: Point = pointAHalfEdge.pointA
    var currentHalfEdge: HalfEdge = nextHalfEdgeA
    area = area + (currentHalfEdge.pointA.x + previousPoint.x)*(previousPoint.y - currentHalfEdge.pointA.y)

    while(!currentHalfEdge.pointA.equals(previousHalfEdgeB.pointA)) {
      println("----")
      previousPoint = currentHalfEdge.pointA
      currentHalfEdge = currentHalfEdge.getNextHalfEdge

      area = area + (currentHalfEdge.pointA.x + previousPoint.x)*(previousPoint.y - currentHalfEdge.pointA.y)
    }

    area = area + (pointBHalfEdge.pointA.x + previousPoint.x)*(previousPoint.y - pointBHalfEdge.pointA.y)
    previousPoint = pointBHalfEdge.pointA
    currentHalfEdge = pointBHalfEdge

    while(!currentHalfEdge.pointA.equals(pointAHalfEdge.pointA)) {
      println("*****")
      area = area + (currentHalfEdge.pointA.x + previousPoint.x)*(previousPoint.y - currentHalfEdge.pointA.y)

      previousPoint = currentHalfEdge.pointA
      currentHalfEdge = currentHalfEdge.getNextHalfEdge
    }

    area
  }

}
