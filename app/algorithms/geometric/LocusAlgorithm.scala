package algorithms.geometric

import geometry.{Point, Polygon, Vector}

import scala.collection.mutable.ArrayBuffer

class LocusAlgorithm {

  // Points of the polygon need to be stored.
  var pointList: ArrayBuffer[Point] = new ArrayBuffer[Point]()
  var movingPolygonPoints: List[Point] = _
  var staticPolygonPoints: List[Point] = _

  // When one polygon can be placed in only one vertex.
  var numberOfVisited: ArrayBuffer[Int] = new ArrayBuffer[Int]()

  // We will be using two points of each polygon and storing a counter for one the polygon we will be moving on its edge.
  var staticPolygonPoint: Point = _
  var movingPolygonPoint: Point = _

  // Reference on where we are in the array.
  var movingIndexPolygonPoint: Int = 0
  var staticIndexPolygonPoint: Int = 0

  // Translation storing variables.
  var xTranslation: Double = 0
  var yTranslation: Double = 0

  // condition in which we will advance or go back.
  var inside: Boolean = true

  // Variables to be used as store for points.
  var actualPointStatic: Point = _
  var posteriorPointStatic: Point = _
  var previousPointStatic: Point = _
  var actualPointM: Point = _
  var posteriorPointM: Point = _
  var previousPointM: Point = _

  // Store the vectors.
  var posteriorVectorStatic: Vector = _
  var previousVectorStatic: Vector = _
  var posteriorVectorMovable: Vector = _
  var previousVectorMovable: Vector = _

  // Store conditions.
  var firstCondition: Double = 0
  var secondCondition: Double = 0

  def getPointList: ArrayBuffer[Point] = {
    pointList
  }

  def initialize(polygonA: Polygon, polygonB: Polygon): Unit = {

    pointList = new ArrayBuffer[Point]()
    movingPolygonPoints = polygonB.points
    staticPolygonPoints = polygonA.points

    // When one polygon can be placed in only one vertex.
    numberOfVisited = new ArrayBuffer[Int]()
    polygonA.points.foreach(_ => numberOfVisited += 0)

    // We will be using two points of each polygon and storing a counter for one the polygon we will be moving on its edge.
    staticPolygonPoint = polygonA.points.head
    movingPolygonPoint = movingPolygonPoints.head

    // Reference on where we are in the array.
    movingIndexPolygonPoint = 0
    staticIndexPolygonPoint = 0

    // Translation storing variables.
    xTranslation = 0
    yTranslation = 0

    // First of all we search for a point on which the movable polygon isn't inside the static one.
    inside = true

    // Variables to be used as store for points.
    actualPointStatic = null
    posteriorPointStatic = null
    previousPointStatic = null
    actualPointM = null
    posteriorPointM = null
    previousPointM = null

    // Store the vectors.
    posteriorVectorStatic = null
    previousVectorStatic = null
    posteriorVectorMovable = null
    previousVectorMovable = null

    // Store conditions.
    firstCondition = 0
    secondCondition = 0
  }

  def updateInitialPositions(): Unit = {
    while (inside) {
      doStep()
      if (inside) {
        movingIndexPolygonPoint += 1
        movingPolygonPoint = movingPolygonPoints(Math.floorMod(movingIndexPolygonPoint, movingPolygonPoints.size))
      }
    }

//    println("Moving Point: " + movingPolygonPoint)
//    println("Static Point: " + staticPolygonPoint)
  }

  def doStep(): Unit = {
    xTranslation = staticPolygonPoint.x - movingPolygonPoint.x
    yTranslation = staticPolygonPoint.y - movingPolygonPoint.y

    updateActualPoints()
    addTranslation(actualPointM, posteriorPointM, previousPointM)
    updateActualVectors()
    updateConditions()
    checkIntersection(previousVectorStatic, previousVectorMovable, firstCondition, secondCondition)
    subTranslation(actualPointM, posteriorPointM, previousPointM)
  }

