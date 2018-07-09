package geometric_test

import geometry.{Polygon, Point,  PolygonFactory}
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PolygonTest extends FlatSpec with Matchers {

  "A Polygon" should "have its points oriented ccw" in {
    val square = PolygonFactory.createNewPolygon(10.0 , 4)

    square.ccw() should be (true)
  }

  it should "intersect another polygon" in {
    val square = new Polygon(List(new Point(1,0), new Point(0,1), new Point(-1, 0), new Point(0, -1)))
    val triangle = new Polygon(List(new Point(0,0), new Point(2,2), new Point(-2, 2)))

    val intersection = square.intersectPolygon(triangle)

    intersection.size should be (2)

    intersection.find(p => p.x == 0.5 && p.y == 0.5).size should be (1)
    intersection.find(p => p.x == -0.5 && p.y == 0.5).size should be (1)
  }

  it should "know if a point is inside or outside" in {
    val triangle = new Polygon(List(new Point(0,0), new Point(2,2), new Point(-2, 2)))

    // On the edge
    triangle.pointInsidePolygon(new Point(2,2)) should be (false)

    // Inside
    triangle.pointInsidePolygon(new Point(0,1)) should be (true)

    // Outside
    triangle.pointInsidePolygon(new Point(3,3)) should be (false)
  }

  it should "get the locus given another polygon" in {
    val pentagon = new Polygon(List(new Point(1,1), new Point(3,3), new Point(3, 4), new Point(2, 5), new Point(1,4)))
    val triangle = new Polygon(List(new Point(0,1), new Point(-1,0), new Point(1, 0)))

    var result: Polygon = pentagon.locusPolygon(triangle)
    result.points.size should be (8)

    val hexagon = PolygonFactory.createNewPolygon(1.0, 6)
    val square = PolygonFactory.createNewPolygon(2.0, 4)

    result = hexagon.locusPolygon(square)
    //result.points.size should be (10)
  }
}
