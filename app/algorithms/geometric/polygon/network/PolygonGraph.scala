package algorithms.geometric.polygon.network

import java.awt.geom.{Ellipse2D, Line2D}
import java.awt.{BasicStroke, Color, Graphics2D}
import java.awt.image.BufferedImage

import geometry.{Point, Polygon}
import network.Node

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class PolygonGraph(private val nodes: mutable.HashMap[Point, PolygonNode], private val links: mutable.HashMap[PolygonNode, ArrayBuffer[PolygonNode]]) {

  val size: (Int, Int) = (550, 550)
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


  def exportPNGGraph(height: Int, width: Int, route: String, filename: String, circle_size: (Double, Double) = (20,20), drawContainer: Boolean = false): Unit = {
    val drawG = this.drawGraph(width,height, circle_size, drawContainer)
    this.exportCanvas(drawG._1, drawG._2, route, filename)
  }

  private def drawGraph(width: Int, height: Int, circle_size: (Double, Double), drawContainer: Boolean = false): (Graphics2D, BufferedImage) = {

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
        if(drawContainer || (!node.value.isContainer && !link._1.value.isContainer)) {
          g.draw(new Line2D.Double(
            (link._1.value.centroid.x / width) * 500 + circle_size._1 / 2,
            ((height - link._1.value.centroid.y) / height) * 500 + circle_size._2 / 2,
            (node.value.centroid.x / width) * 500 + circle_size._1 / 2,
            ((height - node.value.centroid.y) / height) * 500 + circle_size._2 / 2))
        }
      })
    })

    // set color of the nodes
    g.setColor(new Color(66,165,245))

    // Draw the nodes.
    links.toList.foreach(link => {
      if(drawContainer || !link._1.value.isContainer) {
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

    // write image to a file
    javax.imageio.ImageIO.write(canvas, "png", new java.io.File(route + filename))
  }

}
