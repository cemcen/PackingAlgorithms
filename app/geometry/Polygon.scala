package geometry

import scala.collection.mutable.ArrayBuffer

/**
  * This is a class for polygon representation. This polygon will be represented inside a circle of center '_center' and radius
  * 'radius', each point of this polygon will have the center as coordinate system.
  *
  */
class Polygon(val points: List[Point], val radius: Double) {

  def this(points: List[Point]) = this(points, -1.0)

  private var _center = new Point(0, 0)

  /**
    * Checks if the received polygon intersects with the actual polygon and return the intersection points.
    * The returned polygon has no radius defined because we assume that the two given polygon are already placed on the mesh.
    * The boolean returned is used to know if it not intersects (optimization)
    *
    * In this implementation we assume that the polygon given have three or more vertices, that is to say, this polygons
    * are well defined.
    */
  def intersectPolygon(polygon: Polygon): Polygon = {
    val intersectionPoints: ArrayBuffer[Point] = new ArrayBuffer[Point]()

    // Used for reference on the polygon.
    var indexPolygonA: Int = 0
    var indexPolygonB: Int = 0

    // We will use an algorithm in time O(n + m). This uses two vectors and cross product to determine the intersection.
    // Second point -> first point. Using ccw vectors over the polygon.
    var vectorPolygonA: Vector = Vector(this.points.tail.head, this.points.head)
    var vectorPolygonB: Vector = Vector(polygon.points.tail.head, polygon.points.head)

    // Methods that will be used on the algorithm of intersection, these are used to advance the vector on each iteration.
    // This will advance the vector used in polygon A.
    def advanceVectorA(): Unit = {
      indexPolygonA += 1
      vectorPolygonA = Vector(points(Math.floorMod(indexPolygonA + 1, points.size)), points(Math.floorMod(indexPolygonA, points.size)))
    }

    // This will advance the vector used in polygon B.
    def advanceVectorB(): Unit = {
      indexPolygonB += 1
      vectorPolygonB = Vector(points(Math.floorMod(indexPolygonB + 1, points.size)), points(Math.floorMod(indexPolygonB, points.size)))
    }

    // We should continue to check an intersection until one of the two polygon had been check entirely.
    while (indexPolygonA < this.points.size && indexPolygonB < polygon.points.size) {
      // Here we have two options, advance vector A or B.
      // We will advance vector A if cross product is positive, advance vector B otherwise.
      // But first we need to check if these two vectors intersects.
      val vIntersectionPoints: List[Point] = vectorPolygonA.intersectVector(vectorPolygonB)
      intersectionPoints.++(vIntersectionPoints)

      // We need to advance one of the vector.
      // Depends on where are one vector to each other.
      val crossProduct: Double = vectorPolygonA x vectorPolygonB

      if (crossProduct > 0) {
        if (vectorPolygonA.leftOn(vectorPolygonB.pointA)) {
          advanceVectorA()
        } else {
          advanceVectorB()
        }
      } else {
        if (vectorPolygonB.leftOn(vectorPolygonA.pointA)) {
          advanceVectorB()
        } else {
          advanceVectorA()
        }
      }
    }

    // Finally we return
    Polygon(intersectionPoints.toList.distinct, -1.0)
  }

  /**
    * Function that makes the minkowski sum between this polygon and the one as parameter.
    *
    * The implemented algorithm is O(n+m) with n the number of vertices in this polygon and m the number of
    * vertices on the one passed as parameter.
    *
    * TODO: algorithm explanation.
    *
    * @return a polygon with the minkowski sum.
    */
  def minkowskiSum(polygon: Polygon): Polygon = ???

  /**
    * Returns true if the points are oriented ccw.
    */
  def ccw(): Boolean = {
    var sum: Double = 0

    for(i <- 1 to points.size) {
      val pointA = points(Math.floorMod(i, points.size))
      val pointB = points(Math.floorMod(i + 1, points.size))
      sum = (pointB.x - pointA.x)*(pointB.y + pointA.y)
    }

    sum < 0
  }

  /**
    * Getter and setter of attribute center.
    */
  def center: Point = _center

  def center_=(x: Int, y: Int): Unit = {
    _center = new Point(x, y)
  }

}

object Polygon {

  def apply(points: List[Point], radius: Double): Polygon = {

    // Before initializing the polygon we need to order the point ccw.
    // We know that the center of those points is (0,0).
    // We are going to use that reference to order those points.
    val center = new Point(0, 0)

    // The first vector will use the first point as reference and then we will order them from that point.
    val vectorCenterPoint: Vector = Vector(center, points.head)

    // This vector will be used to calculate the angle of each point in the polygon.
    val vectorAuxiliary: Vector = Vector(center, points.head)

    // Then we will order by the size of the angle and sign of the cross product between the vectors.
    case class OrderPolygon(index: Int, angle: Double, crossProduct: Double) extends Ordered[OrderPolygon] {
      def compare(that: OrderPolygon): Int = this.angle compare that.angle
    }

    val orderPolygonList: ArrayBuffer[OrderPolygon] = new ArrayBuffer[OrderPolygon]()
    // Retrieve angle between the point previously selected and the point in the iteration.
    // And calculate the cross product between them so we can know the sign of the angle.
    points.zipWithIndex.foreach {
      case (point, i) =>
        vectorAuxiliary.setPoints(center, point)
        orderPolygonList += OrderPolygon(i, vectorCenterPoint.angleBetween(vectorAuxiliary), vectorCenterPoint x vectorAuxiliary)
    }

    // Now we need to order it by size.
    val sortedList: Seq[OrderPolygon] = orderPolygonList.sortBy(order => order.angle)

    // We need a new list of points so we can order it ccw.
    val pointsOrderedCCW: ArrayBuffer[Point] = new ArrayBuffer[Point]()

    // Then use the angle order to push or queue an element depending on the cross product sign.
    pointsOrderedCCW append points.head
    sortedList.foreach(orderPolygon => {
      if (orderPolygon.index != 0) {
        if (orderPolygon.crossProduct >= 0) {
          pointsOrderedCCW append points(orderPolygon.index)
        } else {
          pointsOrderedCCW prepend points(orderPolygon.index)
        }
      }
    })

    // Finally we create a polygon with points ordered ccw.
    new Polygon(pointsOrderedCCW.toList, radius)
  }

}




