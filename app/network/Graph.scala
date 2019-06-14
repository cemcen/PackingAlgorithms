package network

import algorithms.geometric.Container2D

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import geometry.{Point, Polygon, Vector}

import org.scalactic._
import org.scalactic.TripleEquals._
import Tolerance._

import util.control.Breaks._

class Graph(private val nodes: mutable.HashMap[Point, Node], private val links: mutable.HashMap[Node, ArrayBuffer[Node]]) {

  def this() = this(new mutable.HashMap[Point, Node](), new mutable.HashMap[Node, ArrayBuffer[Node]]())

  def addPolygon1Intersection(polygon: Polygon, interPolygon: Polygon): Graph = {

    val myNodes: mutable.HashMap[Point, Node] = new mutable.HashMap[Point, Node]() ++ this.nodes
    val myLinks: mutable.HashMap[Node, ArrayBuffer[Node]] = new mutable.HashMap[Node, ArrayBuffer[Node]]
    links.keys.foreach(node => {
      val newLinks = new ArrayBuffer[Node]() ++ links(node)
      myLinks += ((node, newLinks))
    })
    val newGraph: Graph = new Graph(myNodes, myLinks)
    newGraph.addIntersectingPolygonOneIntersection(polygon, interPolygon)
    newGraph
  }

  def addPolygon2Intersections(polygon: Polygon, interPolygon: Polygon, interPolygon2: Polygon): Graph = {

    val myNodes: mutable.HashMap[Point, Node] = new mutable.HashMap[Point, Node]() ++ this.nodes
    val myLinks: mutable.HashMap[Node, ArrayBuffer[Node]] = new mutable.HashMap[Node, ArrayBuffer[Node]]
    links.keys.foreach(node => {
      val newLinks = new ArrayBuffer[Node]() ++ links(node)
      myLinks += ((node, newLinks))
    })
    val newGraph: Graph = new Graph(myNodes, myLinks)
    newGraph.addIntersectingPolygonTwoIntersections(polygon, interPolygon, interPolygon2)
    newGraph
  }


