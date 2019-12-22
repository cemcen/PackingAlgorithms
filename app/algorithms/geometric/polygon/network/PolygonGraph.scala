package algorithms.geometric.polygon.network

import java.awt.geom.{Ellipse2D, Line2D}
import java.awt.{BasicStroke, Color, Graphics2D}
import java.awt.image.BufferedImage
import java.io.File

import algorithms.geometric.Container2D
import experiments.Experiment
import geometry.{Point, Polygon}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.reflect.io.Path

class PolygonGraph(private val nodes: mutable.HashMap[Point, PolygonNode], private val links: mutable.HashMap[PolygonNode, ArrayBuffer[PolygonNode]]) {

  val size: (Int, Int) = (550, 550)
  var step: Int = 1
  var width: Int = 0
  var height: Int = 0

  def this() = this(new mutable.HashMap[Point, PolygonNode](), new mutable.HashMap[PolygonNode, ArrayBuffer[PolygonNode]]())

  def addPolygonLinks(polygon: Polygon, interPolygons: ArrayBuffer[Polygon]): Unit = {
    val node: PolygonNode = PolygonNode(polygon)
    addNode(polygon, node)

    interPolygons.foreach(pol => {
      addLink(node, nodes(pol.centroid))
    })
  }

  def addPolygon(polygon: Polygon): Unit = {
    val node: PolygonNode = PolygonNode(polygon)
    addNode(polygon, node)
  }

  def getPolygon(point: Point): Polygon = {
    nodes(point).value
  }

  def checkNeighbourhood(polygon: Polygon, polygonLessArea: Polygon): Boolean = {
    var completed: Boolean = true
    links(nodes(polygon.centroid)).foreach(pol_node => {
      if(pol_node.value.isHole) {
        completed = completed && polygonLessArea.getArea > pol_node.value.getArea
      }
    })
    completed
  }

  def getNeighbours(polygon: Polygon): ArrayBuffer[Polygon] = {
    val neighbours: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()
    links(nodes(polygon.centroid)).foreach(pol_node => {
      if(!pol_node.value.isHole) neighbours += pol_node.value
    })
    neighbours
  }

  private def delegateLinks(polygon: Polygon, holes: ArrayBuffer[Polygon]): Unit = {
      links(nodes(polygon.centroid)).foreach(p_node => {
        holes.foreach(hole => {
          if(p_node.value.intersectPolygonCuadratic(hole).nonEmpty) {
            addLink(p_node, nodes(hole.centroid))
          }
        })
      })
  }

  private def deletePolygon(polygon: Polygon): Unit = {
    links.foreach(link => {
      link._2 -= nodes(polygon.centroid)
    })

    links -= nodes(polygon.centroid)
    nodes -= polygon.centroid
  }

  def addContainer(container: Container2D): Unit = {

    val path = Path.string2path("debug/packing/")
    val directory: File = new File("debug/packing")
    if (!directory.exists()) {
      directory.mkdir()
    }
    path.jfile.listFiles.foreach(f => {
      f.delete()
    })

    height = container.getHeight.toInt
    width = container.getWidth.toInt
    val polygon: Polygon = container.getPolygon
    addPolygon(polygon)
  }

  def changedHeightContainer(container: Container2D, oldHeight: Double, nHeight: Double): Unit = {
    height = nHeight.toInt
    nodes.foreach(pnt_node => {
      if(pnt_node._2.value.isContainer) {

        val nContainer: PolygonNode = PolygonNode(container.getPolygon)
        val oldContainer: PolygonNode = nodes(pnt_node._1)

        links(oldContainer).foreach(link => {
          addLink(link, nContainer)
        })
        deletePolygon(oldContainer.value)
      }
      if(pnt_node._2.value.isHole) {
        pnt_node._2.value.changePointIfHasPoint(container.getWidth, oldHeight, nHeight)
      }
    })
  }

  def getPolygonInGraph: ArrayBuffer[Polygon] = {
    var polygons: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()
    nodes.foreach(node => {
      if(!node._2.value.isContainer) {
        polygons += node._2.value
      }
    })
    polygons
  }

  private def addNode(polygon: Polygon, node: PolygonNode): Unit = {
    if (!nodes.contains(polygon.centroid)) {
      nodes += ((polygon.centroid, node))
      if (!links.contains(node)) {
        links += ((node, new mutable.ArrayBuffer[PolygonNode]()))
      }
    }
  }

  private def addLink(nodeA: PolygonNode, nodeB: PolygonNode): Unit = {
    addNode(nodeA.value, nodeA)
    addNode(nodeB.value, nodeB)

    links(nodeA) += nodeB
    links(nodeB) += nodeA
  }

