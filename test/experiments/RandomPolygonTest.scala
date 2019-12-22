package experiments

import java.util.NoSuchElementException

import algorithms.Packing2D
import algorithms.packing.{AdvanceFrontPacking, PackingAlgorithm, PiledPacking, RockFallingPacking, SpaceReducePacking}
import dto.dim2D.input.{InputLayer, InputPolygon}
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RandomPolygonTest extends FlatSpec with Matchers {

  val widths: List[Double] = List(100.0, 150.0, 200.0, 300.0)
  val heights: List[Double] = List(100.0, 150.0, 200.0, 300.0)
  val repetitions: Int = 10

  "Random polygon packing" should "gravity packing" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/gravity_packing/", "test_6")

    widths.foreach(width => {
      heights.foreach(height => {
        for(i <- 1 to repetitions) {
          println("----------------------------------------------")
          println("Repetition: " + i)
          println("Width: " + width)
          println("Height: " + height)
          val packingAlgorithm: PackingAlgorithm = new AdvanceFrontPacking
          val polygons: List[InputPolygon] = List(new InputPolygon("5_10", 5, 100, 10))
          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
          Packing2D.setPackingAlgorithm(packingAlgorithm)
          Packing2D.createMesh(polygons, width, height, randomShape = true, 36)
        }
      })
    })

    Experiment.writeToFile()
  }

  it should "packing with layers" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/layer_packing/", "test_6")

    widths.foreach(width => {
      heights.foreach(height => {
        for(i <- 1 to repetitions) {
          println("----------------------------------------------")
          println("Repetition: " + i)
          println("Width: " + width)
          println("Height: " + height)
          val packingAlgorithm: PackingAlgorithm = new PiledPacking
          val layers: List[InputLayer] = List(
            new InputLayer(List(new InputPolygon("5_10", 5, 100, 10)), Option(height/2), Option(36)),
            new InputLayer(List(new InputPolygon("5_10", 5, 100, 10)), Option(height/2), Option(36))
          )
          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
          Packing2D.setPackingAlgorithm(packingAlgorithm)
          try {
            Packing2D.createMultiLayerMesh(layers, width, randomShape = true)
          } catch {
            case x: NoSuchElementException => println("Error")
          }
        }
      })
    })

    Experiment.writeToFile()
  }

  it should "density packing" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/dense_packing/", "test_6")

    widths.foreach(width => {
      heights.foreach(height => {
        for(i <- 1 to repetitions) {
          println("----------------------------------------------")
          println("Repetition: " + i)
          println("Width: " + width)
          println("Height: " + height)
          val packingAlgorithm: PackingAlgorithm = new AdvanceFrontPacking
          val polygons: List[InputPolygon] = List(new InputPolygon("5_10", 5, 100, 10))
          packingAlgorithm.setPackingTechnique(new SpaceReducePacking)
          Packing2D.setPackingAlgorithm(packingAlgorithm)
          Packing2D.createMesh(polygons, width, height, randomShape = true, 36)
        }
      })
    })

    Experiment.writeToFile()
  }


}
