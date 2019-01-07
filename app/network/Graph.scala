package network

import algorithms.geometric.Container2D

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import geometry.{Point, Polygon, Vector}

class Graph(private val nodes: mutable.HashMap[Point, Node], private val links: mutable.HashMap[Node, ArrayBuffer[Node]]) {

  def this() = this(new mutable.HashMap[Point, Node](), new mutable.HashMap[Node, ArrayBuffer[Node]]())

  def addIntersectingPolygonOneIntersection(polygon: Polygon, interPolygon: Polygon): Unit = {

    var newNodesList: ArrayBuffer[Node] = new ArrayBuffer[Node]()
    var exists: ArrayBuffer[Boolean] = new ArrayBuffer[Boolean]()
    val polygonPoints: List[Point] = polygon.points

    polygonPoints.foreach(pnt => {
      if(nodes.contains(pnt)) {
        newNodesList += nodes(pnt)
        exists += true
      } else {
        newNodesList += Node(pnt)
        exists += false
      }
    })

    polygonPoints.indices.foreach(i => {

      // El nodo ya estaba en el grafo
      if(!exists(i)) {

        // Both of these cases we need to insert a new node on the graph
        addNode(polygonPoints(i), newNodesList(i))

        // We need to check if the new node is between some vertex of the intersected polygon.
        val nearestPoints: ArrayBuffer[Point] = interPolygon.getCollinear(polygonPoints(i))

        if(nearestPoints.nonEmpty) {
          // Fixed links with
          changeLinks(nodes(nearestPoints.head), nodes(polygonPoints(i)), nodes(nearestPoints.tail.head))
        }
      }

      // Finally add the edge representing the connexion with the next vertex of the polygon.
      addLink(nodes(polygonPoints(i)), nodes(polygonPoints(Math.floorMod(i + 1, polygonPoints.length))))
    })
  }

  def addIntersectingPolygonTwoIntersections(polygon: Polygon, interPolygon1: Polygon, interPolygon2: Polygon): Unit = {

    var newNodesList: ArrayBuffer[Node] = new ArrayBuffer[Node]()
    var exists: ArrayBuffer[Boolean] = new ArrayBuffer[Boolean]()
    val polygonPoints: List[Point] = polygon.points

    polygonPoints.foreach(pnt => {
      if(nodes.contains(pnt)) {
        newNodesList += nodes(pnt)
        exists += true
      } else {
        newNodesList += Node(pnt)
        exists += false
      }
    })

    polygonPoints.indices.foreach(i => {

      // El nodo ya estaba en el grafo
      if(!exists(i)) {

        // Both of these cases we need to insert a new node on the graph
        addNode(polygonPoints(i), newNodesList(i))

        // We need to check if the new node is between some vertex of the intersected polygon.
        val nearestPoints1: ArrayBuffer[Point] = interPolygon1.getCollinear(polygonPoints(i))
        val nearestPoints2: ArrayBuffer[Point] = interPolygon2.getCollinear(polygonPoints(i))

        if(nearestPoints1.nonEmpty) {
          // Fixed links with
          changeLinks(nodes(nearestPoints1.head), nodes(polygonPoints(i)), nodes(nearestPoints1.tail.head))
        } else if( nearestPoints2.nonEmpty ) {
          // Fixed links with
          changeLinks(nodes(nearestPoints2.head), nodes(polygonPoints(i)), nodes(nearestPoints2.tail.head))
        }
      }

      // Finally add the edge representing the connexion with the next vertex of the polygon.
      addLink(nodes(polygonPoints(i)), nodes(polygonPoints(Math.floorMod(i + 1, polygonPoints.length))))
    })
  }

  def addContainer(container: Container2D): Unit = {

    val points: List[Point] = container.getPolygon.points
    points.indices.foreach(i => {
      addNode(points(i), Node(points(i)))
    })

    points.indices.foreach(i => {
      addLink(nodes(points(i)), nodes(points(Math.floorMod(i + 1, points.length))))
      addLink(nodes(points(Math.floorMod(i + 1, points.length))), nodes(points(i)))
    })
  }

  def addNode(point: Point, node: Node): Unit = {
    if (!nodes.contains(point)) {
      nodes += ((point, node))
      if (!links.contains(node)) {
        links += ((node, new mutable.ArrayBuffer[Node]()))
      }
    }
  }

  def addLink(nodeA: Node, nodeB: Node): Unit = {
    addNode(nodeA.value, nodeA)
    addNode(nodeB.value, nodeB)

    links(nodeA) += nodeB
    links(nodeB) += nodeA
  }

  def changeLinks(nodeA: Node, nodeB: Node, nodeC: Node): Graph = {
    val newGraph: Graph = new Graph(nodes, links)

    newGraph.links(nodeA) -= nodeC
    newGraph.links(nodeA) += nodeB
    newGraph.links(nodeC) -= nodeA
    newGraph.links(nodeC) += nodeB

    newGraph.addNode(nodeB.value, nodeB)
    newGraph.links(nodeB) += nodeC
    newGraph.links(nodeB) += nodeA

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
    val compareVector: Vector =  new Vector(nodeEnd.value, nodeIni.value)

    possibleNextNodes.foreach(node => {
      val maybeVector: Vector =  new Vector(nodeEnd.value, node.value)
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
