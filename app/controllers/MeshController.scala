package controllers

import dto.dim2D.input.Input2DMesh
import dto.dim2D.output.Output2DMesh
import javax.inject.Inject
import play.api.libs.json.{JsError, JsSuccess, JsValue}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

class MeshController @Inject()(components: ControllerComponents)
  extends AbstractController(components) {

  /**
    * The polygons to be packed.
    * @return geometric packing result mesh.
    */
  def createMesh2D: Action[JsValue] = Action(parse.json) { request =>
    request.body.validate[Input2DMesh] match {
      case s: JsSuccess[Input2DMesh] =>
        val mesh: Input2DMesh = s.get
        Ok("TODO: return mesh")
      case _: JsError =>
        BadRequest("Json received in wrong format.")
    }
  }

}
