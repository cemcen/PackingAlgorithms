package algorithms.packing

import algorithms.geometric.{Container2D, LocusAlgorithm}
import geometry.{Point, Polygon}

import scala.collection.mutable.ArrayBuffer

class NaivePacking {

  def insertNextPolygon(insertingPolygon: Polygon, container: Container2D, polygonList: ArrayBuffer[Polygon]): Point = {

    // We want to find the best position for the polygon to be inserted. In this approach we prioritize positions on the container
    // than between polygons.
    var bestCenterPos: Point = null

    // First case Container vs Polygons.
    val containerLocus: Polygon = container.getInnerLocus(insertingPolygon)

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

          val insertingPoint: Point = executeAlgorithm(insertingPolygon, pnt, container, polygonList)

          if (insertingPoint != null) {
            bestCenterPos = insertingPoint
          }
        })
      })
    })

    // Check every polygon for possible insertion.
    polygonList.indices.foreach(i => {

      // Current polygon
      val polygon: Polygon = polygonList(i)

      // Get locus from polygon inserted.
      val polygonLocus: Polygon = LocusAlgorithm.getLocusOfTwoPolygons(polygon, insertingPolygon)
      val intersectionPoints: List[Point] = containerLocus.intersectPolygon(polygonLocus)

      intersectionPoints.foreach(pnt => {

        val insertingPoint: Point = executeAlgorithm(insertingPolygon, pnt, container, polygonList)

        if (insertingPoint != null) {
          bestCenterPos = insertingPoint
        }
      })
    })

    bestCenterPos
  }


  def executeAlgorithm(insertingPolygon: Polygon, pointAnalyzed: Point, container: Container2D, polygonList: ArrayBuffer[Polygon]): Point = {

    var bestPosition: Point = null

    // Save centroid.
    val centroid: Point = insertingPolygon.centroid

    // Move Polygon.
    insertingPolygon.movePolygon(pointAnalyzed)

    // Check if intersection.
    var intersects = false

    polygonList.foreach(pol => {
      if (insertingPolygon.intersectPolygon(pol).size > 1) intersects = true
    })

    val containerIntersections: Int = container.getPolygon.intersectPolygon(insertingPolygon).size
    insertingPolygon.movePolygon(centroid)

    if (!intersects && container.isInside(pointAnalyzed) && containerIntersections < 2) bestPosition = pointAnalyzed

    bestPosition
  }

}
