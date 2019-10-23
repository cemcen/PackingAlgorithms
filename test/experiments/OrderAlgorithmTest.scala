package experiments

import java.util.NoSuchElementException

import algorithms.Packing2D
import algorithms.packing.{AdvanceFrontPacking, PackingAlgorithm, PiledPacking, RockFallingPacking, SpaceReducePacking}
import dto.dim2D.input.{InputLayer, InputPolygon}
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class OrderAlgorithmTest extends FlatSpec with Matchers {

  val nPolygons: List[Int] = List(10, 20, 30, 50, 100, 200, 500)
  val repetitions: Int = 10

  "Testing order of a polygon packing" should "gravity packing" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/gravity_packing/", "test_7")

    nPolygons.foreach(nPol => {
      for (i <- 1 to repetitions) {
        println("----------------------------------------------")
        println("Repetition: " + i)
        println("nPolygons: " + nPol)
        val packingAlgorithm: PackingAlgorithm = new AdvanceFrontPacking
        val polygons: List[InputPolygon] = List(new InputPolygon("5_10", 5, 100, 10))
        packingAlgorithm.setPackingTechnique(new RockFallingPacking)
        Packing2D.setPackingAlgorithm(packingAlgorithm)
        Packing2D.createMesh(polygons, 100000, 100000, randomShape = false, 36, nPol)
      }
    })

    Experiment.writeToFile()
  }

  it should "packing with layers" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/layer_packing/", "test_7")

    nPolygons.foreach(nPol => {
      for (i <- 1 to repetitions) {
        println("----------------------------------------------")
        println("Repetition: " + i)
        println("nPolygons: " + nPol)
        val packingAlgorithm: PackingAlgorithm = new PiledPacking
        val layers: List[InputLayer] = List(
          new InputLayer(List(new InputPolygon("5_10", 5, 100, 10)), Option(500), Option(36)),
          new InputLayer(List(new InputPolygon("5_10", 5, 100, 10)), Option(500), Option(36))
        )
        packingAlgorithm.setPackingTechnique(new RockFallingPacking)
        Packing2D.setPackingAlgorithm(packingAlgorithm)
        try {
          Packing2D.createMultiLayerMesh(layers, 100000, randomShape = false, nPol/2)
        } catch {
          case x: NoSuchElementException => println("Error")
        }
      }
    })

    Experiment.writeToFile()
  }

  it should "density packing" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/dense_packing/", "test_7")

    nPolygons.foreach(nPol => {
      for (i <- 1 to repetitions) {
        println("----------------------------------------------")
        println("Repetition: " + i)
        println("nPolygons: " + nPol)
        val packingAlgorithm: PackingAlgorithm = new AdvanceFrontPacking
        val polygons: List[InputPolygon] = List(new InputPolygon("5_10", 5, 100, 10))
        packingAlgorithm.setPackingTechnique(new SpaceReducePacking)
        Packing2D.setPackingAlgorithm(packingAlgorithm)
        Packing2D.createMesh(polygons, 100000, 100000, randomShape = false, 36, nPol)
      }
    })

    Experiment.writeToFile()
  }


}
