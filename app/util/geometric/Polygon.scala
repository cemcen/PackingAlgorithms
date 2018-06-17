package util.geometric

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
  def intersectPolygon(polygon: Polygon) = ???


  /**
    * Getter and setter of attribute center.
    */
  def center: Point = _center
  def center_= (x: Int, y: Int): Unit = {
    _center = new Point(x,y)
  }

}

