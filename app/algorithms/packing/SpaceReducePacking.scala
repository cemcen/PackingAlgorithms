package algorithms.packing

import algorithms.geometric.{Container2D, LocusAlgorithm}
import geometry.{Point, Polygon}
import network.Graph

import scala.collection.mutable.ArrayBuffer

class SpaceReducePacking extends PackingApproach {

  var minimumArea: Double = _
  var bestGraph: Graph = _

  override def insertNextPolygon(insertingPolygon: Polygon, container: Container2D, polygonList: ArrayBuffer[Polygon]): Point = {

    // Reinitialize the two values to be considered, minimum area and the graph that contains that possibility.
    minimumArea = Double.MaxValue
    bestGraph = this.graph

    // We want to find the best position for the polygon to be inserted. In this approach we choose the one that has less area.
    var bestCenterPos: Point = null

    // We need the container locus to choose if a polygon may be attached to him.
    val containerLocus: Polygon = container.getInnerLocus(insertingPolygon)

    // Check every polygon for possible insertion.
    polygonList.indices.foreach(i => {

      // Current polygon
      val polygon: Polygon = polygonList(i)

      // Get locus from polygon inserted.
      val polygonLocus: Polygon = LocusAlgorithm.getLocusOfTwoPolygons(polygon, insertingPolygon)
      val intersectionPoints: List[Point] = containerLocus.intersectPolygon(polygonLocus)

      // If they intersect it means that the polygon can be placed there.
      intersectionPoints.foreach(pnt => {

        val insertingPoint: Point = executeAlgorithm(insertingPolygon, pnt, container, polygonList, container.getPolygon, polygon)

        if (insertingPoint != null) {
          bestCenterPos = insertingPoint
        }
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
          val insertingPoint: Point = executeAlgorithm(insertingPolygon, pnt, container, polygonList, polygonA, polygonB)

          if (insertingPoint != null) {
            bestCenterPos = insertingPoint
          }
        })
      })
    })

    bestCenterPos
  }

  override def executeAlgorithm(insertingPolygon: Polygon, pointAnalyzed: Point,
                                container: Container2D, polygonList: ArrayBuffer[Polygon],
                                polygonIntersectionA: Polygon, polygonIntersectionB: Polygon): Point = {

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

    if (!intersects && container.isInside(pointAnalyzed) && containerIntersections < 2) {

      val maybeBestGraph : Graph = graph.addPolygon2Intersections(insertingPolygon, polygonIntersectionA, polygonIntersectionB)
      val maybeBestMinimumValue: Double = maybeBestGraph.calculateHoleArea(insertingPolygon, polygonIntersectionA)

      if(maybeBestMinimumValue < minimumArea) {
        bestGraph = maybeBestGraph
        minimumArea = maybeBestMinimumValue
        bestPosition = pointAnalyzed
      }
    }

    insertingPolygon.movePolygon(centroid)

    if(bestPosition != null) {
      updateGraph(bestGraph)
    }

    bestPosition
  }
}
