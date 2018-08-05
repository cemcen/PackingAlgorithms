package geometry

import scala.collection.mutable.ArrayBuffer

/**
  * This is a class for polygon representation. This polygon will be represented inside a circle of center '_center' and radius
  * 'radius', each point of this polygon will have the center as coordinate system.
  *
  */
class Polygon(val points: List[Point], val radius: Double) {
  def this(points: List[Point]) = this(points, -1.0)

  private var _centroid: Point = null

  /**
    * Checks if the received polygon intersects with the actual polygon and return the intersection points.
    * Returns a list of points that are the intersection points between them.
    * The boolean returned is used to know if it not intersects (optimization)
    *
    * In this implementation we assume that the polygon given have three or more vertices, that is to say, this polygons
    * are well defined.
    */
  def intersectPolygon(polygon: Polygon): List[Point] = {
    val intersectionPoints: ArrayBuffer[Point] = new ArrayBuffer[Point]()

    // Used for reference on the polygon.
    var indexPolygonA: Int = 0
    var indexPolygonB: Int = 0

    // We will use an algorithm in time O(n + m). This uses two vectors and cross product to determine the intersection.
    // Second point -> first point. Using ccw vectors over the polygon.
    var vectorPolygonA: Vector = Vector(this.points.tail.head, this.points.head)
    var vectorPolygonB: Vector = Vector(polygon.points.tail.head, polygon.points.head)

    val polygonASize: Int = this.points.size
    val polygonBSize: Int = polygon.points.size

    // Methods that will be used on the algorithm of intersection, these are used to advance the vector on each iteration.
    // This will advance the vector used in polygon A.
    def advanceVectorA(): Unit = {
      indexPolygonA += 1
      vectorPolygonA = Vector(this.points(Math.floorMod(indexPolygonA + 1, polygonASize)), this.points(Math.floorMod(indexPolygonA, polygonASize)))
    }

    // This will advance the vector used in polygon B.
    def advanceVectorB(): Unit = {
      indexPolygonB += 1
      vectorPolygonB = Vector(polygon.points(Math.floorMod(indexPolygonB + 1, polygonBSize)), polygon.points(Math.floorMod(indexPolygonB, polygonBSize)))
    }

    // We should continue to check an intersection until one of the two polygon had been check entirely.
    while (indexPolygonA < this.points.size * 2 && indexPolygonB < polygon.points.size * 2) {
      // Here we have two options, advance vector A or B.
      // We will advance vector A if cross product is positive, advance vector B otherwise.
      // But first we need to check if these two vectors intersects.
      val vIntersectionPoints: List[Point] = vectorPolygonA.intersectVector(vectorPolygonB)
      intersectionPoints ++= vIntersectionPoints

      // For debugging
//       println("Intersection: " + vIntersectionPoints)
//       println("-----------------------------------")
//       println("-----------------------------------")
//       println("VectorA: " +  vectorPolygonA.pointA + ";" + vectorPolygonA.pointB)
//       println("VectorB: " +  vectorPolygonB.pointA + ";" + vectorPolygonB.pointB)

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
    intersectionPoints.toList.distinct
  }


  /**
    * Checks if the point given is inside this polygon.
    */
  def pointInsidePolygon(point: Point): Boolean = {

    // We are going to check if the point is at the left of each edge.
    var isInside: Boolean = true
    for (i <- 0 to points.size) {
      val vectorA: Vector = Vector(points(Math.floorMod(i + 1, points.size)), points(Math.floorMod(i, points.size)))
      isInside &&= vectorA.leftOn(point)
    }

    isInside
  }

  /**
    * Returns true if the points are oriented ccw.
    */
  def ccw(): Boolean = {
    var sum: Double = 0

    for (i <- 1 to points.size) {
      val pointA = points(Math.floorMod(i, points.size))
      val pointB = points(Math.floorMod(i + 1, points.size))
      sum = (pointB.x - pointA.x) * (pointB.y + pointA.y)
    }

    sum < 0
  }

  /**
    * Returns points that are collinear with given one.
    */
  def getCollinear(point: Point): ArrayBuffer[Point] = {
    var collinear: ArrayBuffer[Point] = new ArrayBuffer[Point]()

    for (i <- points.indices) {
      val pointA: Point = points(Math.floorMod(i, points.size))
      val pointB: Point = points(Math.floorMod(i + 1, points.size))
      val vectorAB: Vector = Vector(pointA, pointB)
      val vectorAC: Vector = Vector(pointA, point)

      // It is at this vector's left if cross product is negative.
      if ((vectorAB x vectorAC) == 0) {
        collinear ++= List(pointA, pointB)
      }
    }

    collinear
  }

  /**
    * Getter and setter of attribute center.
    */
  def centroid: Point = {
    if (_centroid == null) {
      // Bourke, Paul (July 1997). "Calculating the area and centroid of a polygon".

      var xCoordinate: Double = 0
      var yCoordinate: Double = 0

      // Signed area
      var A: Double = 0

      for (i <- points.indices) {
        val pointA = points(Math.floorMod(i, points.size))
        val pointB = points(Math.floorMod(i + 1, points.size))
        val K: Double = pointA.x * pointB.y - pointB.x * pointA.y
        xCoordinate += (pointA.x + pointB.x) * K
        yCoordinate += (pointA.y + pointB.y) * K
        A += K
      }

      xCoordinate /= (3 * A)
      yCoordinate /= (3 * A)

      _centroid = new Point(xCoordinate, yCoordinate)
    }
    _centroid
  }

  /**
    * Moves center to the given Points. Also translate the other points of the polygon.
    */
  def movePolygon(move: Point): Unit = {
    val xTranslation: Double = move.x - centroid.x
    val yTranslation: Double = move.y - centroid.y

    // Move the centroid.
    centroid.x = move.x
    centroid.y = move.y

    // Move all points of the polygon
    points.foreach(pnt => {
      pnt.x += xTranslation
      pnt.y += yTranslation
    })
  }

  def hasAllPointsPositive: Boolean = {
    points.foldRight(true)( (a,b) => {
      b && (a.x > 0 && a.y > 0)
    })
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
      //println(orderPolygon)
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




