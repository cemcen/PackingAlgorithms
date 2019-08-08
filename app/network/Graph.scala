package network

import algorithms.geometric.Container2D

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import geometry.{Point, Polygon, Vector}
import org.scalactic._
import org.scalactic.TripleEquals._
import Tolerance._

import util.control.Breaks._
import java.awt.image.BufferedImage
import java.awt.{BasicStroke, Color, Font, Graphics2D}
import java.awt.geom._

import scala.reflect.io.Path

class Graph(private val nodes: mutable.HashMap[Point, Node], private val links: mutable.HashMap[Node, ArrayBuffer[Node]]) {

  val size: (Int, Int) = (550, 550)

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

//    println("***** LOOKING FOR ROUTE ********")
//    println("Node INI: " + nodeA)
//    println("Node END: " + nodeB)

    var currentNode: Node = nodeA
    var nextNode: Node = nodeB
    var routeList: ArrayBuffer[Node] = new ArrayBuffer[Node]()
    routeList += currentNode

    while (routeList.distinct.size == routeList.size) {

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
    * This algorithm looks for the minimum cycle that has the edge marked only once.
    */
  def lookForShortestRouteMarkedRoute(nodeA: Node, nodeB: Node,
                                      edgesMarked: mutable.HashMap[Node, mutable.HashMap[Node, Boolean]]): ArrayBuffer[Node] = {
    var currentNode: Node = nodeA
    var nextNode: Node = nodeB
    var routeList: ArrayBuffer[Node] = new ArrayBuffer[Node]()

    while (routeList.distinct.size == routeList.size) {

      val auxNode = nextNode

      val possibleNextNodes: ArrayBuffer[Node] = links(nextNode).filter(node => !edgesMarked(nextNode)(node))

      val compareVector: Vector = Vector(nextNode.value, currentNode.value)

      if(possibleNextNodes.length == 0){
        return routeList
      }

      if(possibleNextNodes.length == 1) {
        nextNode = possibleNextNodes.head
      } else if(possibleNextNodes.length == 2) {
        possibleNextNodes.foreach(node => {
          val maybeVector: Vector = Vector(nextNode.value, node.value)
          val crossProductSign = compareVector x maybeVector
          if(!(crossProductSign === 0.0 +- 1e-5))
            nextNode = node
        })
      } else {
        var chosenNode: Node = possibleNextNodes.head
        var minimumAngle: Double = Double.MaxValue
        possibleNextNodes.foreach(node => {
          val maybeVector: Vector = Vector(nextNode.value, node.value)
          var calculation: Double = (maybeVector * compareVector) / (maybeVector.magnitude() * compareVector.magnitude())
          if(calculation === -1.0 +- 1e-8) calculation = -1.0
          if(calculation === 1.0 +- 1e-8) calculation = 1.0
          var angle: Double = Math.acos(calculation)
          val crossProductSign = compareVector x maybeVector

          if (angle.isNaN) angle = 0
          // We order the angles counterclockwise.

          if (angle < minimumAngle && (crossProductSign < 0 || crossProductSign === 0.0 +- 1e-8) && !(angle === 0.0 +- 1e-8)) {
            minimumAngle = angle
            chosenNode = node
          }
        })

        nextNode = chosenNode
      }

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

//    println("***** LOOKING FOR ANGLE ********")
//    println("Node INI: " + nodeIni)
//    println("Node END: " + nodeEnd)

    // Only one route possible
    if(possibleNextNodes.length == 2) {
      possibleNextNodes.foreach(node => {
        val maybeVector: Vector = Vector(nodeEnd.value, node.value)
        val crossProductSign = compareVector x maybeVector
//
//        println("Node: " + node)
//        println("CrossProduct: " + crossProductSign)
//        println("********")
        if(!(crossProductSign === 0.0 +- 1e-3))
          return node
      })
    }

    possibleNextNodes.foreach(node => {
      val maybeVector: Vector = Vector(nodeEnd.value, node.value)
      var calculation: Double = (maybeVector * compareVector) / (maybeVector.magnitude() * compareVector.magnitude())
      if(calculation === -1.0 +- 1e-8) calculation = -1.0
      if(calculation === 1.0 +- 1e-8) calculation = 1.0
      var angle: Double = Math.acos(calculation)
      val crossProductSign = compareVector x maybeVector

//      println("Node: " + node)
//      println("Angle: " + angle)
//      println("CrossProduct: " + crossProductSign)
//      println("********")

      if (angle.isNaN) angle = 0
      // We order the angles counterclockwise.

      if (angle < minimumAngle && (crossProductSign < 0 || crossProductSign === 0.0 +- 1e-8) && !(angle === 0.0 +- 1e-8)) {
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

  /**
    * Find all polygons in an undirected graph.
    *
    * The algorithm consists of drawing all minimum ccw cycles that we can find on this undirected graph.
    *
    * In each step we choose an edge that has not been marked (Edges that has not been processed yet).
    * After we selected the edge we check how much degree the end node has.
    * Depending on the degree we execute the following rules:
    *
    *    1. If the degree is 2.
    *        When we have degree equals two we know that it can only be one route in ccw.
    *        So using cross product we verify which route is ccw. Then we follow the route until it completes the cycle.
    *    2. If the degree is greater than 2.
    *        When the degree is greater than 2 we need to find the next node that completes a curve that is in ccw.
    *        Then we follow the route until it completes the cycle.
    *
    * When finished this step we know that not all cycles are formed. The holes in between can be skipped.
    * But we know that every edge must have been processed once.
    *
    * So the edges that only were processed once are the cycles that we have not found.
    * So we reiterate over every edge to find those edges that had been marked only once.
    * When found we follow the ccw route that has edges only marked once and add these cycles to the result.
    *
    * This algorithm is O(E), because we need to check all edges to find all the cycles.
    *
    * Computes 2E (Follow the cycles) + 2E (Check edges marked once) = 4E in total.
    *
    * */
  def getPolygonInGraph(drawRoutes: Boolean = false, route: String = "", width: Int = 0, height: Int = 0,
                        circle_size: (Double, Double) = (20,20)): ArrayBuffer[Polygon] = {

    // Return list
    var polygonList: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()

    if(drawRoutes) {
      val path = Path.string2path(route + "allRoutes/")
      path.jfile.listFiles.foreach(f => {
        f.delete()
      })
    }


    // Initialize structure that will mark the edges that we have already been.
    var edgesMarked: mutable.HashMap[Node, mutable.HashMap[Node, Boolean]] = new mutable.HashMap[Node, mutable.HashMap[Node, Boolean]]()
    links.foreach(link => {
      val nodeA = link._1
      if(!(edgesMarked contains nodeA)) {
        edgesMarked += ((nodeA, new mutable.HashMap[Node, Boolean]()))
      }
      link._2.foreach(nodeB => {
        if(!(edgesMarked(nodeA) contains nodeB)) {
          edgesMarked(nodeA) += ((nodeB, false))
        }
      })
    })

    // Follow the cycles
    nodes.toList.foreach(element => {
      val nodeA: Node = element._2

      if(links(nodeA).length > 2) {
        links(nodeA).foreach(nodeB => {
          if(!edgesMarked(nodeA)(nodeB)) {
            val nextNode = lookForMinimumAngle(nodeA, nodeB, links(nodeB))
            if(!edgesMarked(nodeB)(nextNode)
                  && (Vector.apply(nodeA.value, nodeB.value) x Vector.apply(nodeB.value, nextNode.value)) > 0
                  && !((Vector.apply(nodeA.value, nodeB.value) x Vector.apply(nodeB.value, nextNode.value)) === 0.0 +- 1e-8)) {
//              println("***************************")
//              println("*******  PREVIOUS NODE *********")
//              println("NODE: " + nodeA)
              val nodeList: ArrayBuffer[Node] = lookForShortestRoute(nodeB, nextNode)
              for( i <- nodeList.indices) {
                val node1 = nodeList(i)
                val node2 = nodeList((i + 1) % nodeList.length)
                edgesMarked(node1)(node2) = true
              }
              if (drawRoutes) {
                this.exportPNGRoute(width, height, route,
                  "allRoutes/" + nodeB.value.toString + "__" + nextNode.value.toString + ".png",
                  nodeList, circle_size)
              }
              polygonList += nodeListToPolygon(nodeList)
            }
          }
        })
      } else {
        // Store both points
        val nodeB: Node = links(nodeA).head
        val nodeC: Node = links(nodeA).tail.head

        // Check if BAC or CAB is ccw and the look for the cycle (polygon or hole).
        val vectorBA: Vector = Vector(nodeB.value, nodeA.value)
        val vectorCA: Vector = Vector(nodeC.value, nodeA.value)

        if((vectorBA x vectorCA) < 0) {
          if(!edgesMarked(nodeB)(nodeA)) {
            val nodeList: ArrayBuffer[Node] = lookForShortestRoute(nodeB, nodeA)
            for( i <- nodeList.indices) {
              val node1 = nodeList(i)
              val node2 = nodeList((i + 1) % nodeList.length)
              edgesMarked(node1)(node2) = true
            }
            if (drawRoutes) {
              this.exportPNGRoute(width, height, route,
                "allRoutes/" + nodeB.value.toString + "__" + nodeA.value.toString + ".png",
                nodeList, circle_size)
            }
            polygonList += nodeListToPolygon(nodeList)
          }

        } else if((vectorBA x vectorCA) > 0) {
          if(!edgesMarked(nodeC)(nodeA)) {
            val nodeList: ArrayBuffer[Node] = lookForShortestRoute(nodeC, nodeA)
            for( i <- nodeList.indices) {
              val node1 = nodeList(i)
              val node2 = nodeList((i + 1) % nodeList.length)
              edgesMarked(node1)(node2) = true
            }
            if (drawRoutes) {
              this.exportPNGRoute(width, height, route,
                "allRoutes/" + nodeC.value.toString + "__" + nodeA.value.toString + ".png",
                nodeList, circle_size)
            }
            polygonList += nodeListToPolygon(nodeList)
          }
        }
      }
    })

    // Find edges marked once.
    edgesMarked.foreach(edge => {
      edge._2.foreach(node_bool => {
        if((edgesMarked contains node_bool._1) && (edgesMarked(node_bool._1) contains edge._1)) {
          if (node_bool._2 && !edgesMarked(node_bool._1)(edge._1)) {
            val nodeList: ArrayBuffer[Node] = lookForShortestRouteMarkedRoute(node_bool._1, edge._1, edgesMarked)
            for (i <- nodeList.indices) {
              val node1 = nodeList(i)
              val node2 = nodeList((i + 1) % nodeList.length)
              edgesMarked(node1)(node2) = true
            }
            if (drawRoutes) {
              this.exportPNGRoute(width, height, route,
                "allRoutes/" + node_bool._1.value.toString + "__" + edge._1.value.toString + ".png",
                nodeList, circle_size)
            }
            polygonList += nodeListToPolygon(nodeList)
          } else if (!node_bool._2 && edgesMarked(node_bool._1)(edge._1)) {
            val nodeList: ArrayBuffer[Node] = lookForShortestRouteMarkedRoute(edge._1, node_bool._1, edgesMarked)
            for (i <- nodeList.indices) {
              val node1 = nodeList(i)
              val node2 = nodeList((i + 1) % nodeList.length)
              edgesMarked(node1)(node2) = true
            }
            if (drawRoutes) {
              this.exportPNGRoute(width, height, route,
                "allRoutes/" + edge._1.value.toString + "__" + node_bool._1.value.toString + ".png",
                nodeList, circle_size)
            }
            polygonList += nodeListToPolygon(nodeList)
          }
        }
      })
    })

    if(drawRoutes) {
      this.exportPNGMarkedRoutes(width, height, route,
        "markedEdges.png",
        edgesMarked, circle_size)
    }

    polygonList
  }

  def getNumberOfNodes: Int = nodes.toList.length
  def getNumberOfEdges: Int = {
    var count = 0
    links.toList.foreach(link => {
      count += link._2.length
    })

    count / 2
  }

  def printEdgeList(): Unit = {
    links.toList.foreach(link => {
      println("**************")
      println("Edge List")
      println()
      println("Node: " + link._1.value)
      println()
      link._2.foreach(node => {
        println("Edge: " + node.value)
        println()
      })
    })
  }

  def getNodeByPoint(point: Point): Node = nodes(point)

  def nodeListToPolygon(nodeList: ArrayBuffer[Node]): Polygon = {
    var points: ArrayBuffer[Point] = new ArrayBuffer[Point]()

    nodeList.foreach(node => {
      if(!points.contains(node.value)) {
        points += node.value
      }
    })

    new Polygon(points.toList)
  }

  def exportPNGRoute(height: Int, width: Int, route: String, filename: String,
                     routeList: ArrayBuffer[Node], circle_size: (Double, Double) = (20,20)): Unit = {
    var drawG = this.drawGraph(width,height, circle_size)
    drawG = this.drawRoute(drawG._1, drawG._2, routeList, height, width, drawNumbers = true, circle_size)
    this.exportCanvas(drawG._1, drawG._2,route, filename)
  }

  def exportPNGMarkedRoutes(height: Int, width: Int, route: String, filename: String,
                     edgesMarked: mutable.HashMap[Node, mutable.HashMap[Node, Boolean]], circle_size: (Double, Double) = (20,20)): Unit = {
    var drawG = this.drawGraph(width,height, circle_size)
    drawG = this.drawMarkedEdges(drawG._1, drawG._2, edgesMarked, height, width, circle_size)
    this.exportCanvas(drawG._1, drawG._2, route, filename)

  }

  def exportPNGGraph(height: Int, width: Int, route: String, filename: String, circle_size: (Double, Double) = (20,20)): Unit = {
    val drawG = this.drawGraph(width,height, circle_size)
    this.exportCanvas(drawG._1, drawG._2, route, filename)
  }

  private def drawRoute(g: Graphics2D, canvas: BufferedImage, routeList: ArrayBuffer[Node],
                        height: Int, width: Int, drawNumbers: Boolean = false,
                        circle_size: (Double, Double)): (Graphics2D, BufferedImage) = {


    // Color for the edges (BLACK)
    g.setColor(new Color(0, 0, 0))

    for( i <- 0 until routeList.indices.length - 1) {
      val NodeA = routeList(i)
      val NodeB = routeList((i + 1) % routeList.length)

      g.draw(new Line2D.Double(
        (NodeA.value.x / width) * 500 + circle_size._1 / 2,
        ((height - NodeA.value.y) / height) * 500 + circle_size._2 / 2,
        (NodeB.value.x / width) * 500 + circle_size._1 / 2,
        ((height - NodeB.value.y) / height) * 500 + circle_size._2 / 2))
    }


    for( i <- 0 until routeList.indices.length - 1) {
      val NodeA = routeList(i)
      // set color of the nodes (RED)
      g.setColor(new Color(255,134,124))
      g.fill(new Ellipse2D.Double(
        (NodeA.value.x / width) * 500 ,
        ((height - NodeA.value.y) / height) * 500,
        circle_size._1,
        circle_size._2))

      // black color for fonts.
      if(drawNumbers) {
        g.setColor(new Color(0, 0, 0))
        g.setFont(new Font("Serif", Font.BOLD, 15))
        val fm = g.getFontMetrics()
        val w = fm.stringWidth((i + 1).toString)
        val h = fm.getAscent
        g.drawString(
          (i + 1).toString,
          ((NodeA.value.x / width) * 500 + circle_size._1 / 2 - (w / 2)).toFloat,
          (((height - NodeA.value.y) / height) * 500 + circle_size._2 / 2 + (h / 4)).toFloat)
      }
    }

    (g, canvas)
  }

  private def drawMarkedEdges(g: Graphics2D, canvas: BufferedImage, edgesMarked: mutable.HashMap[Node, mutable.HashMap[Node, Boolean]],
                        height: Int, width: Int, circle_size: (Double, Double)): (Graphics2D, BufferedImage) = {


    // Color for the edges (BLACK)
    g.setColor(new Color(0, 0, 0))

    edgesMarked.foreach(edge => {
      edge._2.foreach(node_bool => {
        if(node_bool._2 && edgesMarked(node_bool._1)(edge._1)) {
          g.setColor(new Color(0, 0, 0)) // black for ok edge
        } else if(node_bool._2 || edgesMarked(node_bool._1)(edge._1)) {
          g.setColor(new Color(255,167,38)) // one sided orange
        } else {
          g.setColor(new Color(239,83,80)) // never visited red
        }

        g.draw(new Line2D.Double(
          (edge._1.value.x / width) * 500 + circle_size._1 / 2,
          ((height - edge._1.value.y) / height) * 500 + circle_size._2 / 2,
          (node_bool._1.value.x / width) * 500 + circle_size._1 / 2,
          ((height - node_bool._1.value.y) / height) * 500 + circle_size._2 / 2))

      })
    })

    // set color of the nodes
    g.setColor(new Color(66,165,245))

    // Draw the nodes.
    links.toList.foreach(link => {
      g.fill(new Ellipse2D.Double(
        (link._1.value.x / width) * 500 ,
        ((height - link._1.value.y) / height) * 500,
        circle_size._1,
        circle_size._2))
    })


    (g, canvas)
  }

  private def drawGraph(width: Int, height: Int, circle_size: (Double, Double)): (Graphics2D, BufferedImage) = {

    // create an image
    val canvas = new BufferedImage(size._1, size._2, BufferedImage.TYPE_INT_RGB)

    // get Graphics2D for the image
    val g = canvas.createGraphics()

    // clear background
    g.setColor(Color.WHITE)
    g.fillRect(0, 0, canvas.getWidth, canvas.getHeight)

    // enable anti-aliased rendering (prettier lines and circles)
    // Comment it out to see what this does!
    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
      java.awt.RenderingHints.VALUE_ANTIALIAS_ON)

    // Draw the nodes.
    g.setStroke(new BasicStroke())  // reset to default
    g.setColor(new Color(0, 0, 0)) // same as Color.BLUE

    links.toList.foreach(link => {
      link._2.toList.foreach(node => {
        g.draw(new Line2D.Double(
          (link._1.value.x / width) * 500 + circle_size._1 / 2,
          ((height - link._1.value.y) / height) * 500 + circle_size._2 / 2,
          (node.value.x / width) * 500 + circle_size._1 / 2,
          ((height - node.value.y) / height) * 500 + circle_size._2 / 2))
      })
    })

    // set color of the nodes
    g.setColor(new Color(66,165,245))

    // Draw the nodes.
    links.toList.foreach(link => {
      g.fill(new Ellipse2D.Double(
        (link._1.value.x / width) * 500 ,
        ((height - link._1.value.y) / height) * 500,
        circle_size._1,
        circle_size._2))
    })


    (g, canvas)
  }

  private def exportCanvas(g: Graphics2D, canvas: BufferedImage, route: String, filename: String): Unit = {

    // done with drawing
    g.dispose()

    // write image to a file
    javax.imageio.ImageIO.write(canvas, "png", new java.io.File(route + filename))
  }
}
