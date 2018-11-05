package network

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class Graph(private val nodes: ArrayBuffer[Node], private val links: mutable.HashMap[Node, ArrayBuffer[Node]]) {

  def this() = this(new ArrayBuffer[Node](), new mutable.HashMap[Node, ArrayBuffer[Node]]())

  def addNode(node: Node): Unit = {
    if (!nodes.contains(node)) {
      nodes += node
      if (!links.contains(node)) {
        links += ((node, new mutable.ArrayBuffer[Node]()))
      }
    }
  }

  def addLink(nodeA: Node, nodeB: Node): Unit = {
    addNode(nodeA)
    addNode(nodeB)

    links(nodeA) += nodeB
  }

  def changeLinks(nodeA: Node, nodeB: Node, nodeC: Node): Graph = {
    val newGraph: Graph = new Graph(nodes, links)

    newGraph.links(nodeA) -= nodeC
    newGraph.links(nodeA) += nodeB

    newGraph.addNode(nodeB)
    newGraph.links(nodeB) += nodeC
    newGraph
  }

  /**
    * TODO Do algorithm
    */
  def lookForShortestRoute(nodeA: Node, nextNode: Node): ArrayBuffer[Node] = {
    var currentNode: Node = nodeA
    var routeList: ArrayBuffer[Node] = new ArrayBuffer[Node]()
    routeList += currentNode

    routeList
  }

  /**
    * Should look for the minimum angle
    */
  def lookForMinimumAngle(nodeIni: Node, nodeEnd: Node, possibleNextNodes: ArrayBuffer[Node]): Node = {
    val chosenNode: Node = possibleNextNodes.head
    chosenNode
  }

}
