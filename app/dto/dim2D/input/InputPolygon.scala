package dto.dim2D.input

import play.api.libs.json.{Json, Reads}

case class InputPolygon(label: String, numberOfVertex: Int, percentage: Int, radius: Float)

object InputPolygon {

  implicit val read: Reads[InputPolygon] = Json.reads[InputPolygon]

}