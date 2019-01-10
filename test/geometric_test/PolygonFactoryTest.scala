package geometric_test

import dto.dim2D.input.InputPolygon
import geometry.PolygonFactory
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PolygonFactoryTest extends FlatSpec with Matchers {


  "A PolygonFactory" should "creates a properly polygon" in {
    val pentagon = PolygonFactory.createNewPolygon(1.0, 5, "pentagon", true, 10)

    pentagon.points.size should be (5)
    pentagon.ccw() should be (true)
  }


}
