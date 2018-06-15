package dto.dim2D.input

import play.api.libs.json.{Json, Reads}

case class Input2DMesh(polygons: List[InputPolygon], width: Option[Int], height: Option[Int])

object Input2DMesh {

  implicit val read: Reads[Input2DMesh] = Json.reads[Input2DMesh]

}