  private def getCoveringHole(polygon: Polygon): PolygonNode = {
    var node: PolygonNode = nodes.head._2
    nodes.foreach(pnt_polygonNode => {
      if (pnt_polygonNode._2.value.isHole && pnt_polygonNode._2.value.pointInsidePolygonRayCasting(polygon.centroid)) node = pnt_polygonNode._2
    })
    node
  }

  def getMinimumArea(polygon: Polygon): Double = {
    val holes: ArrayBuffer[Polygon] = getHoles(polygon)
    var area: Double = Double.MaxValue

    holes.foreach(pol => {
      if(pol.getArea < area) area = pol.getArea
    })
    area
  }

  private def getHoles(polygon: Polygon): ArrayBuffer[Polygon] = {

    //if(step < 4) {
    //      println("Step: " + step)
    val holeCovering: PolygonNode = getCoveringHole(polygon)

//    println("Polygon: ")
//    polygon.points.foreach(pnt => println(pnt))
//    println("Covering Hole: ")
//    holeCovering.value.points.foreach(pnt => println(pnt))

    // Intersection points
    val pInterHole: List[Point] = polygon.intersectPolygonCuadratic(holeCovering.value)

//    println("Intersections: ")
//    pInterHole.foreach(pnt => println(pnt))

    var contPoints: ArrayBuffer[Point] = new ArrayBuffer[Point]()
    var contPoints2: ArrayBuffer[Point] = new ArrayBuffer[Point]()

    if (holeCovering.value.points.contains(pInterHole.head)) {
      contPoints += pInterHole.head
      contPoints += pInterHole.head
    } else {
      contPoints ++= holeCovering.value.getNearestPointsFromPoint(pInterHole.head)
    }

    if(pInterHole.length > 1) {
      if (holeCovering.value.points.contains(pInterHole.tail.head)) {
        contPoints2 += pInterHole.tail.head
        contPoints2 += pInterHole.tail.head
      } else {
        contPoints2 ++= holeCovering.value.getNearestPointsFromPoint(pInterHole.tail.head)
      }
    } else {
      if (holeCovering.value.points.contains(pInterHole.head)) {
        contPoints2 += pInterHole.head
        contPoints2 += pInterHole.head
      } else {
        contPoints2 ++= holeCovering.value.getNearestPointsFromPoint(pInterHole.head)
      }
    }

    var polygonContPoints: ArrayBuffer[Point] = new ArrayBuffer[Point]()
    var polygonContPoints2: ArrayBuffer[Point] = new ArrayBuffer[Point]()

    if (polygon.points.contains(pInterHole.head)) {
      polygonContPoints += pInterHole.head
      polygonContPoints += pInterHole.head
    } else {
      polygonContPoints ++= polygon.getNearestPointsFromPoint(pInterHole.head)
    }

    if(pInterHole.length > 1) {
      if (polygon.points.contains(pInterHole.tail.head)) {
        polygonContPoints2 += pInterHole.tail.head
        polygonContPoints2 += pInterHole.tail.head
      } else {
        polygonContPoints2 ++= polygon.getNearestPointsFromPoint(pInterHole.tail.head)
      }
    } else {
      if (polygon.points.contains(pInterHole.head)) {
        polygonContPoints2 += pInterHole.head
        polygonContPoints2 += pInterHole.head
      } else {
        polygonContPoints2 ++= polygon.getNearestPointsFromPoint(pInterHole.head)
      }
    }

    val routeA: ArrayBuffer[Point] = polygon.routeFromTo(polygonContPoints.tail.head, polygonContPoints2.head)
    //      println("Route A: ")
    //      routeA.foreach(pnt => println(pnt))
    val routeB: ArrayBuffer[Point] = polygon.routeFromTo(polygonContPoints2.tail.head, polygonContPoints.head)
    //      println("Route B: ")
    //      routeB.foreach(pnt => println(pnt))

    val holeRouteA: ArrayBuffer[Point] = holeCovering.value.routeFromTo(contPoints.tail.head, contPoints2.head)
    val holeRouteB: ArrayBuffer[Point] = holeCovering.value.routeFromTo(contPoints2.tail.head, contPoints.head)

    //      println("HRoute A: ")
    //      holeRouteA.foreach(pnt => println(pnt))
    //      println("HRoute B: ")
    //      holeRouteB.foreach(pnt => println(pnt))
    //
    val holeLinks: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()

    var h2: Polygon = new Polygon(List())
    var h1: Polygon = new Polygon(List())

    val polygonTest: Polygon = new Polygon((routeA.reverse ++ holeRouteA).toList)
    //      println("Tests")
    //      println(polygonTest.pointInsidePolygonRayCasting(polygon.centroid))
    //      println(!polygonTest.simplePolygon)
    if (polygonTest.pointInsidePolygonRayCasting(polygon.centroid)
      || !polygonTest.simplePolygon) {
      //        println("Opcion 1")
      h1 = new Polygon((routeA.reverse ++ holeRouteB).toList)
      h2 = new Polygon((routeB.reverse ++ holeRouteA).toList)
    } else {
      //        println("Opcion 2")
      h1 = new Polygon((routeA.reverse ++ holeRouteA).toList)
      h2 = new Polygon((routeB.reverse ++ holeRouteB).toList)
    }

    h1.setHole()
    h2.setHole()

    holeLinks += h1
    holeLinks += h2

    holeLinks
  }

