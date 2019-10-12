package experiments

import algorithms.Packing2D
import algorithms.packing.{AdvanceFrontPacking, PackingAlgorithm, RockFallingPacking, SpaceReducePacking}
import dto.dim2D.input.InputPolygon
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GravityPackingTest extends FlatSpec with Matchers {

  val widths: List[Double] = List(50.0, 100.0, 150.0, 200.0, 300.0, 500.0, 1000.0)
  val heights: List[Double] = List(50.0, 100.0, 150.0, 200.0, 300.0, 500.0, 1000.0)
  val repetitions: Int = 20

  "Gravity Packing" should "Test same polygon bigger container" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/dense_packing/", "test_same_polygon_bigger_container")

    widths.foreach(width => {
      heights.foreach(height => {
        for(i <- 1 to repetitions) {
          val packingAlgorithm: PackingAlgorithm = new AdvanceFrontPacking
          val polygons: List[InputPolygon] = List(new InputPolygon("5_10", 5, 100, 10))
          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
          Packing2D.setPackingAlgorithm(packingAlgorithm)
          Packing2D.createMesh(polygons, width, height, randomShape = false, 36)
        }
      })
    })

    Experiment.writeToFile()
  }

  it should "Test two polygon different vertex bigger container" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/dense_packing/", "test_same_polygon_bigger_container")

    widths.foreach(width => {
      heights.foreach(height => {
        for(i <- 1 to repetitions) {
          val packingAlgorithm: PackingAlgorithm = new AdvanceFrontPacking
          val polygons: List[InputPolygon] = List(
            new InputPolygon("5_10", 5, 100, 10),
            new InputPolygon("7_10", 7, 100, 10)
          )
          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
          Packing2D.setPackingAlgorithm(packingAlgorithm)
          Packing2D.createMesh(polygons, width, height, randomShape = false, 36)
        }
      })
    })

    Experiment.writeToFile()
  }

  it should "Test two polygon different size bigger container" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/dense_packing/", "test_same_polygon_bigger_container")

    widths.foreach(width => {
      heights.foreach(height => {
        for(i <- 1 to repetitions) {
          val packingAlgorithm: PackingAlgorithm = new AdvanceFrontPacking
          val polygons: List[InputPolygon] = List(
            new InputPolygon("5_10", 5, 100, 10),
            new InputPolygon("5_20", 5, 100, 20)
          )
          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
          Packing2D.setPackingAlgorithm(packingAlgorithm)
          Packing2D.createMesh(polygons, width, height, randomShape = false, 36)
        }
      })
    })

    Experiment.writeToFile()
  }

  it should "Test almost circle polygons bigger container" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/dense_packing/", "test_same_polygon_bigger_container")

    widths.foreach(width => {
      heights.foreach(height => {
        for(i <- 1 to repetitions) {
          val packingAlgorithm: PackingAlgorithm = new AdvanceFrontPacking
          val polygons: List[InputPolygon] = List(
            new InputPolygon("20_10", 20, 100, 10)
          )
          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
          Packing2D.setPackingAlgorithm(packingAlgorithm)
          Packing2D.createMesh(polygons, width, height, randomShape = false, 36)
        }
      })
    })

    Experiment.writeToFile()
  }

  it should "Test almost circle polygons different size bigger container" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/dense_packing/", "test_same_polygon_bigger_container")

    widths.foreach(width => {
      heights.foreach(height => {
        for(i <- 1 to repetitions) {
          val packingAlgorithm: PackingAlgorithm = new AdvanceFrontPacking
          val polygons: List[InputPolygon] = List(
            new InputPolygon("20_10", 20, 100, 10),
            new InputPolygon("20_20", 20, 100, 20)
          )
          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
          Packing2D.setPackingAlgorithm(packingAlgorithm)
          Packing2D.createMesh(polygons, width, height, randomShape = false, 36)
        }
      })
    })

    Experiment.writeToFile()
  }
}
