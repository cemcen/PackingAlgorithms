package geometry

import scala.collection.mutable.ArrayBuffer

/**
  * This is a class for polygon representation. This polygon will be represented inside a circle of center '_center' and radius
  * 'radius', each point of this polygon will have the center as coordinate system.
  */
class Polygon(points: List[Point], radius: Double) {

  private var _center = new Point(0,0)

  /**
    * Checks if the received polygon intersects with the actual polygon and return the intersection points.
    *
    */
  def intersectPolygon(polygon: Polygon): Boolean = ???

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
    * Getter and setter of attribute center.
    */
  def center: Point = _center
  def center_= (x: Int, y: Int): Unit = {
    _center = new Point(x,y)
  }

}

object Polygon {

  def apply(points: List[Point], radius: Double): Polygon = {

    // Before initializing the polygon we need to order the point ccw.
    // We know that the center of those points is (0,0).
    // We are going to use that reference to order those points.
    val center = new Point(0,0)

    // The first vector will use the first point as reference and then we will order them from that point.
    val vectorCenterPoint: Vector = Vector(center, points.head)

    // This vector will be used to calculate the angle of each point in the polygon.
    val vectorAuxiliar: Vector = Vector(center, points.head)

    // Then we will order by the size of the angle and sign of the cross product between the vectors.
    case class OrderPolygon(index: Int, angle: Double, crossProduct: Double)
    val orderPolygonList: ArrayBuffer[OrderPolygon] = new ArrayBuffer[OrderPolygon]()

    // Retrieve angle between the point previously selected and the point in the iteration.
    // And calculate the cross product between them so we can know the sign of the angle.
    points.zipWithIndex.foreach{
      case (point, i) =>
        vectorAuxiliar.setPoints(center, point)
        orderPolygonList += OrderPolygon(i, vectorCenterPoint.angleBetween(vectorAuxiliar), vectorCenterPoint x vectorAuxiliar)
    }

    // Now we need to order it by size.
    orderPolygonList.sortBy(order => order.angle)

    // We need a new list of points so we can order it ccw.
    val pointsOrderedCCW: ArrayBuffer[Point] = new ArrayBuffer[Point]()

    // Then use the angle order to push or queue an element depending on the cross product sign.
    orderPolygonList.foreach(orderPolygon => {
      if (orderPolygon.index > 0) {
        if (orderPolygon.crossProduct > 0){
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