  def addPolygonLinksAndUpdateHoles(polygon: Polygon, interPolygons: ArrayBuffer[Polygon]): Unit = {

    val holeLinks: ArrayBuffer[Polygon] = getHoles(polygon)
    val holeCovering: PolygonNode = getCoveringHole(polygon)

    addPolygon(polygon)
    holeLinks.foreach(hole => {
      addPolygon(hole)
    })

    interPolygons.foreach(pol => {
      addLink(nodes(polygon.centroid), nodes(pol.centroid))
    })

    delegateLinks(holeCovering.value, holeLinks)
    holeLinks.foreach(hole => {
      addLink(nodes(hole.centroid), nodes(polygon.centroid))
    })
    deletePolygon(holeCovering.value)

    exportPNGGraph(height, width, route = "debug/packing/", filename = "step_" + step + "polygon_graph.png", circle_size = (10, 10))
    exportPNGGraphPacking(height, width, route = "debug/packing/", filename = "step_" + step + "graph.png", circle_size = (10, 10))
    //}

    step += 1
  }

  def firstPolygonLink(polygon: Polygon, container: Polygon): Unit = {

    val inter: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()
    inter += container
    this.addPolygonLinks(polygon, inter)

    // Intersection points
    val interPoints: List[Point] = polygon.intersectPolygon(container)

    // We need to make two new polygons representing the holes between the container and the recently added polygon.
    // We know that the two intersection points are points from the polygon.
    val contPoints: List[Point] = container.getNearestPointsFromPoint(interPoints.head)
    val contPoints2: List[Point] = container.getNearestPointsFromPoint(interPoints.tail.head)
    val contPoint: Point = contPoints2.filter(p => contPoints.contains(p)).head

    val routeA: ArrayBuffer[Point] = polygon.routeFromTo(interPoints.head, interPoints.tail.head)
    val routeB: ArrayBuffer[Point] = polygon.routeFromTo(interPoints.tail.head, interPoints.head)

    val holeLinks: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()

    var h2: Polygon = new Polygon(List())
    var h1: Polygon = new Polygon(List())
    var c2: Point = new Point()
    var c4: Point = new Point()

    if (contPoints.indexOf(contPoint) == 0) {
      c2 = contPoints.tail.head
      c4 = contPoints2.head

      h2 = new Polygon((routeA.reverse ++ container.routeFromTo(c2, c4)).toList)
      h1 = new Polygon((routeB.reverse :+ contPoint).toList)

    } else {
      c2 = contPoints2.tail.head
      c4 = contPoints.head

      h2 = new Polygon((routeB.reverse ++ container.routeFromTo(c2, c4)).toList)
      h1 = new Polygon((routeA.reverse :+ contPoint).toList)
    }

    h1.setHole()
    h2.setHole()

    holeLinks += h1
    holeLinks += h2

    holeLinks.foreach(hole => {
      addPolygon(hole)
    })
    addPolygonLinks(polygon, holeLinks)
    if(Experiment.EXPORT_STEPS) {
      exportPNGGraph(height, width, route = "debug/packing/", filename = "step_" + step + "polygon_graph.png", circle_size = (10, 10))
      exportPNGGraphPacking(height, width, route = "debug/packing/", filename = "step_" + step + "graph.png", circle_size = (10, 10))
    }
    step += 1
  }


  def exportPNGGraph(height: Int, width: Int, route: String, filename: String, circle_size: (Double, Double) = (20, 20), drawContainer: Boolean = false): Unit = {
    val drawG = this.drawGraphPolygon(width, height, circle_size, drawContainer)
    this.exportCanvas(drawG._1, drawG._2, route, filename)
  }

