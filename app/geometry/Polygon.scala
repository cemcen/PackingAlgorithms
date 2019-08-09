package geometry

import scala.collection.mutable.ArrayBuffer
import org.scalactic._
import org.scalactic.TripleEquals._
import Tolerance._

/**
  * This is a class for polygon representation. This polygon will be represented inside a circle of center '_center' and radius
  * 'radius', each point of this polygon will have the center as coordinate system.
  *
  */
class Polygon(var points: List[Point], val radius: Double, val label: String) {

  def this(points: List[Point]) = this(points, -1.0, "")

  private var _centroid: Point = null
  private var _centroidEdgeLongest: Double = -1
  private var _maxDiagonalLength: Double = -1
  private var _area: Double = -1
  private var hole: Boolean = false
  private var container: Boolean = false

  /**
    * Checks if the received polygon intersects with the actual polygon and return the intersection points.
    * Returns a list of points that are the intersection points between them.
    * The boolean returned is used to know if it not intersects (optimization)
    *
    * In this implementation we assume that the polygon given have three or more vertices, that is to say, this polygons
    * are well defined.
    */
  def intersectPolygon(polygon: Polygon): List[Point] = {
    linearPolygonIntersection(this, polygon)
  }

  def linearPolygonIntersection(polygonA: Polygon, polygonB: Polygon): List[Point] = {

    val intersectionPoints: ArrayBuffer[Point] = new ArrayBuffer[Point]()

    // println("Poligono A: " + polygonA.points)
    // println("Poligono B: " + polygonB.points)

    // Used for reference on the polygon.
    var indexPolygonA: Int = 0
    var indexPolygonB: Int = 0

    // movement after first interseccion.
    var aadv: Int = 0
    var bbdv: Int = 0
    var firstInter: Boolean = true

    // We will use an algorithm in time O(n + m). This uses two vectors and cross product to determine the intersection.
    // Second point -> first point. Using ccw vectors over the polygon.
    var vectorPolygonA: Vector = Vector(polygonA.points.head, polygonA.points.tail.head)
    var vectorPolygonB: Vector = Vector(polygonB.points.head, polygonB.points.tail.head)

    val polygonASize: Int = polygonA.points.size
    val polygonBSize: Int = polygonB.points.size

    // Methods that will be used on the algorithm of intersection, these are used to advance the vector on each iteration.
    // This will advance the vector used in polygon A.
    def advanceVectorA(): Unit = {
      indexPolygonA += 1
      aadv += 1
      vectorPolygonA = Vector(polygonA.points(Math.floorMod(indexPolygonA, polygonASize)), polygonA.points(Math.floorMod(indexPolygonA + 1, polygonASize)))
    }

    // This will advance the vector used in polygon B.
    def advanceVectorB(): Unit = {
      indexPolygonB += 1
      bbdv += 1
      vectorPolygonB = Vector(polygonB.points(Math.floorMod(indexPolygonB, polygonBSize)), polygonB.points(Math.floorMod(indexPolygonB + 1, polygonBSize)))
    }

    // We should continue to check an intersection until one of the two polygon had been check entirely
    while ((indexPolygonA < polygonASize || indexPolygonB < polygonBSize) && aadv < 2 * polygonASize && bbdv < 2 * polygonBSize) {

      // Here we have two options, advance vector A or B.
      // We will advance vector A if cross product is positive, advance vector B otherwise.
      // But first we need to check if these two vectors intersects.
      val vIntersectionPoints: List[Point] = vectorPolygonA.intersectVector(vectorPolygonB)
      intersectionPoints ++= vIntersectionPoints
      if(vIntersectionPoints.nonEmpty && firstInter) {
        firstInter = false
        aadv = 0
        bbdv = 0
      }

      // We need to advance one of the vector.
      // Depends on where are one vector to each other.
      val crossProduct: Double = vectorPolygonA x vectorPolygonB

      if (crossProduct > 0) {
        if ((vectorPolygonA x Vector(vectorPolygonA.pointA, vectorPolygonB.pointB)) >= 0) {
          advanceVectorA()
        } else {
          advanceVectorB()
        }
      } else {
        if ((vectorPolygonB x Vector(vectorPolygonB.pointA, vectorPolygonA.pointB)) >= 0) {
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
      sum += (pointB.x - pointA.x) * (pointB.y + pointA.y)
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
      if ((vectorAB x vectorAC) === 0.0 +- 1e-8) {
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
    * Gets the longest distance between a point a its centroid. REMEMBER IS THE SQUARED DISTANCE.
    */
  def centroidEdgeLongest: Double = {

    if(_centroidEdgeLongest == -1) {
      val centroid: Point = this.centroid
      var distance: Double = Double.MinValue

      this.points.foreach(p => {
        val dist: Double = p.distance(centroid)

        if(dist > distance) distance = dist
      })

      _centroidEdgeLongest = Math.sqrt(distance)
    }

    _centroidEdgeLongest
  }

  /**
    * Gets the longest distance between two points of this polygon.
    */
  def maximumDiagonalLength: Double = {

    if(_maxDiagonalLength == -1) {

      var distance: Double = Double.MinValue

      for (i <- points.indices) {
        for (j <- points.indices.filter(k => k != i)) {
          val pointA = points(i)
          val pointB = points(j)

          val dist = pointA.distance(pointB)
          if(dist > distance) {
            distance = dist
          }
        }
      }

      _maxDiagonalLength = Math.sqrt(distance)
    }

    _maxDiagonalLength
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

  /**
    * Returns neighbour points from known point of this polygon.
    * Returns (previous, next)
    */
  def getNearestPoints(pnt: Point): List[Point] = {

    var nearestPoints: ArrayBuffer[Point] = new ArrayBuffer[Point]()

    for (i <- points.indices){
      if(pnt.equals(points(i))){
        nearestPoints += points(Math.floorMod(i - 1 + points.length, points.length))
        nearestPoints += points(Math.floorMod(i + 1, points.length))
      }
    }

    nearestPoints.toList
  }

  /**
    * Returns the area of the polygon.
    */
  def getArea: Double = {

    if(_area == -1){
      _area = 0.0
      for (i <- this.points.indices) {
        val pointA: Point = this.points(i)
        val pointB: Point = this.points((i + 1) % this.points.length)

        _area += (pointA.x + pointB.x) * (pointA.y - pointB.y)
      }
      _area = math.abs(_area * 0.5)
    }

    _area
  }

  /**
    * Returns neighbour points from point inside the perimeter of this polygon.
    */
  def getNearestPointsFromPoint(pnt: Point): List[Point] = {
    var nearestPoints: ArrayBuffer[Point] = new ArrayBuffer[Point]()

    for (i <- points.indices) {
      val pntA: Point = points(i)
      val pntB: Point = points(Math.floorMod(i + 1, points.length))

      val sum: Double = pnt.x * (pntA.y - pntB.y) +
                       pntA.x * (pntB.y - pnt.y) +
                       pntB.x * (pnt.y - pntA.y)

      if (sum === 0.0 +- 1e-8) {

        val maxX: Double = Math.max(pntA.x, pntB.x)
        val minX: Double = Math.min(pntA.x, pntB.x)
        val maxY: Double = Math.max(pntA.y, pntB.y)
        val minY: Double = Math.min(pntA.y, pntB.y)

        if ((minX < pnt.x || minX === pnt.x +- 1e-8) &&
          (pnt.x < maxX || pnt.x === maxX +- 1e-8) &&
          (minY < pnt.y || minY === pnt.y +- 1e-8) &&
          (pnt.y < maxY || pnt.y === maxY +- 1e-8)) {

          nearestPoints += pntA
          nearestPoints += pntB
        }
      }
    }

    nearestPoints.toList
  }

  def isHole: Boolean = hole
  def setHole(): Unit = hole = true

  def isContainer: Boolean = container
  def setContainer(): Unit = container = true

  def getCopy: Polygon = {

    var mPoints = new ArrayBuffer[Point]()
    this.points.foreach(pnt => {
      mPoints += new Point(pnt.x, pnt.y)
    })

    new Polygon(mPoints.toList, this.radius, this.label)
  }

  /**
    * Returns the minimum distance between this two polygons.
    */
  def minimumDistance(polygon: Polygon): Double = {

    var distance: Double = points.head.distance(polygon.points.head)

    points.foreach(pntA => {
      polygon.points.foreach(pntB => {
        val dist: Double = pntA.distance(pntB)
        if(dist < distance) distance = dist
      })
    })

    Math.sqrt(distance)
  }

}

object Polygon {

  def apply(points: List[Point], radius: Double, label: String): Polygon = {

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
    new Polygon(pointsOrderedCCW.toList, radius, label)
  }

}




