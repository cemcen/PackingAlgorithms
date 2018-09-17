package geometric_test

import dto.dim2D.input.InputPolygon
import geometry.PolygonFactory
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PolygonFactoryTest extends FlatSpec with Matchers {


  "A PolygonFactory" should "creates a properly polygon" in {
    val pentagon = PolygonFactory.createNewPolygon(1.0, 5, "pentagon")

    pentagon.points.size should be (5)
    pentagon.ccw() should be (true)
  }

  it should "create a list of polygon with given specifications" in {
    val polygonList = PolygonFactory.createPolygonArrayInsertion(List(InputPolygon("hexagono", 6, 10, 1.0)))

    polygonList.size should be (10)

    polygonList.foreach( poly => {
      poly.points.size should be (6)
    })
  }


}
