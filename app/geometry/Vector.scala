package geometry

import scala.collection.mutable.ArrayBuffer

/**
  * Class representation for euclidean vectors.
  */
class Vector(var pointA: Point, var pointB: Point) {
  private var i: Double = _
  private var j: Double = _
  private var k: Double = _

  /**
    * Will be used to recycle an object of this class for optimization issues.
    *
    * ReInitializer of this class.
    */
  def setPoints(pointA: Point, pointB: Point): Unit = {
    this.pointA = pointA
    this.pointB = pointB

    scalarComponents()
  }

  /**
    * Saves the scalar components of this vector.
    */
  def scalarComponents(): Unit = {
    i = pointB.x - pointA.x
    j = pointB.y - pointA.y
    k = pointB.z - pointA.z
  }

  /**
    * Method that calculates the cross product between two vectors and returns the magnitude of that vector.
    */
  def x(vector: Vector): Double = i*vector.j - j*vector.i

  /**
    * Calculates the dot product between two vectors.
    */
  def *(vector: Vector): Double = i*vector.i + j*vector.j + k*vector.k

  /**
    * Returns magnitude of the vector.
    */
  def magnitude(): Double = Math.sqrt(i*i + j*j + k*k)

  /**
    * Returns angle between two vectors.
    */
  def angleBetween(vector: Vector): Double = Math.acos((this * vector) / (this.magnitude() * vector.magnitude()))

  /**
    * Returns true if the point is at the left of this vector.
    */
  def leftOn(point: Point): Boolean = {
    val vectorPoint: Vector = Vector(this.pointA, point)

    // It is at this vector's left if cross product is negative.
    (this x vectorPoint) < 0
  }

