package geometry

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

}

object Vector {

  def apply(pointA: Point, pointB: Point): Vector = {
    val vector = new Vector(pointA, pointB)
    vector.scalarComponents()
    vector
  }

}
