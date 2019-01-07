package network

import algorithms.geometric.Container2D

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import geometry.{Point, Polygon, Vector}

class Graph(private val nodes: mutable.HashMap[Point, Node], private val links: mutable.HashMap[Node, ArrayBuffer[Node]]) {

  def this() = this(new mutable.HashMap[Point, Node](), new mutable.HashMap[Node, ArrayBuffer[Node]]())

  def addPolygon1Intersection(polygon: Polygon, interPolygon: Polygon): Graph = {

    val newGraph: Graph = new Graph(this.nodes, this.links)
    newGraph.addIntersectingPolygonOneIntersection(polygon, interPolygon)
    newGraph
  }

  def addPolygon2Intersections(polygon: Polygon, interPolygon: Polygon, interPolygon2: Polygon): Graph = {

    val newGraph: Graph = new Graph(this.nodes, this.links)
    newGraph.addIntersectingPolygonTwoIntersections(polygon, interPolygon, interPolygon2)
    newGraph
  }


  private def addIntersectingPolygonOneIntersection(polygon: Polygon, interPolygon: Polygon): Unit = {

    var exists: ArrayBuffer[Boolean] = new ArrayBuffer[Boolean]()
    val polygonPoints: List[Point] = polygon.points

    polygonPoints.foreach(pnt => {
      if(nodes.contains(pnt)) {
        exists += true
      } else {
        addNode(new Point(pnt.x, pnt.y), Node(new Point(pnt.x, pnt.y)))
        exists += false
      }
    })

    polygonPoints.indices.foreach(i => {

      // El nodo ya estaba en el grafo
      if(!exists(i)) {

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

  private def addIntersectingPolygonTwoIntersections(polygon: Polygon, interPolygon1: Polygon, interPolygon2: Polygon): Unit = {

    var exists: ArrayBuffer[Boolean] = new ArrayBuffer[Boolean]()
    val polygonPoints: List[Point] = polygon.points

    polygonPoints.foreach(pnt => {
      if(nodes.contains(pnt)) {
        exists += true
      } else {
        addNode(new Point(pnt.x, pnt.y), Node(new Point(pnt.x, pnt.y)))
        exists += false
      }
    })

    polygonPoints.indices.foreach(i => {

      // El nodo ya estaba en el grafo
      if(!exists(i)) {

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

  private def addNode(point: Point, node: Node): Unit = {
    if (!nodes.contains(point)) {
      nodes += ((point, node))
      if (!links.contains(node)) {
        links += ((node, new mutable.ArrayBuffer[Node]()))
      }
    }
  }

  private def addLink(nodeA: Node, nodeB: Node): Unit = {
    addNode(nodeA.value, nodeA)
    addNode(nodeB.value, nodeB)

    links(nodeA) += nodeB
    links(nodeB) += nodeA
  }

  private def changeLinks(nodeA: Node, nodeB: Node, nodeC: Node): Unit = {

    links(nodeA) -= nodeC
    links(nodeA) += nodeB
    links(nodeC) -= nodeA
    links(nodeC) += nodeB

    addNode(nodeB.value, nodeB)
    links(nodeB) += nodeC
    links(nodeB) += nodeA
  }

  private def getAreaOfRoute(route: ArrayBuffer[Node]): Double = {

    var area: Double = 0

    for (i <- route.indices) {
      val pointA: Point = route(i).value
      val pointB: Point = route((i + 1) % route.length).value

      area += (pointA.x + pointB.x) * (pointA.y - pointB.y)
    }

    math.abs(area * 0.5)
  }

  def calculateHoleArea(polygon: Polygon, polygonIntersected: Polygon): Double = {

    val polygonPoints: List[Point] = polygon.points
    var intersectionPoint: Point = polygon.points.head

    polygonPoints.foreach(pnt => {
      val pointList: ArrayBuffer[Point] = polygonIntersected.getCollinear(pnt)
      if (pointList.nonEmpty) {
        intersectionPoint = pnt
      }
    })

    var pointsNear: List[Point] = List()
    if(polygonIntersected.points.contains(intersectionPoint)){
      pointsNear = polygonIntersected.getNearestPoints(intersectionPoint)
    } else {
      pointsNear = polygonIntersected.getNearestPointsFromPoint(intersectionPoint)
    }

    println("----------------")
    println("Polygon")
    polygon.points.foreach(pnt => {
      println(pnt)
    })
    println("Polygon Inter")
    polygonIntersected.points.foreach(pnt => {
      println(pnt)
    })
    println("inter Point")
    println(intersectionPoint)
    println("previous Point")
    println(pointsNear.head)
    println("next point")
    println(pointsNear.tail.head)
    val route1: ArrayBuffer[Node] = lookForShortestRoute(nodes(pointsNear.head), nodes(intersectionPoint))
    val route2: ArrayBuffer[Node] = lookForShortestRoute(nodes(intersectionPoint), nodes(pointsNear.tail.head))

    math.min(getAreaOfRoute(route1), getAreaOfRoute(route2))
  }

  /**
    * This algorithm looks for the route that covers a hole beginning from the exterior of one of the edges of the packing polygon.
    */
  private def lookForShortestRoute(nodeA: Node, nodeB: Node): ArrayBuffer[Node] = {
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
  private def lookForMinimumAngle(nodeIni: Node, nodeEnd: Node, possibleNextNodes: ArrayBuffer[Node]): Node = {
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
