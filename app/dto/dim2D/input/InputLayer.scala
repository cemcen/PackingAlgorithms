package dto.dim2D.input

import play.api.libs.json.{Json, Reads}

case class InputLayer(polygons: List[InputPolygon], height: Option[Double], regularity: Option[Int])

object InputLayer {

  implicit val read: Reads[InputLayer] = Json.reads[InputLayer]

}