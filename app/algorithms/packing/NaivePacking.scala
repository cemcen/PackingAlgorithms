package algorithms.packing

import algorithms.geometric.{Container2D, LocusAlgorithm}
import geometry.{Point, Polygon}

import scala.collection.mutable.ArrayBuffer

class NaivePacking {

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