  private def addIntersectingPolygonOneIntersection(polygon: Polygon, interPolygon: Polygon): Unit = {

    var exists: ArrayBuffer[Boolean] = new ArrayBuffer[Boolean]()
    val polygonPoints: List[Point] = polygon.points

    polygonPoints.foreach(pnt => {
      if (nodes.contains(pnt)) {
        exists += true
      } else {
        addNode(new Point(pnt.x, pnt.y), Node(new Point(pnt.x, pnt.y)))
        exists += false
      }
    })

    polygonPoints.indices.foreach(i => {

      // El nodo ya estaba en el grafo
      if (!exists(i)) {

        // We need to check if the new node is between some vertex of the intersected polygon.
        val nearestPoints: ArrayBuffer[Point] = interPolygon.getCollinear(polygonPoints(i))

        if (nearestPoints.nonEmpty) {
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
    //println(polygon.points.length)

    polygonPoints.foreach(pnt => {
      if (nodes.contains(pnt)) {
        exists += true
        //println("HasNode")
      } else {
        addNode(new Point(pnt.x, pnt.y), Node(new Point(pnt.x, pnt.y)))
        exists += false
        //println("HasntNode")
      }
    })

    polygonPoints.indices.foreach(i => {

      // El nodo ya estaba en el grafo
      if (!exists(i)) {

        // We need to check if the new node is between some vertex of the intersected polygon.
        val nearestPoints1: ArrayBuffer[Point] = interPolygon1.getCollinear(polygonPoints(i))
        val nearestPoints2: ArrayBuffer[Point] = interPolygon2.getCollinear(polygonPoints(i))

        //println("****** GET COLLINEAR ********")
        //println("POINT: " + polygonPoints(i))
        //println("RESULTS A: " + nearestPoints1.length)
        //println("RESULTS B: " + nearestPoints2.length)

        if (nearestPoints1.nonEmpty) {
          // Fixed links with
          changeLinks(nodes(nearestPoints1.head), nodes(polygonPoints(i)), nodes(nearestPoints1.tail.head))
        } else if (nearestPoints2.nonEmpty) {
          // Fixed links with
          changeLinks(nodes(nearestPoints2.head), nodes(polygonPoints(i)), nodes(nearestPoints2.tail.head))
        }
      }

      // Finally add the edge representing the connexion with the next vertex of the polygon.
      addLink(nodes(polygonPoints(i)), nodes(polygonPoints(Math.floorMod(i + 1, polygonPoints.length))))
    })


    // Check If the others polygons has collinear point to this one
    interPolygon1.points.indices.foreach(i => {

      // We need to check if the new node is between some vertex of the intersected polygon.
      val nearestPoints1: ArrayBuffer[Point] = polygon.getCollinear(interPolygon1.points(i))

      //println("****** GET COLLINEAR ********")
      //println("POINT: " + interPolygon1.points(i))
      //println("RESULTS A: " + nearestPoints1.length)

      if (nearestPoints1.nonEmpty) {
        // Fixed links with
        changeLinks(nodes(nearestPoints1.head), nodes(interPolygon1.points(i)), nodes(nearestPoints1.tail.head))
      }

    })

    interPolygon2.points.indices.foreach(i => {

      // We need to check if the new node is between some vertex of the intersected polygon.
      val nearestPoints1: ArrayBuffer[Point] = polygon.getCollinear(interPolygon2.points(i))

      //println("****** GET COLLINEAR ********")
      //println("POINT: " + interPolygon2.points(i))
      //println("RESULTS A: " + nearestPoints1.length)

      if (nearestPoints1.nonEmpty) {
        // Fixed links with
        changeLinks(nodes(nearestPoints1.head), nodes(interPolygon2.points(i)), nodes(nearestPoints1.tail.head))
      }

    })

  }

  def addContainer(container: Container2D): Unit = {

    val points: List[Point] = container.getPolygon.points
    points.indices.foreach(i => {
      addNode(points(i), Node(points(i)))
    })

    points.indices.foreach(i => {
      addLink(nodes(points(i)), nodes(points(Math.floorMod(i + 1, points.length))))
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

  def getCloserNodeBetweenFrom(nodeA: Node, nodeC: Node, nodeB: Node): Node = {

    var auxNodeA: Node = nodeA

    if (!links(nodeA).contains(nodeC)) {
      var nodeALinks: ArrayBuffer[Node] = links(nodeA)

      breakable {
        while (!nodeALinks.contains(nodeC) && !nodeALinks.contains(nodeB)) {
          nodeALinks.foreach(node => {
            val pointList: List[Point] = Vector(nodeA.value, nodeC.value).intersectVector(Vector(nodeA.value, node.value))
            if (pointList.size > 1) {
              val pointList: List[Point] = Vector(node.value, nodeC.value).intersectVector(Vector(nodeB.value, node.value))
              if (pointList.size == 1 || node.value.distance(nodeA.value) > nodeB.value.distance(nodeA.value)) {
                break
              }
              auxNodeA = node
              nodeALinks = links(node)
            }
          })
        }
      }
    }

    auxNodeA
  }

  private def changeLinks(nodeA: Node, nodeB: Node, nodeC: Node): Unit = {

    //println("********** CHANGE LINKS ***************")
    //println("Node A: " + nodeA)
    //println("Node B: " + nodeB)
    //println("Node C: " + nodeC)

    var auxNodeA: Node = getCloserNodeBetweenFrom(nodeA, nodeC, nodeB)
    var auxNodeC: Node = getCloserNodeBetweenFrom(nodeC, auxNodeA, nodeB)

    //println("Node AUX A: " + auxNodeA)
    //println("Node AUX C: " + auxNodeC)

    links(auxNodeA) -= auxNodeC
    links(auxNodeA) += nodeB
    links(auxNodeC) -= auxNodeA
    links(auxNodeC) += nodeB

    addNode(nodeB.value, nodeB)
    links(nodeB) += auxNodeC
    links(nodeB) += auxNodeA
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
    var intersectionPoint: Point = null

    polygonPoints.foreach(pnt => {
      val pointList: ArrayBuffer[Point] = polygonIntersected.getCollinear(pnt)
      if (pointList.nonEmpty) {
        intersectionPoint = pnt
      }
    })

    if (intersectionPoint == null) {
      polygonIntersected.points.foreach(pnt => {
        val pointList: ArrayBuffer[Point] = polygon.getCollinear(pnt)
        if (pointList.nonEmpty) {
          intersectionPoint = pnt
        }
      })
    }

    var pointsNear: List[Point] = List()
    if (polygonIntersected.points.contains(intersectionPoint)) {
      pointsNear = polygonIntersected.getNearestPoints(intersectionPoint)
    } else {
      pointsNear = polygonIntersected.getNearestPointsFromPoint(intersectionPoint)
    }

    //println("***** FIRST ROUTE *******")
    val route1: ArrayBuffer[Node] = lookForShortestRoute(nodes(pointsNear.head), nodes(intersectionPoint))
    //println("***** SECOND ROUTE *******")
    val route2: ArrayBuffer[Node] = lookForShortestRoute(nodes(intersectionPoint), nodes(pointsNear.tail.head))

    math.min(getAreaOfRoute(route1), getAreaOfRoute(route2))
  }

  /**
    * This algorithm looks for the route that covers a hole beginning from the exterior of one of the edges of the packing polygon.
    */
  def lookForShortestRoute(nodeA: Node, nodeB: Node): ArrayBuffer[Node] = {
    var currentNode: Node = nodeA
    var nextNode: Node = nodeB
    var routeList: ArrayBuffer[Node] = new ArrayBuffer[Node]()
    routeList += currentNode

    while (routeList.distinct.size == routeList.size) {

      //println("---------------")
      //println("Current Node: " + currentNode)
      // Change currentNode
      val auxNode = nextNode

      // Look for next node
      nextNode = lookForMinimumAngle(currentNode, nextNode, links(nextNode))
      //println("Next Node: " + auxNode)
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
    val compareVector: Vector = Vector(nodeEnd.value, nodeIni.value)

    //println("***** LOOKING FOR ANGLE ********")
    //println("Node INI: " + nodeIni)
    //println("Node END: " + nodeEnd)

    // Only one route possible
    if(possibleNextNodes.length == 2) {
      possibleNextNodes.foreach(node => {
        val maybeVector: Vector = Vector(nodeEnd.value, node.value)
        val crossProductSign = compareVector x maybeVector

        //println("Node: " + node)
        //println("CrossProduct: " + crossProductSign)
        //println("********")
        if(!(crossProductSign === 0.0 +- 1e-3))
          return node
      })
    }

    possibleNextNodes.foreach(node => {
      val maybeVector: Vector = Vector(nodeEnd.value, node.value)
      var angle: Double = Math.acos((maybeVector * compareVector) / (maybeVector.magnitude() * compareVector.magnitude()))
      val crossProductSign = compareVector x maybeVector

      //println("Node: " + node)
      //println("Angle: " + angle)
      //println("CrossProduct: " + crossProductSign)
      //println("********")

      if (angle.isNaN) angle = 0
      // We order the angles counterclockwise.

      if (angle < minimumAngle && crossProductSign < 0 && !(angle === 0.0 +- 1e-3)) {
        minimumAngle = angle
        chosenNode = node
      }
    })

    chosenNode
  }

  /** For testing purpose. */

  def containsNode(node: Node): Boolean = {
    nodes.contains(node.value)
  }

  def getPolygonInGraph: ArrayBuffer[Polygon] = {

    //println("Number Of Nodes: " + this.getNumberOfNodes)
    //nodes.toList.foreach(element => {
    //  val nodeA: Node = element._2
    //  println("Node: " + nodeA)
    //  links(nodeA).foreach(println(_))
    //})

    var polygonList: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()
    var edges: mutable.HashMap[Node, ArrayBuffer[Node]] = new mutable.HashMap[Node, ArrayBuffer[Node]]()

    for (node <- nodes) {
      edges += ((node._2, new mutable.ArrayBuffer[Node]()))
    }

    nodes.toList.foreach(element => {
      val nodeA: Node = element._2
      //println("****************")
      //println("Node A: " + nodeA)

      links(nodeA).foreach(nodeB => {
        if (!edges(nodeA).contains(nodeB)) {
          //println("****************")
          //println("Node B: " + nodeB)
          edges(nodeA) += nodeB
          val nodeList: ArrayBuffer[Node] = lookForShortestRoute(nodeA, nodeB).distinct
          for (i <- nodeList.indices) {
            val node1: Node = nodeList(i)
            val node2: Node = nodeList((i + 1) % nodeList.length)
            if (!edges(node1).contains(node2)) {
              edges(node1) += node2
            }
          }

          val polygon: Polygon = new Polygon(nodeList.map(_.value).toList)

          if(polygon.ccw() && links(nodeList(nodeList.length - 1)).contains(nodeList.head)) {
            polygonList += polygon
            println("---------------------")
            polygon.points.foreach(println(_))
            println("IAM CCW")
          }
        }
      })
    })

    polygonList
  }

  def getNumberOfNodes: Int = nodes.toList.length
  def getNodeByPoint(point: Point): Node = nodes(point)
}
