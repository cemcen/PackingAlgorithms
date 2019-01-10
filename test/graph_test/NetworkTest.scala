package graph_test

import algorithms.geometric.Container2D
import geometry.{Point, Polygon}
import network.{Graph, Node}
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NetworkTest extends FlatSpec with Matchers {

  "A Network" should "know how to add a container" in {
    val graph: Graph = new Graph()
    val container: Container2D = Container2D(100, 50)

    graph.addContainer(container)

    graph.containsNode(Node(new Point(0, 0))) should be (true)
    graph.containsNode(Node(new Point(0, 50))) should be (true)
    graph.containsNode(Node(new Point(100, 50))) should be (true)
    graph.containsNode(Node(new Point(100, 0))) should be (true)
  }

  it should "pass test 1" in {

    var graph: Graph = new Graph()
    val container: Container2D = Container2D(100, 50)
    val polygonA: Polygon = new Polygon(List(new Point(0, 5.837241792),
      new Point(9.2816174376, 0),
      new Point(17.5343902768, 4.6366721235),
      new Point(18.0580172592, 14.4290245643),
      new Point(10.0211926458, 19.9550727638),
      new Point(0.3858565724, 14.9157680912)
    ))

    val polygonB: Polygon = new Polygon(List(new Point(17.5949808957, 5.7697777783),
      new Point(25.7220092518, 0),
      new Point(34.7420753178, 4.0414899689),
      new Point(35.8663701582, 13.8992058434),
      new Point(27.6804938753, 19.9038196867),
      new Point(19.2417229201, 16.640877315)
    ))

    graph.addContainer(container)
    graph = graph.addPolygon1Intersection(polygonA, container.getPolygon)
    graph = graph.addPolygon2Intersections(polygonB, container.getPolygon, polygonA)

    val areaHole: Double = graph.calculateHoleArea(polygonB, container.getPolygon)
    areaHole should === (42.8935 +- 1e-4)
  }

}