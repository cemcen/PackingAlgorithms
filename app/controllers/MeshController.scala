package controllers

import algorithms.Packing2D
import algorithms.packing.AdvanceFrontPacking
import dto.dim2D.input.Input2DMesh
import dto.dim2D.output.{Output2DMesh, OutputPolygon, Point2D}
import geometry.Polygon
import javax.inject.Inject
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}

import scala.collection.mutable.ArrayBuffer

class MeshController @Inject()(components: ControllerComponents)
  extends AbstractController(components) {

  /**
    * The polygons to be packed.
    * @return geometric packing result mesh.
    */
  def createMesh2D: Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Input2DMesh] match {
      case s: JsSuccess[Input2DMesh] =>
        // Get the json with the data.
        val mesh: Input2DMesh = s.get
        var width, height: Double = 0

        // We store the result in this list.
        var polygonMesh: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()

        // Check if we received the width and length.
        if (mesh.width.isDefined && mesh.height.isDefined) {
          // If defined we only get the dimensions.
          width = mesh.width.get
          height = mesh.height.get
          Packing2D.setPackingAlgorithm(new AdvanceFrontPacking)
          polygonMesh = Packing2D.createMesh(mesh.polygons, width, height)

        } else {
          // TODO: create a box container of the area of all polygons.
        }

        // TODO: Create list of output polygons.
        var polygonOutput: ArrayBuffer[OutputPolygon] = new ArrayBuffer[OutputPolygon]()
        polygonMesh.foreach(pol => {
          var pointList: ArrayBuffer[Point2D] = new ArrayBuffer[Point2D]()

          pol.points.foreach(pnt => {
            pointList += new Point2D(pnt.x, pnt.y)
          })

          polygonOutput += new OutputPolygon(pointList.toList, pol.label, pol.radius)
        })

        val output: Output2DMesh = new Output2DMesh(polygonOutput.toList, width, height)

        Ok(Json.obj("mesh" -> output))
      case _: JsError =>
        BadRequest("Json received in wrong format.")
    }
  }

}
