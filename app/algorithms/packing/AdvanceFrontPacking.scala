package algorithms.packing
import algorithms.geometric.Container
import geometry.Polygon

import scala.collection.mutable.ArrayBuffer

/**
  *
  * This class implements the advance front geometric packing heuristic.
  *
  *
  * This approach uses the minskowski sum to search for the optimal position of the next inserted polygon.
  * In each iteration we save the 'front' polygon, that is to say, the polygons that could have a new polygon
  * as neighbour. When inserting a new polygon we need to update which polygon still are on the 'front'.
  *
  */
class AdvanceFrontPacking extends PackingAlgorithm {

  override var nextPolygon: List[Polygon] = _
  override var finalPolygonPosition: ArrayBuffer[Polygon] = _
  override protected var container: Container = _

  /**
    * Executes the packing algorithm. Classes that extends this abstract class must implement this method.
    */
  override def executeAlgorithm(): Unit = ???

}
