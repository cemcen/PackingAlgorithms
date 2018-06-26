package geometric_test

import geometry.Point
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.scalatest._

@RunWith(classOf[JUnitRunner])
class PointTest extends FlatSpec with Matchers {

  "A Point" should "add properly" in {
    val pointA = new Point(1,1)
    val pointB = new Point(2,4)
    val result = pointA + pointB

    result.x should be (3)
    result.y should be (5)
    result.z should be (0)
  }

}