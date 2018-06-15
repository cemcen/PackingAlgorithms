package dto.dim2D.output

import play.api.libs.json.{JsObject, Json, Reads, Writes}

case class Output2DMesh(polygons: List[OutputPolygon], width: Int, height: Int)

object Output2DMesh {

  implicit object Output2DMeshWrites extends Writes[Output2DMesh] {

    def writes(u: Output2DMesh): JsObject = Json.obj(
      "polygons" -> u.polygons,
      "width" -> u.width,
      "height" -> u.height
    )
  }

  implicit val read: Reads[Output2DMesh] = Json.reads[Output2DMesh]
  implicit val write: Writes[Output2DMesh] = Json.writes[Output2DMesh]
}