  def exportPNGGraphPacking(height: Int, width: Int, route: String, filename: String,
                            circle_size: (Double, Double) = (20, 20)): Unit = {
    val drawG = this.drawGraph(width, height, circle_size)
    this.exportCanvas(drawG._1, drawG._2, route, filename)
  }

  def exportPNGGraphPackingCompleted(route: String, filename: String,
                                     circle_size: (Double, Double) = (20, 20),
                                     completedPolygons: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()): Unit = {
    val drawG = this.drawGraph(width, height, circle_size, drawContainer = false, completedPolygons)
    this.exportCanvas(drawG._1, drawG._2, route, "step_" + (step - 1) + "_" + filename)
  }

  private def drawGraph(width: Int, height: Int, circle_size: (Double, Double),
                        drawContainer: Boolean = false,
                        completedPolygons: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()): (Graphics2D, BufferedImage) = {

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

    g.setStroke(new BasicStroke())

    completedPolygons.foreach(pol => {
      if (!pol.isContainer) {
        g.setColor(new Color(229, 115, 115))
        g.fillPolygon(
          pol.points.map(pnt => ((pnt.x / width) * 500 + circle_size._1 / 2).toInt).toArray,
          pol.points.map(pnt => (((height - pnt.y) / height) * 500 + circle_size._2 / 2).toInt).toArray,
          pol.points.length
        )
      }
    })

    links.toList.foreach(link => {
      if (drawContainer || !link._1.value.isContainer) {
        g.setColor(new Color(0, 0, 0))
        for (i <- link._1.value.points.indices) {
          val pntA = link._1.value.points(i)
          val pntB = link._1.value.points((i + 1) % link._1.value.points.length)
          g.draw(new Line2D.Double(
            (pntA.x / width) * 500 + circle_size._1 / 2,
            ((height - pntA.y) / height) * 500 + circle_size._2 / 2,
            (pntB.x / width) * 500 + circle_size._1 / 2,
            ((height - pntB.y) / height) * 500 + circle_size._2 / 2))
        }
      }
    })

    g.setColor(new Color(66, 165, 245))

    // Draw the nodes.
    links.toList.foreach(link => {
      if (drawContainer || !link._1.value.isContainer) {
        for (i <- link._1.value.points.indices) {
          val pnt = link._1.value.points(i)
          g.fill(new Ellipse2D.Double(
            (pnt.x / width) * 500,
            ((height - pnt.y) / height) * 500,
            circle_size._1,
            circle_size._2))
        }
      }
    })


    (g, canvas)
  }

  private def drawGraphPolygon(width: Int, height: Int, circle_size: (Double, Double),
                               drawContainer: Boolean = false): (Graphics2D, BufferedImage) = {

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
    g.setStroke(new BasicStroke()) // reset to default
    g.setColor(new Color(0, 0, 0)) // same as Color.BLUE

    links.toList.foreach(link => {
      link._2.toList.foreach(node => {
        if (drawContainer || (!node.value.isContainer && !link._1.value.isContainer)) {
          g.draw(new Line2D.Double(
            (link._1.value.centroid.x / width) * 500 + circle_size._1 / 2,
            ((height - link._1.value.centroid.y) / height) * 500 + circle_size._2 / 2,
            (node.value.centroid.x / width) * 500 + circle_size._1 / 2,
            ((height - node.value.centroid.y) / height) * 500 + circle_size._2 / 2))
        }
      })
    })

    // set color of the nodes
    g.setColor(new Color(66, 165, 245))

    // Draw the nodes.
    links.toList.foreach(link => {
      if (drawContainer || !link._1.value.isContainer) {
        g.fill(new Ellipse2D.Double(
          (link._1.value.centroid.x / width) * 500,
          ((height - link._1.value.centroid.y) / height) * 500,
          circle_size._1,
          circle_size._2))
      }
    })


    (g, canvas)
  }

  private def exportCanvas(g: Graphics2D, canvas: BufferedImage, route: String, filename: String): Unit = {

    // done with drawing
    g.dispose()

    // If directory does not exists, we create the directory
    val PATH: String = route
    val paths: Array[String] = PATH.split("/")
    var directoryPath: String = ""

    paths.foreach(path => {
      directoryPath += path
      val directory: File = new File(directoryPath)
      if (!directory.exists()) {
        directory.mkdir()
      }
      directoryPath += "/"
    })

    // write image to a file
    javax.imageio.ImageIO.write(canvas, "png", new java.io.File(route + filename))
  }

}