  /**
    * Returns points of intersection
    */
  def intersectVector(vector: Vector): List[Point] = {
    var intersectionPoints: ArrayBuffer[Point] = new ArrayBuffer[Point]()

    // First things first we need to be sure it can have an intersection.
    // For this we will do cross product of a vector and the two points of the other vector.
    // For example, we have a vector AB and points C and D.
    // We need to be sure that C and D have cross product of different sign, that tell us that C and D are in opposite planes.

    // First with this vector AB compare C and D.
    val vectorAC: Vector = Vector(this.pointA, vector.pointA)
    val vectorAD: Vector = Vector(this.pointA, vector.pointB)

    // The same with vector CD compare A and B.
    val vectorCA: Vector = Vector(vector.pointA, this.pointA)
    val vectorCB: Vector = Vector(vector.pointA, this.pointB)

    if ((this x vectorAC) * (this x vectorAD) <= 0 && (vector x vectorCA) * (vector x vectorCB) <= 0) {
      // There is a chance that the points are collinear and don't intersect.
      if(this.pointA.x == this.pointB.x && vector.pointA.x == this.pointA.x && vector.pointB.x == this.pointA.x) {
        // This means that the vectors are collinear in X.

        // We need to get the intersection points. And there are 6 possibilities.
        if (Math.abs(vector.pointA.y - this.pointA.y) + Math.abs(vector.pointA.y - this.pointB.y) == Math.abs(this.pointA.y - this.pointB.y)
          && Math.abs(vector.pointB.y - this.pointA.y) + Math.abs(vector.pointB.y - this.pointB.y) == Math.abs(this.pointA.y - this.pointB.y)) {
          intersectionPoints += vector.pointA
          intersectionPoints += vector.pointB
        } else if (Math.abs(vector.pointA.y - this.pointA.y) + Math.abs(vector.pointA.y - this.pointB.y) == Math.abs(this.pointA.y - this.pointB.y)
          && Math.abs(vector.pointB.y - this.pointA.y) + Math.abs(vector.pointB.y - this.pointB.y) != Math.abs(this.pointA.y - this.pointB.y)) {

          intersectionPoints += vector.pointA
          if (Math.abs(this.pointA.y - vector.pointA.y) + Math.abs(this.pointA.y - vector.pointB.y) == Math.abs(vector.pointA.y - vector.pointB.y)) {
            intersectionPoints += this.pointA
          } else {
            intersectionPoints += this.pointB
          }

        } else if (Math.abs(vector.pointA.y - this.pointA.y) + Math.abs(vector.pointA.y - this.pointB.y) != Math.abs(this.pointA.y - this.pointB.y)
          && Math.abs(vector.pointB.y - this.pointA.y) + Math.abs(vector.pointB.y - this.pointB.y) == Math.abs(this.pointA.y - this.pointB.y)) {

          intersectionPoints += vector.pointB

          if (Math.abs(this.pointA.y - vector.pointA.y) + Math.abs(this.pointA.y - vector.pointB.y) == Math.abs(vector.pointA.y - vector.pointB.y)) {
            intersectionPoints += this.pointA
          } else {
            intersectionPoints += this.pointB
          }
        } else {
          intersectionPoints += this.pointA
          intersectionPoints += this.pointB
        }
      } else if (this.pointA.y == this.pointB.y && vector.pointA.y == this.pointA.y && vector.pointB.y == this.pointA.y) {
        // This means that the vectors are collinear in Y.
        
        // We need to get the intersection points. And there are 6 possibilities.
        if (Math.abs(vector.pointA.x - this.pointA.x) + Math.abs(vector.pointA.x - this.pointB.x) == Math.abs(this.pointA.x - this.pointB.x)
          && Math.abs(vector.pointB.x - this.pointA.x) + Math.abs(vector.pointB.x - this.pointB.x) == Math.abs(this.pointA.x - this.pointB.x)) {
          intersectionPoints += vector.pointA
          intersectionPoints += vector.pointB
        } else if (Math.abs(vector.pointA.x - this.pointA.x) + Math.abs(vector.pointA.x - this.pointB.x) == Math.abs(this.pointA.x - this.pointB.x)
          && Math.abs(vector.pointB.x - this.pointA.x) + Math.abs(vector.pointB.x - this.pointB.x) != Math.abs(this.pointA.x - this.pointB.x)) {

          intersectionPoints += vector.pointA
          if (Math.abs(this.pointA.x - vector.pointA.x) + Math.abs(this.pointA.x - vector.pointB.x) == Math.abs(vector.pointA.x - vector.pointB.x)) {
            intersectionPoints += this.pointA
          } else {
            intersectionPoints += this.pointB
          }

        } else if (Math.abs(vector.pointA.x - this.pointA.x) + Math.abs(vector.pointA.x - this.pointB.x) != Math.abs(this.pointA.x - this.pointB.x)
          && Math.abs(vector.pointB.x - this.pointA.x) + Math.abs(vector.pointB.x - this.pointB.x) == Math.abs(this.pointA.x - this.pointB.x)) {

          intersectionPoints += vector.pointB

          if (Math.abs(this.pointA.x - vector.pointA.x) + Math.abs(this.pointA.x - vector.pointB.x) == Math.abs(vector.pointA.x - vector.pointB.x)) {
            intersectionPoints += this.pointA
          } else {
            intersectionPoints += this.pointB
          }
        } else {
          intersectionPoints += this.pointA
          intersectionPoints += this.pointB
        }

      } else {
        // Get points of intersections for a general case (No collinear).

        // http://www.cs.swan.ac.uk/~cssimon/line_intersection.html
        val ta: Double = (((vector.pointA.y - vector.pointB.y) * (this.pointA.x - vector.pointA.x)
          + (vector.pointB.x - vector.pointA.x) * (this.pointA.y - vector.pointA.y))
        / ((vector.pointB.x - vector.pointA.x) * (this.pointA.y - this.pointB.y)
          + (this.pointA.x - this.pointB.x) * (vector.pointB.y - vector.pointA.y)))

        intersectionPoints += new Point(this.pointA.x + ta*(this.pointB.x - this.pointB.x), this.pointA.y + ta*(this.pointB.y - this.pointB.y))
      }
    }

    // Finally return the result.
    intersectionPoints.toList
  }
}

object Vector {

  def apply(pointA: Point, pointB: Point): Vector = {
    val vector = new Vector(pointA, pointB)
    vector.scalarComponents()
    vector
  }

}