  def updateActualPoints(): Unit = {
    actualPointStatic = staticPolygonPoints(Math.floorMod(staticIndexPolygonPoint, staticPolygonPoints.size))
    posteriorPointStatic = staticPolygonPoints(Math.floorMod(staticIndexPolygonPoint + 1, staticPolygonPoints.size))
    previousPointStatic = staticPolygonPoints(Math.floorMod(staticIndexPolygonPoint - 1, staticPolygonPoints.size))
    actualPointM = movingPolygonPoints(Math.floorMod(movingIndexPolygonPoint, movingPolygonPoints.size))
    posteriorPointM = movingPolygonPoints(Math.floorMod(movingIndexPolygonPoint + 1, movingPolygonPoints.size))
    previousPointM = movingPolygonPoints(Math.floorMod(movingIndexPolygonPoint - 1, movingPolygonPoints.size))
  }

  def addTranslation(actualPointM: Point, posteriorPointM: Point, previousPointM: Point): Unit = {
    actualPointM.x += xTranslation
    actualPointM.y += yTranslation
    posteriorPointM.x += xTranslation
    posteriorPointM.y += yTranslation
    previousPointM.x += xTranslation
    previousPointM.y += yTranslation
  }

  def updateActualVectors(): Unit = {
    posteriorVectorStatic = Vector(actualPointStatic, posteriorPointStatic)
    previousVectorStatic = Vector(actualPointStatic, previousPointStatic)
    posteriorVectorMovable = Vector(actualPointM, posteriorPointM)
    previousVectorMovable = Vector(actualPointM, previousPointM)
  }

  def updateConditions(): Unit = {
    firstCondition = posteriorVectorMovable x previousVectorStatic
    secondCondition = previousVectorMovable x posteriorVectorStatic
  }

  def checkIntersection(previousVectorStatic: Vector, previousVectorMovable: Vector, firstCondition: Double, secondCondition: Double): Unit = {
    inside = false
    if (firstCondition < 0) {
      if (secondCondition < 0) {
        if ((previousVectorMovable x previousVectorStatic) >= 0) inside = true
      }
    } else {
      if (secondCondition < 0) inside = true
      else {
        if ((previousVectorMovable x previousVectorStatic) < 0) inside = true
      }
    }
  }

  def subTranslation(actualPointM: Point, posteriorPointM: Point, previousPointM: Point): Unit = {
    actualPointM.x -= xTranslation
    actualPointM.y -= yTranslation
    posteriorPointM.x -= xTranslation
    posteriorPointM.y -= yTranslation
    previousPointM.x -= xTranslation
    previousPointM.y -= yTranslation
  }

  def savePoint(polygonB: Polygon): Unit= {
    pointList += new Point(polygonB.centroid.x + xTranslation, polygonB.centroid.y + yTranslation)
  }

  def insertPoint(polygonB: Polygon): Unit = {
    val point: Point = new Point(polygonB.centroid.x + xTranslation, polygonB.centroid.y + yTranslation)

    if(!pointList.contains(point)) {
      var index = 0
      pointList.indices.foreach(i => {
        val vector: Vector = Vector(pointList(i), pointList(Math.floorMod(i + 1, pointList.size)))

        if(vector.leftOn(point)){
          index = i
        }
      })

      pointList.insert(index, point)
    }
  }


  def moveStaticPolygonVertex(): Unit = {
    numberOfVisited.update(Math.floorMod(staticIndexPolygonPoint, staticPolygonPoints.size), numberOfVisited(Math.floorMod(staticIndexPolygonPoint, staticPolygonPoints.size)) + 1)
    staticIndexPolygonPoint += 1
    staticPolygonPoint = staticPolygonPoints(Math.floorMod(staticIndexPolygonPoint, staticPolygonPoints.size))
  }

