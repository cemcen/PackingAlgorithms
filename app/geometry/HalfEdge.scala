package geometry

/**
  * Class representing the half edge structure.
  *
  * pointA is the initial point and pointB the ending point of this edge.
  *
  * For optimization purposes we will store the previous edge.
  */
class HalfEdge(var pointA: Point, var pointB: Point, interior: Boolean, polygon: Polygon) {

  private var previousHalfEdge: HalfEdge = _
  private var nextHalfEdge: HalfEdge = _
  private var pairHalfEdge: HalfEdge = _

  def getNextHalfEdge: HalfEdge = nextHalfEdge
  def getPairHalfEdge: HalfEdge = pairHalfEdge
  def getPreviousHalfEdge: HalfEdge = previousHalfEdge
  def getPolygon: Polygon = polygon
  def isInterior: Boolean = interior

  private def setNextHalfEdge(next: HalfEdge): Unit = nextHalfEdge = next
  private def setPairHalfEdge(pair: HalfEdge): Unit = pairHalfEdge = pair
  private def setPreviousHalfEdge(previous: HalfEdge): Unit = previousHalfEdge = previous

  def setPointB(pnt: Point): Unit = pointB = pnt
  def setPointA(pnt: Point): Unit = pointA = pnt

  def pairHalfEdge(pair: HalfEdge): Unit = {
    pair.setPairHalfEdge(this)
    pairHalfEdge = pair
  }

  def nextHalfEdge(next: HalfEdge): Unit = {
    next.setPreviousHalfEdge(this)
    nextHalfEdge = next
  }

  def previousHalfEdge(previous: HalfEdge): Unit = {
    previous.setNextHalfEdge(this)
    previousHalfEdge = previous
  }
}