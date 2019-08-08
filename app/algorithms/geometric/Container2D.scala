package algorithms.geometric

import geometry.{Point, Polygon}

import scala.collection.mutable.ArrayBuffer

/**
  * This class must be able to manage any request about the packing container.
  *
  * (xPos, yPos) is the point at the left down corner.
  */
class Container2D(width: Double, height: Double, xPos: Double = 0, yPos: Double = 0) {

  // Used for storing the half edges of this polygon.
  private val containerPolygon: Polygon = new Polygon(List(new Point(xPos, yPos), new Point(width,yPos), new Point(width, height), new Point(xPos, height)))

  /**
    * Returns the polygon that makes the centroid of the given polygon
    * when surrounding the inner boundaries of this container.
    */
  def getInnerLocus(polygon: Polygon): Polygon = {
    // We should look for the extreme points of the polygon.
    // This locus should have four points because the container is a rectangle.

    val pointList: List[Point] = polygon.points
    val locusPoints: ArrayBuffer[Point] = new ArrayBuffer[Point]
    var xMax, xMin: Double = pointList.head.x
    var yMax, yMin: Double = pointList.head.y

    // Obtain the bounding box of the polygon
    pointList.foreach(pnt => {
      if(pnt.x > xMax) xMax = pnt.x
      else if(pnt.x < xMin) xMin = pnt.x

      if(pnt.y > yMax) yMax = pnt.y
      else if(pnt.y < yMin) yMin = pnt.y
    })

    def saveLocusPoint(xTranslation: Double,  yTranslation: Double) = {
      locusPoints += new Point(polygon.centroid.x + xTranslation, polygon.centroid.y + yTranslation)
    }

    // Left Bottom Corner
    saveLocusPoint( xPos - xMin, yPos - yMin)

    // Right Bottom Corner
    saveLocusPoint( width - xMax,  yPos - yMin)

    // Right Top Corner
    saveLocusPoint( width - xMax, height - yMax)

    // Left Top Corner.
    saveLocusPoint( xPos - xMin, height - yMax)

    new Polygon(locusPoints.toList)
  }

  /**
    * Check if the given point is inside the container.
    */
  def isInside(point: Point): Boolean = point.x <= width && point.x >= 0 && point.y <= height && point.y >= 0

  /**
    * Transform the container in a Polygon.
    */
  def getPolygon: Polygon = containerPolygon

  def minimumDistance(polygon: Polygon): Double = {
    var distance: Double = Double.MaxValue

    var minX: Double = polygon.points.head.x
    var minY: Double = polygon.points.head.x
    var maxX: Double = polygon.points.head.y
    var maxY: Double = polygon.points.head.y

    polygon.points.foreach(p => {
      if(p.x > maxX) maxX = p.x
      if(p.y > maxY) maxY = p.y
      if(p.x < minX) minX = p.x
      if(p.y < minY) minY = p.x
    })

    distance = Math.min(Math.min(minX, minY), Math.min(height - maxY, width - maxX))

    distance
  }
}

object Container2D {

  def apply(width: Double, height: Double): Container2D = {
    val container: Container2D = new Container2D(width, height)
    container
  }

}