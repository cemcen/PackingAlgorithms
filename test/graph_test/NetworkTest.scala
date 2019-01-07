package graph_test

import network.{Graph, Node}
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NetworkTest extends FlatSpec with Matchers {

  "A Network" should "have nodes and egdes" in {
    val graph: Graph = new Graph()
    val nodeA: Node = Node("NodeA")
    val nodeB: Node = Node("NodeB")

    graph.addNode(nodeA)
    graph.addNode(nodeB)

    graph.addLink(nodeA, nodeB)
  }

}