package network

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import geometry.{Vector, Point}

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
    * This algorithm looks for the route that covers a hole beginning from the exterior of one of the edges of the packing polygon.
    */
  def lookForShortestRoute(nodeA: Node, nodeB: Node): ArrayBuffer[Node] = {
    var currentNode: Node = nodeA
    var nextNode: Node = nodeB
    var routeList: ArrayBuffer[Node] = new ArrayBuffer[Node]()
    routeList += currentNode

    while(!nextNode.equals(currentNode)){

      // Change currentNode
      val auxNode = nextNode

      // Look for next node
      nextNode = lookForMinimumAngle(currentNode, nextNode, links(nextNode))

      // Add current node of this iteration to the route List.
      currentNode = auxNode
      routeList += currentNode
    }

    routeList
  }

  /**
    * Looks for the node next to the end node given with minimum angle between them.
    * https://stackoverflow.com/questions/14066933/direct-way-of-computing-clockwise-angle-between-2-vectors
    */
  def lookForMinimumAngle(nodeIni: Node, nodeEnd: Node, possibleNextNodes: ArrayBuffer[Node]): Node = {
    var chosenNode: Node = possibleNextNodes.head
    var minimumAngle: Double = Double.MaxValue
    val compareVector: Vector =  new Vector(nodeEnd.value.asInstanceOf[Point], nodeIni.value.asInstanceOf[Point])

    possibleNextNodes.foreach(node => {
      val maybeVector: Vector =  new Vector(nodeEnd.value.asInstanceOf[Point], node.value.asInstanceOf[Point])
      var angle: Double = Math.atan2(maybeVector * compareVector, maybeVector.determinant(compareVector))

      // We order the angles counterclockwise.
      // TODO: check if the signs are correct.
      if (angle > 0) angle = 360 - angle
      else if (angle < 0) angle = Math.abs(angle)

      if (angle < minimumAngle) {
        minimumAngle = angle
        chosenNode = node
      }
    })
    chosenNode
  }

}
