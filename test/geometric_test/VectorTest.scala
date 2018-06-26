package geometric_test

import geometry.{Point, Vector}
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class VectorTest extends FlatSpec with Matchers {

  "A Vector" should "be reassignable" in {
    val vectorA = Vector(new Point(0,0), new Point(1,1))
    vectorA.setPoints(new Point(2,3), new Point(-1,2))

    vectorA.pointA.x should be (2)
    vectorA.pointA.y should be (3)
    vectorA.pointB.x should be (-1)
    vectorA.pointB.y should be (2)
  }

  it should "be able to do a cross product with another vector" in {
    val vectorA = Vector(new Point(0,0), new Point(1,1))
    val vectorB = Vector(new Point(2,3), new Point(-3,2))

    val result = vectorA x vectorB

    result should be (4)
  }

  it should "be able to do dot product with another vector" in {
    val vectorA = Vector(new Point(0,0), new Point(1,1))
    val vectorB = Vector(new Point(2,3), new Point(-3,2))

    val result = vectorA * vectorB

    result should be (-6)
  }

  it should "have magnitude" in {
    val vectorA = Vector(new Point(5,4), new Point(1,1))

    vectorA.magnitude() should be (5)
  }

  it should "calculate the angle between him and another vector" in {
    val vectorA = Vector(new Point(0,0), new Point(1,1))
    val vectorB = Vector(new Point(2,3), new Point(-3,2))

    val angle = vectorA.angleBetween(vectorB)

    angle should be (2.553590050042225687217032302654417456595462153319181467248)
  }

  it should "know which points are on its left" in {
    val vectorA = Vector(new Point(0,0), new Point(1,1))

    val leftOn = vectorA.leftOn(new Point(1, 2))

    leftOn should be (false)
  }

  it should "manage intersection between vectors (intersection normal case)" in {
    val vectorA = Vector(new Point(0,0), new Point(2,0))
    val vectorB = Vector(new Point(1,1), new Point(1,-1))

    val vectorC = Vector(new Point(0,0), new Point(3,3))
    val vectorD = Vector(new Point(3,0), new Point(0,3))

    val resultAB: List[Point] = vectorA.intersectVector(vectorB)
    val resultCD: List[Point] = vectorC.intersectVector(vectorD)

    resultAB.size should be (1)

    resultAB.head.x should be (1)
    resultAB.head.y should be (0)

    resultCD.size should be (1)

    resultCD.head.x should be (1.5)
    resultCD.head.y should be (1.5)
  }

  it should "manage intersection between vectors (no intersection normal case)" in {
    val vectorA = Vector(new Point(0,0), new Point(2,0))
    val vectorB = Vector(new Point(3,1), new Point(3,-1))

    val result: List[Point] = vectorA.intersectVector(vectorB)

    result.size should be (0)
  }

  it should "manage intersection between vectors (collinear at one edge point)" in {
    val vectorA = Vector(new Point(0,0), new Point(2,0))
    val vectorB = Vector(new Point(2,0), new Point(2,2))
    val vectorC = Vector(new Point(0,0), new Point(0,1))

    val result: List[Point] = vectorA.intersectVector(vectorB)
    val result2: List[Point] = vectorA.intersectVector(vectorC)

    result.size should be (1)
    result2.size should be (1)

    result.head.x should be (2)
    result.head.y should be (0)
    result2.head.x should be (0)
    result2.head.y should be (0)
  }

  it should "manage intersection between vectors (collinear)" in {
    val vectorA = Vector(new Point(0,0), new Point(2,2))
    val vectorB = Vector(new Point(1,1), new Point(3,3))

    val result: List[Point] = vectorA.intersectVector(vectorB)

    result.size should be (2)
  }


}
