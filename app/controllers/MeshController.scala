package controllers

import algorithms.Packing2D
import dto.dim2D.input.Input2DMesh
import geometry.Polygon
import javax.inject.Inject
import play.api.libs.json.{JsError, JsSuccess, JsValue}
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

        // We store the result in this list.
        var polygonMesh: ArrayBuffer[Polygon] = new ArrayBuffer[Polygon]()

        // Check if we received the width and length.
        if (mesh.width.isDefined && mesh.height.isDefined) {
          // If defined we only get the dimensions.
          polygonMesh = Packing2D.createMesh(mesh.polygons, mesh.width.get, mesh.height.get)
        } else {
          // TODO: create a box container of the area of all polygons.
        }

        // TODO: Create list of output polygons.

        Ok("TODO: return mesh")
      case _: JsError =>
        BadRequest("Json received in wrong format.")
    }
  }

}
