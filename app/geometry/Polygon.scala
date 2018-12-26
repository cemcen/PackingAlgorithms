package geometry

import scala.collection.mutable
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

  var halfEdges: mutable.HashMap[Point, ArrayBuffer[HalfEdge]] = new mutable.HashMap[Point, ArrayBuffer[HalfEdge]]()
  private var point_polygon: mutable.HashMap[Point, ArrayBuffer[Polygon]] = new mutable.HashMap[Point, ArrayBuffer[Polygon]]()
  private var _centroid: Point = null
  private var _area: Double = -1

  /**
    * Checks if the received polygon intersects with the actual polygon and return the intersection points.
    * Returns a list of points that are the intersection points between them.
    * The boolean returned is used to know if it not intersects (optimization)
    *
    * In this implementation we assume that the polygon given have three or more vertices, that is to say, this polygons
    * are well defined.
    */
  def intersectPolygon(polygon: Polygon): List[Point] = {
    /*val intersectionPoints: ArrayBuffer[Point] = new ArrayBuffer[Point]()
    val polygonASize: Int = this.points.size
    val polygonBSize: Int = polygon.points.size

    for (indexPolygonA <- this.points.indices) {
      for (indexPolygonB <- polygon.points.indices){
        val vectorPolygonA: Vector = Vector(this.points(Math.floorMod(indexPolygonA + 1, polygonASize)), this.points(Math.floorMod(indexPolygonA, polygonASize)))
        val vectorPolygonB: Vector = Vector(polygon.points(Math.floorMod(indexPolygonB + 1, polygonBSize)), polygon.points(Math.floorMod(indexPolygonB, polygonBSize)))

        val vIntersectionPoints: List[Point] = vectorPolygonA.intersectVector(vectorPolygonB)
        vIntersectionPoints.foreach(pnt => if(!intersectionPoints.contains(pnt)) intersectionPoints += pnt)
      }
    }

    intersectionPoints.distinct.toList*/
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


  /** ALL METHODS OF HALF EDGE **/

  /**
    * Here we initialize the half edges of this polygon.
    *
    * We only use them when needed.
    */
  def setHalfEdges(): Unit = {

    // Create all half edges of every point.
    for (i <- points.indices) {
      var pHalfEdge: ArrayBuffer[HalfEdge] = new ArrayBuffer[HalfEdge]()

      val previousP = points(Math.floorMod(i - 1 + points.length, points.length))
      val nextP = points(Math.floorMod(i + 1, points.length))

      pHalfEdge += new HalfEdge(points(i), nextP, true, this)
      pHalfEdge += new HalfEdge(points(i), previousP, false, this)

      point_polygon += ((points(i), new ArrayBuffer[Polygon]()))
      halfEdges += ((points(i), pHalfEdge))
    }

    // set pair, next and previous.
    for (i <- points.indices) {
      val currentHE: ArrayBuffer[HalfEdge] = halfEdges(points(i))
      val previousHE: ArrayBuffer[HalfEdge] = halfEdges(points(Math.floorMod(i - 1 + points.length, points.length)))
      val nextHE: ArrayBuffer[HalfEdge] = halfEdges(points(Math.floorMod(i + 1, points.length)))

      val previousIHE: HalfEdge = previousHE.filter(_.isInterior)(0)
      val previousEHE: HalfEdge = previousHE.filter(!_.isInterior)(0)
      val nextIHE: HalfEdge = nextHE.filter(_.isInterior)(0)
      val nextEHE: HalfEdge = nextHE.filter(!_.isInterior)(0)
      val currentIHE: HalfEdge = currentHE.filter(_.isInterior)(0)
      val currentEHE: HalfEdge = currentHE.filter(!_.isInterior)(0)

      currentIHE.nextHalfEdge(nextIHE)
      currentIHE.pairHalfEdge(nextEHE)
      currentIHE.previousHalfEdge(previousIHE)
      currentEHE.nextHalfEdge(previousEHE)
      currentEHE.pairHalfEdge(previousIHE)
      currentEHE.previousHalfEdge(nextEHE)
    }
  }

  /**
    * Updates half edge structure on intersection points.
    */
  def updateHalfEdge(polygon: Polygon): Unit = {

    val intersectionPoints: List[Point] = this.intersectPolygon(polygon)

    // Add intersection points to this polygon.
    intersectionPoints.foreach(pnt => {

      // Two possible cases. Contained or not contained.
      if(!points.contains(pnt)){

        /**
          * A ------------------- Y
          *   |
          *   |    * C
          *   |   /
          *   |  /
          *   | /
          * F * D
          *   | \
          *   |  \
          *   |   \
          *   |    * E
          *   |
          *   |
          * B * ------------------ X
          *
          *  This code follows this figure.
          */
        // If pnt is not in this polygon we must add this point to this polygon.
        // This will help to update the half edge structure.

        var pPolAHalfEdge: ArrayBuffer[HalfEdge] = new ArrayBuffer[HalfEdge]()
        val pPolBHalfEdge: ArrayBuffer[HalfEdge] = polygon.halfEdges(pnt)

        // We need to have the previous point and
        // the next point of this intersection point in both polygons.
        var nearestPoints: List[Point] = polygon.getNearestPoints(pnt)

        val nextPointPolygonB: Point = nearestPoints.tail.head

        nearestPoints = this.getNearestPointsFromPoint(pnt)

        val previousPointPolygonA: Point = nearestPoints.head
        val nextPointPolygonA: Point = nearestPoints.tail.head

        val DC: HalfEdge = pPolBHalfEdge.filter(!_.isInterior)(0)
        val AF: HalfEdge = this.halfEdges(previousPointPolygonA).filter(_.isInterior)(0)
        AF.setPointB(pnt)
        AF.nextHalfEdge(DC)

        val FB: HalfEdge = new HalfEdge(pnt, nextPointPolygonA, true, this)
        val BX: HalfEdge = this.halfEdges(nextPointPolygonA).filter(_.isInterior)(0)
        FB.nextHalfEdge(BX)

        val EF: HalfEdge = polygon.halfEdges(nextPointPolygonB).filter(!_.isInterior)(0)
        EF.nextHalfEdge(FB)

        val BF: HalfEdge = this.halfEdges(nextPointPolygonA).filter(!_.isInterior)(0)
        val FA: HalfEdge = new HalfEdge(pnt, previousPointPolygonA, false, this)
        BF.setPointB(pnt)
        BF.nextHalfEdge(FA)

        val AY: HalfEdge = this.halfEdges(previousPointPolygonA).filter(!_.isInterior)(0)
        FA.previousHalfEdge(AY)

        AF.pairHalfEdge(FA)
        BF.pairHalfEdge(FB)

        pPolAHalfEdge ++= List(FA, FB)
        halfEdges += ((pnt, pPolAHalfEdge))

        var newPointsArrange: ArrayBuffer[Point] = new ArrayBuffer[Point]()

        for(i <- points.indices) {
          newPointsArrange += points(i)
          if(points(i) == previousPointPolygonA) {
            newPointsArrange += pnt
          }
        }

        points = newPointsArrange.toList
      } else {
        /**
          * A *------------------ Y (previous)
          *   | \------D (previous)
          *   |  \
          *   |   \
          *   |    \
          *   |     C (next)
          *   |
          * B * ----------------- X
          * (next)
          *
          *  This code follows this figure.
          */

        val nearestPointsB: List[Point] = polygon.getNearestPoints(pnt)
        val nearestPointsA: List[Point] = this.getNearestPoints(pnt)

        val C: Point = nearestPointsB.tail.head
        val A: Point = pnt
        val Y: Point = nearestPointsA.head

        val YA: HalfEdge = this.halfEdges(Y).filter(_.isInterior)(0)
        val AD: HalfEdge = polygon.halfEdges(A).filter(!_.isInterior)(0)
        val CA: HalfEdge = polygon.halfEdges(C).filter(!_.isInterior)(0)
        val AB: HalfEdge = this.halfEdges(A).filter(_.isInterior)(0)

        CA.nextHalfEdge(AB)
        YA.nextHalfEdge(AD)
      }


      if (!point_polygon.contains(pnt)){
        point_polygon += (pnt -> new ArrayBuffer[Polygon]())
      }

      if(!polygon.point_polygon.contains(pnt)){
        polygon.point_polygon += (pnt -> new ArrayBuffer[Polygon]())
      }

      // To know which polygon we need to turn when looking for holes.
      point_polygon.put(pnt, point_polygon(pnt) ++ List(polygon))
      polygon.point_polygon.put(pnt, polygon.point_polygon(pnt) ++ List(this))

    })
  }

  def resetHalfEdges(): Unit = halfEdges = new mutable.HashMap[Point, ArrayBuffer[HalfEdge]]()
  def getHalfEdge(point: Point, isInterior: Boolean): HalfEdge = {
    if(isInterior) this.halfEdges(point).filter(_.isInterior)(0)
    else this.halfEdges(point).filter(!_.isInterior)(0)
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

        if (minX <= pnt.x && pnt.x <= maxX && minY <= pnt.y && pnt.y <= maxY) {

          nearestPoints += pntA
          nearestPoints += pntB
        }
      }
    }

    nearestPoints.toList
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




