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


  it should "The route of a hole should be correct" in {

    var graph: Graph = new Graph()
    val container: Container2D = Container2D(40, 20)
    graph.addContainer(container)

    val polygonA: Polygon = new Polygon(List(new Point(9.873203747009304,18.87967522412119),
      new Point(18.576790851313625,11.549736213083712),
      new Point(13.439021221975826,0.0),
      new Point(2.4774599942431497,1.2827299365263984),
      new Point(0.0,13.429938314738614)
    ))

    val polygonB: Polygon = new Polygon(List(new Point(26.480190456396482,0.0),
      new Point(18.676128438043282,4.477967963175011),
      new Point(18.524729770117293,15.255925513434633),
      new Point(26.045250511046333,19.937347247568322),
      new Point(35.629424398448144,15.077304152183153),
      new Point(35.38248097088226,4.497220715570403)
    ))

    graph = graph.addPolygon1Intersection(polygonA, container.getPolygon)
    graph = graph.addPolygon2Intersections(polygonB, container.getPolygon, polygonA)

    val route = graph.lookForShortestRoute(
      graph.getNodeByPoint(new Point(0.0,13.429938314738614)),
      graph.getNodeByPoint(new Point(9.873203747009304,18.87967522412119))
    )

    val route2 = graph.lookForShortestRoute(
      graph.getNodeByPoint(new Point(9.873203747009304,18.87967522412119)),
      graph.getNodeByPoint(new Point(0.0,13.429938314738614))
    )

    route.distinct.length should === (11)
    route2.distinct.length should === (5)

    graph.getPolygonInGraph()
  }


  it should "it should find the correct holes" in {

    var graph: Graph = new Graph()
    val container: Container2D = Container2D(50, 50)
    graph.addContainer(container)

    val polygonA: Polygon = new Polygon(List(new Point(25.009771483112065,2.998712319330295),
      new Point(29.680474897306794,16.256907321448658),
      new Point(20.122892114600255,28.072282969822055),
      new Point(4.947623285251153,25.300816713567258),
      new Point(0.0,11.890919459601822),
      new Point(9.542436253995271,0.0)
    ))

    val polygonB: Polygon = new Polygon(List(new Point(19.38181701665519,39.0844692643667),
      new Point(7.8365197894500085,46.12946215677053),
      new Point(3.552713678800501E-15,34.19507813253752),
      new Point(12.302696434326421,26.644077071863144)
    ))

    val polygonC: Polygon = new Polygon(List(new Point(27.356295257807812,9.659522699084789),
      new Point(38.05488497660717,0.0),
      new Point(47.349850599115264,9.804331423452119),
      new Point(37.86227699705536,19.962155611049738)
    ))

    val polygonD: Polygon = new Polygon(List(new Point(50.00000000000001,33.88201640308741),
      new Point(35.71064651419611,33.721694626521845),
      new Point(35.49094167746731,20.1274735760122),
      new Point(49.37799313948949,19.15933589085798)
    ))

    val polygonE: Polygon = new Polygon(List(new Point(35.04618569254171,47.90867216691592),
      new Point(21.012190873257257,49.999999999999986),
      new Point(18.930703370051607,36.06421421376885),
      new Point(32.54550964970839,33.66861394420066)
    ))

    graph = graph.addPolygon1Intersection(polygonA, container.getPolygon)
    graph = graph.addPolygon2Intersections(polygonB, container.getPolygon, polygonA)
    graph = graph.addPolygon2Intersections(polygonC, container.getPolygon, polygonA)
    graph = graph.addPolygon2Intersections(polygonD, container.getPolygon, polygonC)
    graph = graph.addPolygon2Intersections(polygonE, container.getPolygon, polygonB)

    graph.exportPNGGraph(50, 50, "debug/test/graph_test/test_4/", "graph.png")

    val route = graph.lookForShortestRoute(
      graph.getNodeByPoint(new Point(47.349850599115264,9.804331423452119)),
      graph.getNodeByPoint(new Point(38.05488497660717,0.0))
    )

    graph.exportPNGRoute(50, 50, "debug/test/graph_test/test_4/", "route1.png", route)

    val route2 = graph.lookForShortestRoute(
      graph.getNodeByPoint(new Point(18.930703370051607,36.06421421376885)),
      graph.getNodeByPoint(new Point(32.54550964970839,33.66861394420066))
    )

    val route3 = graph.lookForShortestRoute(
      graph.getNodeByPoint(new Point(35.71064651419611,33.721694626521845)),
      graph.getNodeByPoint(new Point(35.49094167746731,20.1274735760122))
    )

    val route4 = graph.lookForShortestRoute(
      graph.getNodeByPoint(new Point(37.86227699705536,19.962155611049738)),
      graph.getNodeByPoint(new Point(49.37799313948949,19.15933589085798))
    )

    route.distinct.length should === (6)
    route2.length should === (6)
    route3.length should === (6)
    route4.length should === (6)

    graph.getNumberOfEdges should === (36)
    graph.getNumberOfNodes should === (26)

    graph.getPolygonInGraph(drawRoutes = true, "debug/test/graph_test/test_4/allRoutes/", 50, 50)
  }
}