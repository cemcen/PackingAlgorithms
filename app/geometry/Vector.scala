package geometry

import scala.collection.mutable.ArrayBuffer

import org.scalactic._
import org.scalactic.TripleEquals._
import Tolerance._

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
    * Method that calculates the subtraction between two vectors and returns the vector resultant.
    */
  def -(vector: Vector): Vector = Vector(this.pointA - vector.pointA, this.pointB - vector.pointB)

  /**
    * Method that calculates the addition between two vectors and returns the vector resultant.
    */
  def +(vector: Vector): Vector = Vector(this.pointA + vector.pointA, this.pointB +  vector.pointB)

  /**
    * Method that calculates the cross product between two vectors and returns the magnitude of that vector.
    */
  def x(vector: Vector): Double = i*vector.j - j*vector.i

  /**
    * Calculates the dot product between two vectors.
    */
  def *(vector: Vector): Double = i*vector.i + j*vector.j + k*vector.k

  /**
    * Multiplies the vector.
    */
  def *(mult: Double): Vector = Vector(pointA*mult, pointB*mult)

  /**
    * Returns magnitude of the vector.
    */
  def magnitude(): Double = Math.sqrt(i*i + j*j + k*k)

  /**
    * Returns angle between two vectors.
    */
  def angleBetween(vector: Vector): Double = Math.acos((this * vector) / (this.magnitude() * vector.magnitude()))

  /**
    * Returns determinant between two points.
    * https://en.wikipedia.org/wiki/Determinant
    */
  def determinant(vector: Vector): Double = {
    i*vector.j - j*vector.i
  }

  /**
    * Returns true if the point is at the left of this vector.
    */
  def leftOn(point: Point): Boolean = {
    val vectorPoint: Vector = Vector(this.pointA, point)

    // It is at this vector's left if cross product is negative.
    (this x vectorPoint) < 0
  }

  override def toString: String = "( " + i + "," + j + "," + k + ")"

  /**
    * Returns points of intersection
    */
  def intersectVector(vector: Vector): List[Point] = {
    intersectVectorV2(vector)
    /*var intersectionPoints: ArrayBuffer[Point] = new ArrayBuffer[Point]()

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
      // https://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect

      val vectorR: Vector = Vector(this.pointB, this.pointA)
      val vectorS: Vector = Vector(vector.pointB, vector.pointA)
      val vectorP: Vector = Vector(new Point(0,0), this.pointB)
      val vectorQ: Vector = Vector(new Point(0,0), vector.pointB)

      // r x s
      val valueD: Double = vectorR x vectorS

      // (q - p) x r
      val valueN: Double = (vectorQ - vectorP) x vectorR

//      println(valueD)
//      println(valueN)

      // This means that we have collinear intersection
      if(valueD == 0 && valueN == 0) {
        // Then we need to know if they are disjoint.
        // If not calculate the intersection
        if ((0 <= (vectorQ - vectorP)*vectorR && (vectorQ - vectorP)*vectorR <= vectorR*vectorR)
          || (0 <= (vectorP - vectorQ)*vectorS && (vectorP - vectorQ)*vectorS <= vectorS*vectorS)) {

          val to = ((vectorQ - vectorP) * vectorR) / (vectorR * vectorR)
          val t1 = ((vectorQ + vectorS - vectorP) * vectorR) / (vectorR * vectorR)

          // Collinear and overlap
          if ((0 <= to && to <= 1) || (0 <= t1 && t1 <= 1)) {

            if (Math.abs(vectorAC.magnitude() + vectorAD.magnitude() - vector.magnitude()) < 0.001) {
              intersectionPoints += this.pointA
            } else {
              intersectionPoints += this.pointB
            }

            if (Math.abs(vectorAC.magnitude() + vectorCB.magnitude() - this.magnitude()) < 0.001) {
              intersectionPoints += vector.pointA
            } else {
              intersectionPoints += vector.pointB
            }
          }
        }
      } else {

        // No parallelism
        if(valueD != 0) {

          val t = ((vectorQ - vectorP) x vectorS) / valueD
          val u = valueN / valueD

          // This means that the intersection exists.
          if((0 <= t && t <= 1) && (0 <= u && u <= 1)) {
            intersectionPoints += new Point(this.pointB.x + t * (this.pointA.x - this.pointB.x), this.pointB.y + t * (this.pointA.y - this.pointB.y))
          }
        }
      }
    }

    // Finally return the result.
    intersectionPoints.toList
    */
  }

  /**
    * Returns points of intersection
    */
  def intersectVectorV2(vector: Vector): List[Point] = {
    var intersectionPoints: ArrayBuffer[Point] = new ArrayBuffer[Point]()

    // Rewriting names for easier understanding
    val p: Vector = Vector(new Point(0,0), this.pointA)
    val r: Vector = this
    val q: Vector = Vector(new Point(0,0), vector.pointA)
    val s: Vector = vector

    val rxs = r x s
    val qpxr = (p - q) x r

    if(rxs === 0.0 +- 1e-8 && qpxr === 0.0 +- 1e-8){
      val t1 = (((s - p) + q) * r) / (r * r)
      val t0 = t1 - (s * r) / (r * r)

      if ((t0 >= 0.0 || t0 === 0.0 +- 1e-8) && (t0 <= 1.0 || t0 === 1.0 +- 1e-8)
        || (t1 >= 0.0 || t1 === 0.0 +- 1e-8) && (t1 <= 1.0 || t1 === 1.0 +- 1e-8)) {
        // Collinear overlapping
        val vectorAC: Vector = Vector(this.pointA, vector.pointA)
        val vectorAD: Vector = Vector(this.pointA, vector.pointB)
        val vectorCB: Vector = Vector(vector.pointA, this.pointB)

        if (Math.abs(vectorAC.magnitude() + vectorAD.magnitude() - vector.magnitude()) === 0.0 +- 1e-8) {
          intersectionPoints += this.pointA
        } else {
          intersectionPoints += this.pointB
        }

        if (Math.abs(vectorAC.magnitude() + vectorCB.magnitude() - this.magnitude()) === 0.0 +- 1e-8) {
          intersectionPoints += vector.pointA
        } else {
          intersectionPoints += vector.pointB
        }
      }
    }

    val t = ((q - p) x s) / (r x s)
    val u = ((q - p) x r) / (r x s)

    if (rxs != 0 && (t >= 0.0 || t === 0.0 +- 1e-8) && (t <= 1.0 || t === 1.0 +- 1e-8) &&
      (u >= 0.0 || u === 0.0 +- 1e-8) && (u <= 1.0 || u === 1.0 +- 1e-8)) {
      intersectionPoints += new Point(
        this.pointA.x + t * (this.pointB.x - this.pointA.x),
        this.pointA.y + t * (this.pointB.y - this.pointA.y))
    }

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
