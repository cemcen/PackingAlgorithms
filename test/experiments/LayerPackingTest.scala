package experiments

import java.util.NoSuchElementException

import algorithms.Packing2D
import algorithms.packing.{AdvanceFrontPacking, PackingAlgorithm, PiledPacking, RockFallingPacking, SpaceReducePacking}
import dto.dim2D.input.{InputLayer, InputPolygon}
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class LayerPackingTest extends FlatSpec with Matchers {

  val widths: List[Double] = List(100.0, 150.0, 200.0, 300.0)
  val heights: List[Double] = List(100.0, 150.0, 200.0, 300.0)
  //val widths: List[Double] = List(75.0, 150.0, 225.0)
  //val heights: List[Double] = List(75.0, 150.0, 225.0)
  //val widths: List[Double] = List(300.0, 500.0)
  //val heights: List[Double] = List(300.0, 500.0)

  val repetitions: Int = 10

  "Layer Packing" should "Test same polygon bigger container" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/layer_packing/", "test_1")

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
            Packing2D.createMultiLayerMesh(layers, width, randomShape = false)
          } catch {
            case x: NoSuchElementException => println("Error")
          }
        }
      })
    })

    Experiment.writeToFile()
  }

  it should "Test two polygon different vertex bigger container" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/layer_packing/", "test_2")

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
            new InputLayer(List(new InputPolygon("7_10", 7, 100, 10)), Option(height/2), Option(36))
          )
          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
          Packing2D.setPackingAlgorithm(packingAlgorithm)
          try {
            Packing2D.createMultiLayerMesh(layers, width, randomShape = false)
          } catch {
            case x: NoSuchElementException => println("Error")
          }
        }
      })
    })

    Experiment.writeToFile()
  }

  it should "Test two polygon different size bigger container" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/layer_packing/", "test_3")

    widths.foreach(width => {
      heights.foreach(height => {
        for(i <- 1 to repetitions) {
          println("----------------------------------------------")
          println("Repetition: " + i)
          println("Width: " + width)
          println("Height: " + height)
          val packingAlgorithm: PackingAlgorithm = new PiledPacking
          val layers: List[InputLayer] = List(
            new InputLayer(List(new InputPolygon("5_20", 5, 100, 20)), Option(height/2), Option(36)),
            new InputLayer(List(new InputPolygon("5_10", 5, 100, 10)), Option(height/2), Option(36))
          )
          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
          Packing2D.setPackingAlgorithm(packingAlgorithm)
          try {
            Packing2D.createMultiLayerMesh(layers, width, randomShape = false)
          } catch {
            case x: NoSuchElementException => println("Error")
          }
        }
      })
    })

    Experiment.writeToFile()
  }

  it should "Test almost circle polygons bigger container" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/layer_packing/", "test_4")

    widths.foreach(width => {
      heights.foreach(height => {
        for(i <- 1 to repetitions) {
          println("----------------------------------------------")
          println("Repetition: " + i)
          println("Width: " + width)
          println("Height: " + height)
          val packingAlgorithm: PackingAlgorithm = new PiledPacking
          val layers: List[InputLayer] = List(
            new InputLayer(List(new InputPolygon("20_10", 20, 100, 10)), Option(height/2), Option(36)),
            new InputLayer(List(new InputPolygon("20_10", 20, 100, 10)), Option(height/2), Option(36))
          )
          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
          Packing2D.setPackingAlgorithm(packingAlgorithm)
          try {
            Packing2D.createMultiLayerMesh(layers, width, randomShape = false)
          } catch {
            case x: NoSuchElementException => println("Error")
          }
        }
      })
    })

    Experiment.writeToFile()
  }

  it should "Test almost circle polygons different size bigger container" in {
    Experiment.debugModeOn()
    Experiment.startNewExperiment("debug/experiments/layer_packing/", "test_5")

    widths.foreach(width => {
      heights.foreach(height => {
        for(i <- 1 to repetitions) {
          println("----------------------------------------------")
          println("Repetition: " + i)
          println("Width: " + width)
          println("Height: " + height)
          val packingAlgorithm: PackingAlgorithm = new PiledPacking
          val layers: List[InputLayer] = List(
            new InputLayer(List(new InputPolygon("20_20", 20, 100, 10)), Option(height/2), Option(36)),
            new InputLayer(List(new InputPolygon("20_10", 20, 100, 20)), Option(height/2), Option(36))
          )
          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
          Packing2D.setPackingAlgorithm(packingAlgorithm)
          try {
            Packing2D.createMultiLayerMesh(layers, width, randomShape = false)
          } catch {
            case x: NoSuchElementException => println("Error")
          }
        }
      })
    })

    Experiment.writeToFile()
  }
}
