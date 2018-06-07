package controllers

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, Result}

class PolygonController  @Inject()(components: ControllerComponents)
  extends AbstractController(components) {

  def configure = Action {
    Ok(Json.toJson(""))
  }
}
