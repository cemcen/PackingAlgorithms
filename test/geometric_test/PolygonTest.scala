package geometric_test

import algorithms.geometric.LocusAlgorithm
import geometry.{Point, Polygon, PolygonFactory}
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
    val pentagon = new Polygon(List(new Point(1, 1), new Point(3, 3), new Point(3, 4), new Point(2, 5), new Point(1, 4)))
    val triangle = new Polygon(List(new Point(0, 1), new Point(-1, 0), new Point(1, 0)))

    var result: Polygon = LocusAlgorithm.getLocusOfTwoPolygons(pentagon, triangle)
    result.points.size should be(8)
    //result.points.foreach(println(_))

    val bigSquare: Polygon = new Polygon(List(new Point(2, 2), new Point(4, 2), new Point(4, 4), new Point(2, 4)))
    val smallSquare: Polygon = new Polygon(List(new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(0, 1)))
    result = LocusAlgorithm.getLocusOfTwoPolygons(bigSquare, smallSquare)
    result.points.size should be(8)
    //result.points.foreach(println(_))

    // No me acuerdo xD
    var hexagon: Polygon = new Polygon(List(new Point(9.93765565212524, -1.1148991612622419), new Point(9.939817848340752, 1.095454764838148), new Point(-3.868173869752697, 9.221563366011342), new Point(-5.524071668673823, 8.335744249876864), new Point(-9.889173168826671, 1.484673040422921), new Point(-9.877958106397436, -1.5575441079652195)))
    var square: Polygon = new Polygon(List(new Point(7.467136283764906, -6.651456661490145), new Point(6.311206450111875, 7.756846855782721), new Point(-0.19271687579717342, 9.998142837836584), new Point(-3.4814766277410074, 9.374397073438542)))
    result = LocusAlgorithm.getLocusOfTwoPolygons(hexagon, square)
    //result.points.foreach(println(_))
    result.points.size should be(10)

    // Caso en el que queda en el interior arista estatica
    hexagon = new Polygon(List(new Point(-5.36091610733671, -8.441598100484162), new Point(1.214984949944071, -9.925916157786615), new Point(2.9223545625618335, -9.563464006868747), new Point(9.70601928893884, -2.406904560373591), new Point(9.94831527300682, 1.0153931400493352), new Point(9.093240309542178, 4.160887005545485)))
    square = new Polygon(List(new Point(3.8128238766380114, 9.244586204138024), new Point(-9.580375071082429, -2.866428700906813), new Point(8.599585034526674, -5.103639606589091), new Point(9.999850085714826, 0.05475639897944287)))
    result = LocusAlgorithm.getLocusOfTwoPolygons(hexagon, square)
    // result.points.foreach(println(_))
    result.points.size should be(10)

    // Caso en que se puede poner todos los vertices de un polygono en un solo vertice del estatico
    hexagon = new Polygon(List(new Point(9.999999917495394, -0.001284559103509554), new Point(5.90811371731029, 8.068097192233742), new Point(5.551049276086123, 8.317803311840436), new Point(2.7990707441108755, 9.600270984168237), new Point(-7.8743014211849705, 6.1640390271577905), new Point(-8.711555819032293, 4.910070794997217)))
    square = new Polygon(List(new Point(2.735710292090129, 9.618518035422719), new Point(0.7440414808717205, 9.97228169852528), new Point(-4.451479517162996, 8.954570347497322), new Point(-7.671441971710343, -6.414746922106977)))
    result = LocusAlgorithm.getLocusOfTwoPolygons(hexagon, square)
    // result.points.foreach(println(_))
    result.points.size should be(10)

    // Arista del polygono que se mueve entre aristas estaticas
    hexagon =  new Polygon(List(new Point(-2.4728522289276755,9.68942732331934), new Point(-8.720888766873323,4.893475157372592), new Point(2.4657936379757524,-9.691226018152621), new Point(5.354518980659221,-8.445657255996133), new Point(8.040348784406463,-5.945821341504763), new Point( 3.0377267743922873,9.52744541018946)))
    square =  new Polygon(List(new Point(1.8829448673927776,9.82112613839977), new Point(-0.0013969112109861303,9.999999902431952), new Point(-4.392367503598228,-8.98371347012661), new Point( -2.683084717119965,-9.633330493695173)))
    result = LocusAlgorithm.getLocusOfTwoPolygons(hexagon, square)
    //result.points.foreach(println(_))
    result.points.size should be (10)

    // Todos los vertices pueden tomar solo un vertice del poligono que se mueve.
    hexagon =  new Polygon(List(new Point(-3.967989870588795,-9.179055310156091), new Point(6.850642976630961,-7.284826065647476), new Point(8.96940091488243,-4.421520917976781), new Point(9.804228198853961,-1.9690376900396895), new Point( 9.944340253077241,-1.0536113757110295), new Point( 9.512026681203626,3.0856682284507437)))
    square =  new Polygon(List(new Point(-6.281474173523742,-7.780943529377026), new Point( 6.079509743646781,-7.939745668275769), new Point(7.043877409936956,-7.098154058188638), new Point( 8.216692186083343,-5.699646438083414)))
    println("HEXAGONO PRUEBA")
    hexagon.points.foreach(println(_))
    println("CUADRADO PRUEBA")
    square.points.foreach(println(_))
    println("RESULTA3")
    result = LocusAlgorithm.getLocusOfTwoPolygons(hexagon, square)
    result.points.foreach(println(_))
    result.points.size should be (10)
  }

  /*it should "get correct locus of 100 randomize polygons" in {
    var result: Polygon =  PolygonFactory.createNewPolygon(10.0, 6)

    for (_ <- 1 to 100) {
      val Rhexagon = PolygonFactory.createNewPolygon(10.0, 6)
      val Rsquare = PolygonFactory.createNewPolygon(10.0, 4)
      println("HEXAGONO PRUEBA")
      Rhexagon.points.foreach(println(_))
      println("CUADRADO PRUEBA")
      Rsquare.points.foreach(println(_))
      println("RESULTA3")
      result = Rhexagon.locusPolygon(Rsquare)
      result.points.foreach(println(_))
      result.points.size should be (10)
    }
  }*/
}