  def executeLocusAlgorithm(polygonB: Polygon): Unit = {
    while (staticIndexPolygonPoint <= staticPolygonPoints.size + 1) {

      doStep()

      if (inside) {
        staticIndexPolygonPoint -= 1
        staticPolygonPoint = staticPolygonPoints(Math.floorMod(staticIndexPolygonPoint, staticPolygonPoints.size))
        movingIndexPolygonPoint += 1
        movingPolygonPoint = movingPolygonPoints(Math.floorMod(movingIndexPolygonPoint, movingPolygonPoints.size))
      } else {
        savePoint(polygonB)
        numberOfVisited.update(Math.floorMod(staticIndexPolygonPoint, staticPolygonPoints.size), numberOfVisited(Math.floorMod(staticIndexPolygonPoint, staticPolygonPoints.size)) + 1)
        staticIndexPolygonPoint += 1
        staticPolygonPoint = staticPolygonPoints(Math.floorMod(staticIndexPolygonPoint, staticPolygonPoints.size))
      }

//      println("Punto A Statico: " + previousPointStatic)
//      println("Punto B Statico: " + actualPointStatic)
//      println("Punto C Statico: " + posteriorPointStatic)
//      println("Punto A Movible: " + previousPointM)
//      println("Punto B Movible: " + actualPointM)
//      println("Punto C Movible: " + posteriorPointM)
//      println("AmBm x AsBs: " + firstCondition)
//      println("CmBm x CsBs: " + secondCondition)
//      println(inside)
//      println("LEN STATIC: " + (staticIndexPolygonPoint <= staticPolygonPoints.size + 1))
//      println("index STATIC: " + staticIndexPolygonPoint)
//      println("--------------------------------")
    }

//    println("Numero de visitas en cada vertice: " + numberOfVisited)
  }

  def checkSpecialCases(polygonB: Polygon): Unit = {

    numberOfVisited.indices.foreach(i => {
      if (numberOfVisited(i) > polygonB.points.size) {
        // Here it means that we could have an special case. (all vertices can be placed on that vertex)
        // So we need to check if we inserted all the vertex of the previous static vertex.
        staticPolygonPoint = staticPolygonPoints(Math.floorMod(i + staticPolygonPoints.size - 1, staticPolygonPoints.size))
        staticIndexPolygonPoint = Math.floorMod(i + staticPolygonPoints.size - 1, staticPolygonPoints.size)

        polygonB.points.indices.foreach(i => {

          movingPolygonPoint = polygonB.points(i)
          movingIndexPolygonPoint = i
          doStep()

          if (!inside) {
            insertPoint(polygonB)
          }

        })
      }
    })
  }
}

object LocusAlgorithm {

  private val locusAlgorithm: LocusAlgorithm = new LocusAlgorithm()

  /**
    * Function that makes the locus representation between this polygon and the one as parameter.
    *
    * The implemented algorithm is O(n+m) with n the number of vertices in this polygon and m the number of
    * vertices on the one passed as parameter.
    *
    * The polygon passed as parameter will move onto this polygon.
    *
    * TODO: algorithm explanation.
    *
    * @return a polygon representing the locus polygon.
    */
  def getLocusOfTwoPolygons(polygonA: Polygon, polygonB: Polygon): Polygon = {

    locusAlgorithm.initialize(polygonA, polygonB)

    // Here we find the positions that those two polygons don't overlap.
    locusAlgorithm.updateInitialPositions()

    // If we are here it means that the actual movingPolygonPoint satisfies that they are overlapping.
    // So we save the center as a point of the new polygon.
    locusAlgorithm.savePoint(polygonB)

    // We know move onto the next point of the static one.
    locusAlgorithm.moveStaticPolygonVertex()

    // Now we advance the polygon by the conditions of not being inside.
    locusAlgorithm.executeLocusAlgorithm(polygonB)

    // There exist some rare cases, so we check them if needed.
    locusAlgorithm.checkSpecialCases(polygonB)

    //pointList.foreach(println(_))
    new Polygon(locusAlgorithm.getPointList.toList.distinct)
  }

}
