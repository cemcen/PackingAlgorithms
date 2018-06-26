package geometry

/**
  * Contains all definitions needed for points (Addition, multiplication, etc).
  * This points are three dimensional.
  */
class Point(var x: Double = 0, var y: Double = 0, var z: Double = 0) {

  /**
    * Addition between two points.
    */
  def +(that: Point): Point = {
    new Point(this.x + that.x, this.y + that.y, this.z + that.z)
  }

  /**
    * Subtraction between two points.
    */
  def -(that: Point): Point = {
    new Point(this.x - that.x, this.y - that.y, this.z - that.z)
  }

  /**
    * Multiplies a point with a scalar
    */
  def *(that: Double): Point = {
    new Point(this.x * that, this.y * that, this.z * that)
  }
}
