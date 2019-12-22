package dto.dim2D.input

import play.api.libs.json.{Json, Reads}

case class Input2DMultiLayerMesh(layers: List[InputLayer], width: Option[Double],
                       randomShape: Option[Boolean], approachAlgorithm: Option[Int])

object Input2DMultiLayerMesh {

  implicit val read: Reads[Input2DMultiLayerMesh] = Json.reads[Input2DMultiLayerMesh]

}