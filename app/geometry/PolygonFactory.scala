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
  def createNewPolygon(radius: Double, numberOfVertex: Int): Polygon = {

    val r = scala.util.Random
    val vertexList = new ArrayBuffer[Point]()
    var x: Double = 0
    var y: Double = 0
    var condition: Boolean = true

    // We iterate on the number of vertex, then create the vertex randomly on a circle with radius R
    // and finally checks if this vertex is repeated on the list of previous generated vertex.
    for(i <- 1 to numberOfVertex) {

      condition = true

      // It probable to finish on the first iteration.
      while(condition) {
        condition = false
        val randomValue = 2*math.Pi*r.nextFloat()
        x = radius*math.cos(randomValue)
        y= radius*math.sin(randomValue)
        vertexList.foreach(vertex => if (vertex.x == x && vertex.y == y) condition = true)
      }

      vertexList.+=(new Point(x,y))
    }

    /**
      * Finally we return the polygon result.
      */
    Polygon(vertexList.toList, radius)
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
  def createPolygonArrayInsertion(polygonData: List[InputPolygon]) : List[Polygon] = {

    // Output list with the order of the polygons.
    val polygonList = new ArrayBuffer[Polygon]()

    // We need the total number of polygons.
    var totalNumberOfPolygons = 0

    // Random seed.
    val r = scala.util.Random

    // First we need to know all the appearance of each polygon.
    // The i value of this array contains the number of appearance of polygon on index i of the input list.
    val appearanceList = new ArrayBuffer[Int]()
    polygonData.foreach( pData => {
      appearanceList.+=(pData.percentage)
      totalNumberOfPolygons += pData.percentage
    })

    // The next step is to choose the order of the polygons, so we are going to iterate as long as we have polygons left.
    // In each step we will reduce the total numberOfPolygons in one and the index of the chosen polygon by one also.
    for(x <- 0 until totalNumberOfPolygons ) {
      val chosenPolygon = r.nextInt(totalNumberOfPolygons - x + 1)
      var polygonCurrentAppearances = 0
      var indexOfPolygon = 0

      // In each iteration search which polygon was chosen.
      breakable {
        for (i <- appearanceList.indices) {
          polygonCurrentAppearances += appearanceList(i)
          // This means we found the polygon and save the index of the polygon.
          if (polygonCurrentAppearances >= chosenPolygon) {
            indexOfPolygon = i
            break
          }
        }
      }

      // Using the data of the polygon chosen we create a new polygon to be inserted.
      val dataChosenPolygon: InputPolygon = polygonData(indexOfPolygon)
      val newPolygon = createNewPolygon(dataChosenPolygon.radius, dataChosenPolygon.numberOfVertex)
      // We erase the polygon to be chosen again.
      appearanceList(indexOfPolygon) -= 1

      // Finally we add the polygon to the list.
      polygonList += newPolygon
    }

    polygonList.toList
  }

}
