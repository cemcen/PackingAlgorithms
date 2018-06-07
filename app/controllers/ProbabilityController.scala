package controllers

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

class ProbabilityController  @Inject()(components: ControllerComponents)
  extends AbstractController(components) {

  def configure = Action {
    Ok(Json.toJson(""))
  }
}
