package geometry

/**
  * Class representing the half edge structure.
  *
  * pointA is the initial point and pointB the ending point of this edge.
  *
  * For optimization purposes we will store the previous edge.
  */
class HalfEdge(pointA: Point, pointB: Point, interior: Boolean) {

  private var previousHalfEdge: HalfEdge = _
  private var nextHalfEdge: HalfEdge = _
  private var pairHalfEdge: HalfEdge = _


  def getNextHalfEdge: HalfEdge = nextHalfEdge
  def getPairHalfEdge: HalfEdge = pairHalfEdge
  def getPreviousHalfEdge: HalfEdge = previousHalfEdge
  def isInterior: Boolean = interior

  def pairHalfEdge(pair: HalfEdge): Unit = {
    pair.setPairHalfEdge(this)
    pairHalfEdge = pair
  }

  def setPairHalfEdge(pair: HalfEdge): Unit = pairHalfEdge = pair

  def nextHalfEdge(next: HalfEdge): Unit = {
    next.setPreviousHalfEdge(this)
    nextHalfEdge = next
  }

  def setNextHalfEdge(next: HalfEdge): Unit = nextHalfEdge = next

  def previousHalfEdge(previous: HalfEdge): Unit = {
    previous.setNextHalfEdge(this)
    previousHalfEdge = previous
  }

  def setPreviousHalfEdge(previous: HalfEdge): Unit = previousHalfEdge = previous
}