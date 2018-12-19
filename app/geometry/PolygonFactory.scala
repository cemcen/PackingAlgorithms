package geometry

import dto.dim2D.input.InputPolygon

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

/**
  * All methods needed to create a new polygon.
  *
  * The polygons are going to be created by choosing n points in a circle of radius R.
  */
object PolygonFactory {

  /**
    * Creates a new polygon.
    * @param radius size of the circle's radius.
    * @param numberOfVertex number of vertex that the new polygon will have.
    * @return new polygon created.
    */
  def createNewPolygon(radius: Double, numberOfVertex: Int, label: String, randomFigure: Boolean, regularity: Int): Polygon = {

    val r = scala.util.Random
    val vertexList = new ArrayBuffer[Point]()
    var x: Double = 0
    var y: Double = 0
    var condition: Boolean = true

    if (randomFigure) {
      // We iterate on the number of vertex, then create the vertex randomly on a circle with radius R
      // and finally checks if this vertex is repeated on the list of previous generated vertex.
      for (i <- 1 to numberOfVertex) {

        condition = true

        // It probable to finish on the first iteration.
        while (condition) {
          condition = false
          val randomValue = 2 * math.Pi * r.nextFloat()
          x = radius * math.cos(randomValue)
          y = radius * math.sin(randomValue)
          vertexList.foreach(vertex => if (vertex.x == x && vertex.y == y && ((vertex.x - x) * (vertex.x - x) + (vertex.y - y) * (vertex.y - y)) > radius) condition = true)
        }

        vertexList.+=(new Point(x, y))
      }
    } else {
      // We need to make a regular shape polygon with some sort of random.

      // First we randomly choose the angle of the first vertex
      val randomValue = 2 * math.Pi * r.nextFloat()

      for (i <- 1 to numberOfVertex) {
        val randomAngle = ((2 * math.Pi * r.nextFloat()) - math.Pi) / regularity
        x = radius * math.cos(randomValue + randomAngle + ((2 * math.Pi / numberOfVertex) * (i - 1)))
        y = radius * math.sin(randomValue + randomAngle + ((2 * math.Pi / numberOfVertex) * (i - 1)))
        vertexList.+=(new Point(x, y))
      }

    }

    /**
      * Finally we return the polygon result.
      */
    Polygon(vertexList.toList, radius, label)
  }

  /**
    * Receives the data of the polygon to be packed. It should have the number of vertex of the polygon, the number of appearance
    * of that polygon and the radius of the polygon.
    *
    * Then uses the probability of each polygon to create a random organization of the polygons inside the output list that will
    * be the order of the insertion of these polygons in the new mesh that will be created.
    *
    * @param polygonData this list should have the user chosen properties of the mesh.
    * @return a list with the order of the polygons that will be inserted.
    */
  def createPolygonArrayInsertion(polygonData: List[InputPolygon], height: Double, width: Double,
                                  randomFigure: Boolean, regularity: Int) : List[Polygon] = {

    // Output list with the order of the polygons.
    val polygonList = new ArrayBuffer[Polygon]()

    // We need the total area of the container.
    var totalArea: Double = height*width

    // We also need the area that's been covered.
    var coveredArea: Double = 0.0

    // Random seed.
    val r = scala.util.Random

    // Probabilities sum.
    var totalProbability: Double = 0.0

    // First we need to know all the appearance of each polygon.
    // The i value of this array contains the probability of appearance of the ith polygon.
    val appearanceList = new ArrayBuffer[Int]()
    polygonData.foreach(pData => {
      appearanceList.+=(pData.percentage)
      totalProbability += pData.percentage
    })

    // Know we need to choose polygons until we have covered the total area of the container.
    while(totalArea > coveredArea) {
      var randomPolygon: Double =  r.nextFloat() * totalProbability
      var indexOfPolygon = 0
      var localSum = 0

      // In each iteration search which polygon was chosen.
      breakable {
        for (i <- appearanceList.indices) {
          localSum += appearanceList(i)
          // This means we found the polygon and save the index of the polygon.
          if (localSum >= randomPolygon) {
            indexOfPolygon = i
            break
          }
        }
      }

      // Using the data of the polygon chosen we create a new polygon to be inserted.
      val dataChosenPolygon: InputPolygon = polygonData(indexOfPolygon)
      val newPolygon: Polygon = createNewPolygon(dataChosenPolygon.radius, dataChosenPolygon.numberOfVertex,
        dataChosenPolygon.label, randomFigure, regularity)

      // Finally we add the polygon to the list and add the area to the covered area variable.
      polygonList += newPolygon
      coveredArea += newPolygon.getArea
    }

    polygonList.toList
  }

}
