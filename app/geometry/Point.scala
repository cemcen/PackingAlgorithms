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

  override def toString: String = "( " + x + "," + y + ")"
  def canEqual(a: Any): Boolean = a.isInstanceOf[Point]
  override def equals(that: Any): Boolean = that match {
    case that: Point => that.canEqual(this) && this.hashCode == that.hashCode
    case _ => false
  }
  override def hashCode: Int = {
    val prime = 31
    var result = 1
    result = prime * result + x.hashCode
    result = prime * result + y.hashCode
    result
  }
}
