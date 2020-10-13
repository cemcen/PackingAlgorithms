package experiments

import algorithms.Packing2D
import algorithms.packing.{AdvanceFrontPacking, PackingAlgorithm, RockFallingPacking, SpaceReducePacking}
import dto.dim2D.input.InputPolygon
import geometry.{Polygon, PolygonFactory}
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.specs2.runner.JUnitRunner

import scala.collection.mutable.ArrayBuffer

@RunWith(classOf[JUnitRunner])
class GravityPackingTest extends FlatSpec with Matchers {

  //val widths: List[Double] = List(50.0, 100.0, 150.0, 200.0)
  //val heights: List[Double] = List(50.0, 100.0, 150.0, 200.0)
  //val widths: List[Double] = List(75.0, 150.0, 225.0)
  //val heights: List[Double] = List(75.0, 150.0, 225.0)
//  val widths: List[Double] = List(300.0, 500.0)
//  val heights: List[Double] = List(300.0, 500.0)
  val widths: List[Double] = List(100.0, 150.0, 200.0, 300.0)
  val heights: List[Double] = List(100.0, 150.0, 200.0, 300.0)

  val repetitions: Int = 3

//  "Gravity Packing" should "Test same polygon bigger container" in {
//    Experiment.debugModeOn()
//
//
//    widths.foreach(width => {
//      heights.foreach(height => {
//        for(i <- 1 to repetitions) {
//          Experiment.startNewExperiment("debug/inputs/experiment_1/", "test_" + width.toString + "_" + height.toString + '_' + i.toString, csv = false)
//          println("----------------------------------------------")
//          println("Repetition: " + i)
//          println("Width: " + width)
//          println("Height: " + height)
////          val packingAlgorithm: PackingAlgorithm = new AdvanceFrontPacking
//          val polygons: List[InputPolygon] = List(new InputPolygon("5_10", 5, 100, 10))
//          val polygonsCreated = PolygonFactory.createPolygonArrayInsertion(polygons, height, width, randomFigure = false, 36)
//          polygonsCreated.foreach(pol => {
//            Experiment.addPolygonToFile(pol)
//          })
////          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
////          Packing2D.setPackingAlgorithm(packingAlgorithm)
////          Packing2D.createMesh(polygons, width, height, randomShape = false, 36)
//          Experiment.writeToFile()
//        }
//      })
//    })
//  }
//
//  "Gravity Packing" should "Test two polygon different vertex bigger container" in {
//    Experiment.debugModeOn()
////    Experiment.startNewExperiment("debug/experiments/gravity_packing/", "test_2")
//
//    widths.foreach(width => {
//      heights.foreach(height => {
//        for(i <- 1 to repetitions) {
//          Experiment.startNewExperiment("debug/inputs/experiment_2/", "test_" + width.toString + "_" + height.toString + '_' + i.toString, csv = false)
//          println("----------------------------------------------")
//          println("Repetition: " + i)
//          println("Width: " + width)
//          println("Height: " + height)
////          val packingAlgorithm: PackingAlgorithm = new AdvanceFrontPacking
//          val polygons: List[InputPolygon] = List(
//            new InputPolygon("5_10", 5, 100, 10),
//            new InputPolygon("7_10", 7, 100, 10)
//          )
//          val polygonsCreated = PolygonFactory.createPolygonArrayInsertion(polygons, height, width, randomFigure = false, 36)
//          polygonsCreated.foreach(pol => {
//            Experiment.addPolygonToFile(pol)
//          })
////          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
////          Packing2D.setPackingAlgorithm(packingAlgorithm)
////          Packing2D.createMesh(polygons, width, height, randomShape = false, 36)
//          Experiment.writeToFile()
//        }
//      })
//    })
//
////    Experiment.writeToFile()
//  }
//
//  it should "Test two polygon different size bigger container" in {
//    Experiment.debugModeOn()
////    Experiment.startNewExperiment("debug/experiments/gravity_packing/", "test_3")
//
//    widths.foreach(width => {
//      heights.foreach(height => {
//        for(i <- 1 to repetitions) {
//          Experiment.startNewExperiment("debug/inputs/experiment_3/", "test_" + width.toString + "_" + height.toString + '_' + i.toString, csv = false)
//          println("----------------------------------------------")
//          println("Repetition: " + i)
//          println("Width: " + width)
//          println("Height: " + height)
//          val packingAlgorithm: PackingAlgorithm = new AdvanceFrontPacking
//          val polygons: List[InputPolygon] = List(
//            new InputPolygon("5_10", 5, 100, 10),
//            new InputPolygon("5_20", 5, 100, 20)
//          )
//          val polygonsCreated = PolygonFactory.createPolygonArrayInsertion(polygons, height, width, randomFigure = false, 36)
//          polygonsCreated.foreach(pol => {
//            Experiment.addPolygonToFile(pol)
//          })
////          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
////          Packing2D.setPackingAlgorithm(packingAlgorithm)
////          Packing2D.createMesh(polygons, width, height, randomShape = false, 36)
//          Experiment.writeToFile()
//        }
//      })
//    })
//
////    Experiment.writeToFile()
//  }
//
//  it should "Test almost circle polygons bigger container" in {
//    Experiment.debugModeOn()
////    Experiment.startNewExperiment("debug/experiments/gravity_packing/", "test_4")
//
//    widths.foreach(width => {
//      heights.foreach(height => {
//        for(i <- 1 to repetitions) {
//          Experiment.startNewExperiment("debug/inputs/experiment_4/", "test_" + width.toString + "_" + height.toString + '_' + i.toString, csv = false)
//          println("----------------------------------------------")
//          println("Repetition: " + i)
//          println("Width: " + width)
//          println("Height: " + height)
////          val packingAlgorithm: PackingAlgorithm = new AdvanceFrontPacking
//          val polygons: List[InputPolygon] = List(
//            new InputPolygon("20_10", 20, 100, 10)
//          )
//          val polygonsCreated = PolygonFactory.createPolygonArrayInsertion(polygons, height, width, randomFigure = false, 36)
//          polygonsCreated.foreach(pol => {
//            Experiment.addPolygonToFile(pol)
//          })
////          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
////          Packing2D.setPackingAlgorithm(packingAlgorithm)
////          Packing2D.createMesh(polygons, width, height, randomShape = false, 36)
//          Experiment.writeToFile()
//        }
//      })
//    })
//
////    Experiment.writeToFile()
//  }
//
//  it should "Test almost circle polygons different size bigger container" in {
//    Experiment.debugModeOn()
////    Experiment.startNewExperiment("debug/experiments/gravity_packing/", "test_5")
//
//    widths.foreach(width => {
//      heights.foreach(height => {
//        for(i <- 1 to repetitions) {
//          Experiment.startNewExperiment("debug/inputs/experiment_5/", "test_" + width.toString + "_" + height.toString + '_' + i.toString, csv = false)
//          println("----------------------------------------------")
//          println("Repetition: " + i)
//          println("Width: " + width)
//          println("Height: " + height)
////          val packingAlgorithm: PackingAlgorithm = new AdvanceFrontPacking
//          val polygons: List[InputPolygon] = List(
//            new InputPolygon("20_10", 20, 100, 10),
//            new InputPolygon("20_20", 20, 100, 20)
//          )
//          val polygonsCreated = PolygonFactory.createPolygonArrayInsertion(polygons, height, width, randomFigure = false, 36)
//          polygonsCreated.foreach(pol => {
//            Experiment.addPolygonToFile(pol)
//          })
////          packingAlgorithm.setPackingTechnique(new RockFallingPacking)
////          Packing2D.setPackingAlgorithm(packingAlgorithm)
////          Packing2D.createMesh(polygons, width, height, randomShape = false, 36)
//          Experiment.writeToFile()
//        }
//      })
//    })
//
////    Experiment.writeToFile()
//  }

  "Random polygon packing" should "gravity packing" in {
//    Experiment.debugModeOn()
//    Experiment.startNewExperiment("debug/experiments/gravity_packing/", "test_6")

//    widths.foreach(width => {
//      heights.foreach(height => {
//        for(i <- 1 to repetitions) {
          val width = 200
          val height = 200
          val i = 1
          Experiment.startNewExperiment("debug/inputs/", "test_" + width.toString + "_" + height.toString + '_' + i.toString, csv = false)
          println("----------------------------------------------")
          println("Repetition: " + i)
          println("Width: " + width)
          println("Height: " + height)
          val packingAlgorithm: PackingAlgorithm = new AdvanceFrontPacking
//          val polygons: List[InputPolygon] = List(new InputPolygon("5_10", 5, 100, 10))
//          val polygons: List[InputPolygon] = List(new InputPolygon("5_10", 5, 100, 10))
//val polygons: List[InputPolygon] = List(
//      new InputPolygon("5_10", 5, 100, 10),
//      new InputPolygon("7_10", 7, 100, 10)
//    )
//    val polygons: List[InputPolygon] = List(
//                  new InputPolygon("5_10", 5, 100, 10),
//                  new InputPolygon("5_20", 5, 100, 20)
//                )
//val polygons: List[InputPolygon] = List(
//              new InputPolygon("20_10", 20, 100, 10)
//            )
val polygons: List[InputPolygon] = List(
              new InputPolygon("20_10", 20, 100, 10),
              new InputPolygon("20_20", 20, 100, 20)
            )
    val polygonsCreated = PolygonFactory.createPolygonArrayInsertion(polygons, height, width, randomFigure = false, 36)
    polygonsCreated.foreach(pol => {
      Experiment.addPolygonToFile(pol)
    })
          packingAlgorithm.setPackingTechnique(new RockFallingPacking)

//          Packing2D.setPackingAlgorithm(packingAlgorithm)
//          val polygonsOutput: ArrayBuffer[Polygon] = Packing2D.createMesh(polygons, width, height, randomShape = false, 36)
//          polygonsOutput.foreach(pol => {
//            Experiment.addPolygonToFile(pol)
//          })

          Experiment.writeToFile()
//        }
//      })
//    })

//    Experiment.writeToFile()
  }
}
